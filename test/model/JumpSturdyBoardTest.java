package model;

import deprecated.JumpSturdyBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JumpSturdyBoardTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void coordinatesTest() {
        String position = "F7-F6";
        int[] expected = new int[]{ 1,5,  2,5};
        assertArrayEquals(JumpSturdyBoard.coordinatesFromMove(position),expected);
        position = "B1-G8";
        expected = new int[]{ 7,1,  0,6};
        assertArrayEquals(JumpSturdyBoard.coordinatesFromMove(position),expected);
    }


}