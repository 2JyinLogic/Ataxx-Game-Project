package ataxx;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.concurrent.LinkedBlockingQueue;


// Optional Task: The GUI for the Ataxx Game

class GUI implements View, CommandSource, Reporter {

    /**
     * Width of chess piece
     */
    private final static int CHESS_WIDTH = 50;
    /**
     * The size of the inside border of a chess piece
     */
    private final static int CHESS_PADDING = 5;
    /**
     * The width of the board, counted by the number of pieces
     */
    private final static int BOARD_WIDTH = 7;

    private final JFrame frame;
    private final JPanel drawPanel;
    private final JLabel score;

    // Checkerboard object
    private Board board;

    // Whether to place a BLOCK
    private boolean isPlaceBlock = true;

    // Task list, used to publish task generation commands
    private final LinkedBlockingQueue<Point> taskQueue = new LinkedBlockingQueue<>();
    // Common command
    private static final Point RESTART = new Point();
    private static final Point PLAYER = new Point();
    private static final Point BLOCK = new Point();

    // Complete the codes here
    GUI(String ataxx) {
        frame = new JFrame(ataxx); //Construction method
        frame.setLayout(new BorderLayout()); // Form layout

        // Top bar
        JPanel topPanel = new JPanel();
        JButton restart = new JButton("New");
        restart.addActionListener(event -> taskQueue.add(RESTART));
        score = new JLabel("Score");
        topPanel.add(restart);
        topPanel.add(score);

        frame.add(topPanel, BorderLayout.NORTH);

        // Lower toolbar
        JPanel bottomPanel = new JPanel();
        // Red drop-down box
        JLabel redLabel = new JLabel("Red");
        JComboBox<String> red = new JComboBox<>();
        red.addItem("Manual");
        red.addItem("AI");
        bottomPanel.add(redLabel);
        bottomPanel.add(red);
        // Blue drop-down box
        JLabel blueLabel = new JLabel("Blue");
        JComboBox<String> blue = new JComboBox<>();
        blue.addItem("AI");
        blue.addItem("Manual");
        bottomPanel.add(blueLabel);
        bottomPanel.add(blue);
        // Click event
        ActionListener listener = event -> {
            int player;
            // Is the player manual
            if ("AI".equals(((JComboBox<?>) event.getSource()).getSelectedItem())) {
                player = 0;
            } else {
                player = 1;
            }
            // Is red
            int color = event.getSource() == red ? 1 : 0;
            // Save changes
            PLAYER.move(player, color);
            // Add command
            taskQueue.add(PLAYER);
        };
        // Add listener
        red.addActionListener(listener);
        blue.addActionListener(listener);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Add drawing panel
        drawPanel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                repaintChessBoard(g);
            }
        };
        drawPanel.setPreferredSize(new Dimension(BOARD_WIDTH * CHESS_WIDTH, BOARD_WIDTH * CHESS_WIDTH));

        // Add a listening event for the checkerboard
        drawPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    // Calculate which square the mouse clicks on
                    int row = e.getY() / CHESS_WIDTH;
                    int col = e.getX() / CHESS_WIDTH;

                    if (isPlaceBlock) {
                        // Clicking on another piece is the end of placing the Block
                        String cmd = getCommand(row, col);
                        PieceState state = board.getContent(cmd.charAt(0), cmd.charAt(1));
                        if (state == PieceState.EMPTY) {
                            board.setBlock(cmd.charAt(0), cmd.charAt(1));
                            return;
                        }
                        if (state == PieceState.BLOCKED) {
                            return;
                        }
                        // End place block
                        isPlaceBlock = false;
                    }

                    // Callback checkerboard click event
                    taskQueue.add(new Point(row, col));
                }
            }
        });

        frame.add(drawPanel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
    }

    private void paintStar(Graphics2D g2d, int x, int y, int w, int h) {
        //The coordinates of the 10 vertices
        //Note: Here are the X-axis to the right and Y-axis up axes at center origin, with a standard radius of 1
        Point2D.Double pA = new Point2D.Double(0, 1);
        Point2D.Double pB = new Point2D.Double(-0.95106, 0.30902);
        Point2D.Double pC = new Point2D.Double(-0.58779, -0.80902);
        Point2D.Double pD = new Point2D.Double(0.58779, -0.80902);
        Point2D.Double pE = new Point2D.Double(0.95106, 0.30902);
        Point2D.Double pF = new Point2D.Double(0, -0.38197);
        Point2D.Double pG = new Point2D.Double(0.36328, -0.11804);
        Point2D.Double pH = new Point2D.Double(0.22452, 0.30902);
        Point2D.Double pI = new Point2D.Double(-0.22452, 0.30902);
        Point2D.Double pJ = new Point2D.Double(-0.36328, -0.11804);

        Point2D.Double[] points = {pA, pI, pB, pJ, pC, pF, pD, pG, pE, pH};

        //Coordinate transformation (specify the target square, draw a five-pointed star inside)
        Rectangle rect = new Rectangle(x, y, w, h);
        int radius_x = rect.width / 2;
        int radius_y = rect.height / 2;

        for (Point2D.Double point : points) {
            point.x = rect.getCenterX() + radius_x * point.x;
            point.y = rect.getCenterY() - radius_y * point.y;  //Coordinate reversal
        }

        //Round five-pointed star
        Path2D outline = new Path2D.Double();
        outline.moveTo(points[0].x, points[0].y);
        for (int i = 1; i < points.length; i++) {
            outline.lineTo(points[i].x, points[i].y);
        }
        outline.closePath();

        g2d.setPaint(Color.GRAY);
        g2d.fill(outline);
    }

    // Redraw the board
    private void repaintChessBoard(Graphics g) {
        // Get a brush
        Graphics2D g2 = (Graphics2D) g;
        if (g2 == null || board == null) {
            return;
        }
        // Draw background
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, BOARD_WIDTH * CHESS_WIDTH, BOARD_WIDTH * CHESS_WIDTH);
        // Drawing grid
        g2.setColor(Color.BLACK);
        int begin = 0, end = BOARD_WIDTH * CHESS_WIDTH;
        for (int i = 0; i <= BOARD_WIDTH; i++) {
            int step = i * CHESS_WIDTH;
            g2.drawLine(begin, step, end, step);
            g2.drawLine(step, begin, step, end);
        }
        // Draw selected piece
        if (chess != null) {
            g2.setColor(Color.CYAN);
            int y = (int) chess.getX() * CHESS_WIDTH;
            int x = (int) chess.getY() * CHESS_WIDTH;
            g2.fillRect(x, y, CHESS_WIDTH, CHESS_WIDTH);
        }
        // Draw a piece
        int padding = CHESS_PADDING << 1;
        for (char r = '7'; r >= '1'; r -= 1) {
            for (char c = 'a'; c <= 'g'; c += 1) {
                // Draw a circle
                PieceState state = board.getContent(c, r);
                // Draw five stars
                if (state == PieceState.BLOCKED) {
                    paintStar(
                            g2,
                            (c - 'a') * CHESS_WIDTH + CHESS_PADDING,
                            ('7' - r) * CHESS_WIDTH + CHESS_PADDING,
                            CHESS_WIDTH - padding,
                            CHESS_WIDTH - padding);
                }
                // Draw red pieces
                if (state == PieceState.RED) {
                    g2.setColor(Color.RED);
                    g2.fillOval(
                            (c - 'a') * CHESS_WIDTH + CHESS_PADDING,
                            ('7' - r) * CHESS_WIDTH + CHESS_PADDING,
                            CHESS_WIDTH - padding,
                            CHESS_WIDTH - padding);
                }
                // Draw a blue piece
                if (state == PieceState.BLUE) {
                    g2.setColor(Color.BLUE);
                    g2.fillOval(
                            (c - 'a') * CHESS_WIDTH + CHESS_PADDING,
                            ('7' - r) * CHESS_WIDTH + CHESS_PADDING,
                            CHESS_WIDTH - padding,
                            CHESS_WIDTH - padding);
                }
            }
        }
    }

    @Override
    public void update(Board board) {
        if (this.board == null) {
            this.board = board;
        }
        // Refresh score
        String score = board.getScore();
        this.score.setText(score);
        // Redraw the board
        drawPanel.repaint();
    }

    private String getCommand(Point pt) {
        return getCommand((int) pt.getX(), (int) pt.getY());
    }

    private String getCommand(int row, int col) {
        char[] chars = new char[2];
        chars[1] = "7654321".charAt(row);
        chars[0] = "abcdefg".charAt(col);

        return new String(chars);
    }

    private Point chess = null;

    @Override
    public String getCommand(String prompt) {
        // Block wait input
        try {
            while (true) {
                Point point = taskQueue.take();

                // Replay or not
                if (point == RESTART) {
                    chess = null;
                    isPlaceBlock = true;
                    return "new";
                }
                // Whether to set a role
                if (point == PLAYER) {
                    chess = null;
                    return (PLAYER.getX() == 1 ? "manual " : "ai ") + (PLAYER.getY() == 1 ? "red" : "blue");
                }
                // Set up chess pieces
                if (chess == null) {
                    chess = point;
                    drawPanel.repaint();
                } else {
                    String cmd = getCommand(chess) + "-" + getCommand(point);
                    chess = null;
                    drawPanel.repaint();
                    return cmd;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void announceWinner(PieceState state) {
        score.setText("Game over: " + String.format("%s wins!", state.toString()));
    }

    @Override
    public void announceMove(Move move, PieceState player) {
        System.out.println(move + ", " + player);
    }

    @Override
    public void message(String format, Object... args) {
        System.out.println("aaa" + String.format(format, args));
    }

    @Override
    public void error(String format, Object... args) {
        System.err.println(String.format(format, args));
    }

    public void setVisible(boolean b) {
        frame.setVisible(b);
    }

    public void pack() {

    }

}
