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
}
