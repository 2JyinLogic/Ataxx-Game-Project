package ataxx;

import org.junit.Test;

import static ataxx.PieceState.*;
import static org.junit.Assert.assertEquals;

public class WinnerTest {

    private static void createMoves(Board b, String[] moves) {
        for (String s : moves) {
            b.createMove(s.charAt(0), s.charAt(1),
                    s.charAt(3), s.charAt(4));
        }
    }


    @Test
    public void testEmptyWinner() {
        Board b = new Board();
        assertEquals(null, b.getWinner());
    }

    @Test
    public void testWinner_couldMove() {
        Board b = new Board();
        String[] moves = {
                "a7-a6", "a1-b1",
                "g1-f1", "g7-f6",
                "a6-a5", "a1-a2",
                "g1-g2", "b1-b2"
        };
        createMoves(b, moves);
        assertEquals(null, b.getWinner());
    }

    @Test
    public void testWinner_couldNotMove_RED() {
        Board b = new Board();
        String[] moves = {
                "a7-a6", "a1-b2",
                "g1-f2", "g7-f7",
                "a6-a5", "a1-a2",
                "g1-f1", "g7-g6",
                "a5-a4", "a1-b1",
                "f2-e3", "f7-e7",
                "a7-b7", "b1-c1",
                "f1-e1", "g6-f6",
                "b7-c7", "b2-c2",
                "f2-e2", "f6-e6",
                "a6-b6", "c2-c3",
                "f2-f3", "g6-g5",
                "b6-c6", "g5-f5",
                "g1-g2", "f5-e5",
                "a5-b5", "b2-b3",
                "f3-f4", "e6-d5",
                "b5-b4", "c1-d1",
                "g2-g3", "d5-d6",
                "a4-a3", "d6-d7",
                "c3-d2", "d5-d4",
                "b5-c5", "e5-e4",
                "g3-g4", "d4-d3",
                "c5-c4"
        };
        createMoves(b, moves);
        assertEquals(RED, b.getWinner());
    }


    // Hidden test 1: Testing a board where blue wins
//    @Test
//    public void testWinner_couldNotMove_BLUE() {
//        Board b = new Board();
//        String[] moves = {
//                "a7-b6", "a1-a2",
//                "g1-f1", "b6-c5",
//                "f1-g1", "c5-b4",
//                "g1-h1", "b4-a3",
//                "h1-h2"
//        };
//        createMoves(b, moves);
//        assertEquals(BLUE, b.getWinner());
//    }

    // Hidden test 2: Testing a board where both colors have no moves
    @Test
    public void testWinner_noMoves() {
        Board b = new Board();
        String[] moves = {
                "a7-a6", "a1-b1",
                "g1-f1", "g7-f6",
                "a6-a5", "a1-a2",
                "g1-g2", "b1-b2",
                "f1-e1", "g7-g6",
                "a5-a4", "a1-b1",
                "f2-e3", "f7-e7",
                "a7-b7", "b1-c1",
                "f1-f2", "g6-f6",
                "b7-c7", "b2-c2",
                "f2-e2", "f6-e6",
                "a6-b6", "c2-c3",
                "f2-f3", "g6-g5",
                "b6-c6", "g5-f5",
                "g1-g2", "f5-e5",
                "a5-b5", "b2-b3",
                "f3-f4", "e6-d5",
                "b5-b4", "c1-d1",
                "g2-g3", "d5-d6",
                "a4-a3", "d6-d7",
                "c3-d2", "d5-d4",
                "b5-c5", "e5-e4",
                "g3-g4", "d4-d3",
                "c5-c4"
        };
        createMoves(b, moves);
        assertEquals(null, b.getWinner());
    }

    // Hidden test 3: Testing a board with alternating moves and captures
    @Test
    public void testWinner_alternatingMoves() {
        Board b = new Board();
        String[] moves = {
                "a7-a6", "a1-b1",
                "g1-f1", "b6-a5",
                "f1-g1", "a5-b4",
                "g1-h1", "b4-c3",
                "h1-h2", "c3-d2",
                "h2-h1", "d2-e1",
                "h1-h2", "e1-f1",
                "h2-h1", "f1-g1",
                "h1-h2", "g1-g2",
                "h2-h1", "g2-h2"
        };
        createMoves(b, moves);
        assertEquals(null, b.getWinner());

    }
}
