package model;

import static model.JumpSturdyBoard.*;

public class BitBoard {
    private static final int BOARD_WIDTH = 8;
    private static final int BOARD_HEIGHT = 8;

    // Bitboards for the game pieces
    public long redSingles;   // Bitboard for red single pieces
    public long blueSingles;  // Bitboard for blue single pieces
    public long redDoubles;   // Bitboard for red double pieces (top knights)
    public long blueDoubles;  // Bitboard for blue double pieces (top knights)

    public long red_on_blue;   // Bitboard for red double pieces (top knights)
    public long blue_on_red;  // Bitboard for blue double pieces (top knights)


    // Masks for edges to handle movements correctly
    private static final long NOT_A_FILE = 0xfefefefefefefefeL; // 11111110...
    private static final long NOT_H_FILE = 0x7f7f7f7f7f7f7f7fL; // 01111111...

    // Directions for single figures based on team
    private static final int RED_DIRECTION = -8;   // Red moves up
    private static final int BLUE_DIRECTION = 8;   // Blue moves down

    // Constructor to initialize the game board
    public BitBoard() {
        setupInitialBoard();
    }

    // Setup the initial board with pieces in their starting positions
    private void setupInitialBoard() {
        // Initialize red and blue singles on their respective rows
        //redSingles = (0xFFL << (8 * 2)) | (0xFFL << (8 * 3));
        //blueSingles = (0xFFL << (8 * 5)) | (0xFFL << (8 * 6));

        redSingles = 0L;
        blueSingles = 0L;

        // Doubles are initially empty
        redDoubles = 0L;
        blueDoubles = 0L;
        red_on_blue=0L;
        blue_on_red=0L;
    }

    // Method to read a 2D array and convert it into a bitboard
    public void readBoard(int[][] board) {
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                int piece = board[row][col];
                int index = row * BOARD_WIDTH + col;
                switch (piece) {
                    case EMPTY:
                        // Do nothing
                        break;
                    case CORNER:
                        // Do nothing
                        break;
                    case RED_ON_RED:
                        redDoubles |= 1L << index;
                        break;
                    case RED_ON_BLUE:
                        red_on_blue |= 1L << index;
                        break;
                    case BLUE_ON_BLUE:
                        blueDoubles |= 1L << index;
                        break;
                    case BLUE_ON_RED:
                        blue_on_red |= 1L << index;
                        break;
                    case RED://TODO: For some reason red and blue are swapped here, not sure why
                        blueSingles |= 1L << index;
                        break;
                    case BLUE:
                        redSingles |= 1L << index;
                        break;
                }
            }
        }
    }

    // String representation of the board for debugging
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                int index = row * BOARD_WIDTH + col;
                if ((redSingles & (1L << index)) != 0) {
                    sb.append("r ");
                } else if ((blueSingles & (1L << index)) != 0) {
                    sb.append("b ");
                } else if ((redDoubles & (1L << index)) != 0) {
                    sb.append("R ");
                } else if ((blueDoubles & (1L << index)) != 0) {
                    sb.append("B ");
                } else if ((blue_on_red & (1L << index)) != 0) {
                    sb.append("X ");
                } else if ((red_on_blue & (1L << index)) != 0) {
                    sb.append("Y ");
                } else {
                    sb.append(". ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // Method to make a move for a single piece
    // `from` and `to` should be the indices of the squares (0 to 63)
    public void moveSingle(int from, int to, boolean isRed) {
        if (isRed) {
            redSingles = movePiece(redSingles, from, to);
        } else {
            blueSingles = movePiece(blueSingles, from, to);
        }
    }

    // Helper method to move a piece on a bitboard
    private long movePiece(long bitboard, int from, int to) {
        // Remove the piece from the 'from' position
        bitboard &= ~(1L << from);
        // Add the piece to the 'to' position
        bitboard |= 1L << to;
        return bitboard;
    }

    // Method to check if a team has won by reaching the opposite end of the board
    public boolean checkWinCondition(boolean isRed) {
        // Winning row masks for red and blue
        long redWinRowMask = 0xFFL << (8 * (BOARD_HEIGHT - 2));
        long blueWinRowMask = 0xFFL << 8;

        if (isRed) {
            return (redSingles & redWinRowMask) != 0;
        } else {
            return (blueSingles & blueWinRowMask) != 0;
        }
    }
    public long getPossibleMovesSingles(long singles, boolean isRed) {
        int direction = isRed ? RED_DIRECTION : BLUE_DIRECTION;
        long emptySpaces = ~(redSingles | blueSingles | redDoubles | blueDoubles | red_on_blue | blue_on_red); // All empty spaces
        long enemyPieces = isRed ? (blue_on_red|blueDoubles|blueSingles) : (redSingles|redDoubles|red_on_blue); // Enemy single figures

        commentedBits("Empty:",emptySpaces);
        commentedBits("Enemy:",enemyPieces);
        commentedBits("Own:",singles);
        // Forward moves (no capture)
        long forwardMoves = shift(singles, direction) & emptySpaces;//Removes all occupied spaces, TODO maybe readd doubleGenesis
        commentedBits("Fwd:",forwardMoves);
        // Side moves (left and right)
        long leftMoves = shift(singles & NOT_A_FILE, -1) & emptySpaces;
        long rightMoves = shift(singles & NOT_H_FILE, 1) & emptySpaces;

        // Capture moves (diagonal)
        long leftCapture = shift(singles & NOT_A_FILE, direction - 1) & enemyPieces;//+-7 9 so diagonal
        long rightCapture = shift(singles & NOT_H_FILE, direction + 1) & enemyPieces;

        return forwardMoves | leftMoves | rightMoves | leftCapture | rightCapture;
    }


    // Utility method to shift bitboards for movement
    private long shift(long bitboard, int offset) {
        return offset > 0 ? bitboard << offset : bitboard >>> -offset;
    }

    /**
     * Displays a bitboard in an 8x8 grid format for easy visualization.
     * @param bitboard The bitboard to display.
     */
    public void displayBitboard(long bitboard) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                // Calculate the position in the bitboard
                int position = 63 - (row * 8 + col); // Start from top left
                // Check if the bit at this position is set
                if ((bitboard & (1L << position)) != 0) {
                    System.out.print("1 ");
                } else {
                    System.out.print("0 ");
                }
            }
            System.out.println(); // New line at the end of each row
        }
        System.out.println(); // Extra line for better separation
    }

    public void commentedBits(String comment, long bits){
        System.out.println(comment);
        displayBitboard(bits);
    }

}
