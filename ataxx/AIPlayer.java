
// NEW VERSION V2


package ataxx;

import java.util.ArrayList;
import java.util.Collections;
import static ataxx.Player.*;

// Final Project Part A.2 Ataxx AI Player (A group project)

/** A Player that computes its own moves. */
class AIPlayer extends Player {


    // Weights for different factors in the evaluation function
    private static final int CAPTURE_WEIGHT = 10;
    private static final int DISTANCE_PENALTY_WEIGHT = 2;
    private static final int CONTROL_WEIGHT = 5;
    private static final int POTENTIAL_MOVES_WEIGHT = 3;

    /** A new AIPlayer for GAME that will play MYCOLOR.
     *  SEED is used to initialize a random-number generator,
     *  increase the value of SEED would make the AIPlayer move automatically.
     *  Identical seeds produce identical behaviour. */
    AIPlayer(Game game, PieceState myColor, long seed) {
        super(game, myColor);
    }

    @Override
    boolean isAuto() {
        return true;
    }

    @Override
    String getAtaxxMove() {
        Move move = findMove();
        getAtaxxGame().reportMove(move, getMyState());
        return move.toString();
    }


    class ScoredMove {
        Move move;
        int score;

        ScoredMove(Move move, int score) {
            this.move = move;
            this.score = score;
        }
    }
    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(getAtaxxBoard());
        lastFoundMove = null;

        ArrayList<Move> listOfMoves = possibleMoves(b, b.nextMove());
        ArrayList<ScoredMove> scoredMoves = new ArrayList<>();

        for (Move move : listOfMoves) {
            int score = evaluateMove(b, move);
            scoredMoves.add(new ScoredMove(move, score));
        }

        // Shuffle the list of scored moves to add randomness
        Collections.shuffle(scoredMoves);

        int bestScore = Integer.MIN_VALUE;
        ArrayList<Move> bestMoves = new ArrayList<>();

        for (ScoredMove scoredMove : scoredMoves) {
            if (scoredMove.score > bestScore) {
                bestScore = scoredMove.score;
                bestMoves.clear();
                bestMoves.add(scoredMove.move);
            } else if (scoredMove.score == bestScore) {
                bestMoves.add(scoredMove.move);
            }
        }

        // Randomly select a move from the best-scoring moves
        int randomIndex = (int) (Math.random() * bestMoves.size());
        lastFoundMove = bestMoves.get(randomIndex);

        return lastFoundMove != null ? lastFoundMove : Move.pass();
    }


    /**
     * Evaluates a move's value based on the current board state.
     * Factors include captured pieces, distance penalty, control score, and potential moves score.
     * @param board the current board
     * @param move the move being evaluated
     * @return the calculated score for the move
     */
    private int evaluateMove(Board board, Move move) {
        int capturedPieces = 0;
        Board tempBoard = new Board(board);
        tempBoard.createMove(move);

        int fromIndex = Board.index(move.col0(), move.row0());
        int toIndex = Board.index(move.col1(), move.row1());

        // Calculate the number of captured pieces
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr != 0 || dc != 0) {
                    int neighborIndex = toIndex + 7 * dr + dc;
                    if (tempBoard.getContent(neighborIndex) == getMyState().opposite()) {
                        capturedPieces++;
                    }
                }
            }
        }

        // Calculate the distance penalty
        int distance = Math.abs(move.col1() - move.col0()) + Math.abs(move.row1() - move.row0());
        int distancePenalty = distance == 1 ? 0 : 1;

        // Calculate the control and potential moves scores
        int controlScore = controlScore(tempBoard, getMyState());
        int potentialMovesScore = potentialMovesScore(tempBoard, getMyState());

        // Combine factors with their respective weights to calculate the final score
        return CAPTURE_WEIGHT * capturedPieces
                - DISTANCE_PENALTY_WEIGHT * distancePenalty
                + CONTROL_WEIGHT * controlScore
                + POTENTIAL_MOVES_WEIGHT * potentialMovesScore;
    }

    /**
     * Calculate the control score by comparing the number of pieces for the current player and the opponent.
     * @param board the current board
     * @param myColor the current player's color
     * @return the control score
     */
    private int controlScore(Board board, PieceState myColor) {
        int myPieces = 0;
        int opponentPieces = 0;

        for (char row = '7'; row >= '1'; row--) {
            for (char col = 'a'; col <= 'g'; col++) {
                int index = Board.index(col, row);
                PieceState content = board.getContent(index);

                if (content == myColor) {
                    myPieces++;
                } else if (content == myColor.opposite()) {
                    opponentPieces++;
                }
            }
        }

        return myPieces - opponentPieces;
    }

    private int potentialMovesScore(Board board, PieceState myColor) {
        int myPotentialMoves = possibleMoves(board, myColor).size();
        int opponentPotentialMoves = possibleMoves(board, myColor.opposite()).size();

        return myPotentialMoves - opponentPotentialMoves;
    }

    private Move lastFoundMove;


    /** Return all possible moves for a color.
     * @param board the current board.
     * @param myColor the specified color.
     * @return an ArrayList of all possible moves for the specified color. */
    private ArrayList<Move> possibleMoves(Board board, PieceState myColor) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        for (char row = '7'; row >= '1'; row--) {
            for (char col = 'a'; col <= 'g'; col++) {
                int index = Board.index(col, row);
                if (board.getContent(index) == myColor) {
                    ArrayList<Move> addMoves
                            = assistPossibleMoves(board, row, col);
                    possibleMoves.addAll(addMoves);
                }
            }
        }
        return possibleMoves;
    }

    /** Returns an Arraylist of legal moves.
     * @param board the board for testing
     * @param row the row coordinate of the center
     * @param col the col coordinate of the center */
    private ArrayList<Move>
    assistPossibleMoves(Board board, char row, char col) {
        ArrayList<Move> assistPossibleMoves = new ArrayList<>();
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                if (i != 0 || j != 0) {
                    char row2 = (char) (row + j);
                    char col2 = (char) (col + i);
                    Move currMove = Move.move(col, row, col2, row2);
                    if (board.moveLegal(currMove)) {
                        assistPossibleMoves.add(currMove);
                    }
                }
            }
        }
        return assistPossibleMoves;
    }
}

