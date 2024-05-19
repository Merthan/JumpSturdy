package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ToolsTest {

    @Test
    void testCleanMove() {
        // Test cases with various move strings
        assertEquals("B3-B4",Tools.cleanMove("b3b4"));
        assertEquals("B3-B4",Tools.cleanMove("b3-b4"));
        assertEquals("B3-B4",Tools.cleanMove("B3-b4"));
        assertEquals("B3-B6",Tools.cleanMove("b36"));
        assertEquals("A1-B2",Tools.cleanMove("a1b2"));
        assertEquals("H8-C5",Tools.cleanMove("h8c5"));
        assertEquals("G2-F4",Tools.cleanMove("g2-f4"));
        assertEquals("E3-D5",Tools.cleanMove("e3 d5")); // Test with whitespace
        assertEquals("C8-F6",Tools.cleanMove("C8-F6")); // Test with uppercase input
        assertEquals("D4-D5",Tools.cleanMove("D4-D5")); // Test with no change needed
        assertEquals("A3-A2", Tools.cleanMove("A32")); // Test with a move ending with a number
    }

    @Test
    void testParseMove(){
        assertArrayEquals(Tools.parseMove("B7-B6"),new byte[]{9,17});
    }

    @Test
    void testFenToString(){// "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0";
        JumpSturdyBoard jumpSturdyBoard = new JumpSturdyBoard("2bb3/5b02/1bb1bb2b0b0/2br3rb1/2b0r04/5r0rr1/2rr2r02/3r02");
        BitBoard bitBoard = new BitBoard();
        bitBoard.readBoard(jumpSturdyBoard.board);
        System.out.println(bitBoard);
        System.out.println(Tools.fenToString("2bb3/5b02/1bb1bb2b0b0/2b03b01/2b0r04/5r0rr1/2rr2r02/3r02"));
    }
}
