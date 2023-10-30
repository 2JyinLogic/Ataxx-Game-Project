package ataxx;

import org.junit.Test;
import static org.junit.Assert.*;

public class MoveTest {


    @Test
    public void testIsCloneTrue(){
        assertEquals(Boolean.valueOf("true"), Move.isClone("c1","d2"));
        assertEquals(Boolean.valueOf("true"), Move.isClone("b1","b2"));
    }

    @Test
    public void testIsCloneFalse(){
        assertEquals(Boolean.valueOf("false"), Move.isClone("c1","c1"));
        assertEquals(Boolean.valueOf("false"), Move.isClone("c1","c3"));
    }


    @Test
    public void testIsJumpTrue(){
        assertEquals(Boolean.valueOf("true"), Move.isJump("c1","d3"));
        assertEquals(Boolean.valueOf("true"), Move.isJump("c1","c3"));
    }

    @Test
    public void testIsJumpFalse(){
        assertEquals(Boolean.valueOf("false"), Move.isJump("c4","d3"));
        assertEquals(Boolean.valueOf("false"), Move.isJump("b2","b5"));
    }

    // Hidden test 6: Testing the isClone method with more false cases
    @Test
    public void testIsCloneFalseAdditional() {
        assertEquals(false, Move.isClone("a2", "a2"));
        assertEquals(false, Move.isClone("d5", "f5"));
    }

    // Hidden test 7: Testing the isJump method with more true cases
    @Test
    public void testIsJumpTrueAdditional() {
        assertEquals(true, Move.isJump("a3", "c3"));
        assertEquals(true, Move.isJump("d6", "f6"));
    }

    // Hidden test 8: Testing the isJump method with more false cases
    @Test
    public void testIsJumpFalseAdditional() {
        assertEquals(false, Move.isJump("a3", "b4"));
        assertEquals(false, Move.isJump("d6", "e7"));
    }
}
