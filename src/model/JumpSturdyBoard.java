package model;

import java.util.Arrays;

public class JumpSturdyBoard {
    private static final int EMPTY = 0;
    private static final int CORNER = 9;
    private static final int RED_ON_RED = 1;  // 'A'
    private static final int RED_ON_BLUE = 2; // 'B'
    private static final int BLUE_ON_BLUE = 3; // 'C'
    private static final int BLUE_ON_RED = 4;  // 'D'
    private static final int RED = 5;          // 'X'
    private static final int BLUE = 6;         // 'Y'

    private static final String[][] TEMP_MAPPINGS = {
            {"r0", "X"},
            {"b0", "Y"},
            {"rr", "A"},
            {"rb", "D"},//Blue on red
            {"bb", "C"},
            {"br", "B"}//Red on blue
    };
    private int[][] board = new int[8][8];
    ;


    public JumpSturdyBoard() {
        //Fill completely with empty
        Arrays.fill(board, new int[]{0, 0, 0, 0, 0, 0, 0, 0});
        board[0][0] = CORNER;
        board[7][7] = CORNER;
        board[0][7] = CORNER;
        board[7][0] = CORNER;
    }

    public JumpSturdyBoard(String fen) {
        board = fenToBoard(fen);
    }

    public String boardToFen() {


        StringBuilder fenBuilder = new StringBuilder();


        for (int row = 0; row < board.length; row++) {
            if (row > 0) {
                fenBuilder.append('/');
            }
            int emptyCount = 0;
            for (int col = 0; col < board[row].length; col++) {
                int piece = board[row][col];
                if (piece == EMPTY) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fenBuilder.append(emptyCount);
                        emptyCount = 0;
                    }
                    fenBuilder.append(getFenCharacter(piece));
                }
            }
            if (emptyCount > 0) {
                fenBuilder.append(emptyCount);
            }
        }
        String fen = fenBuilder.toString();
        for (String[] mapping : TEMP_MAPPINGS) {
            fen = fen.replace(mapping[1], mapping[0]);
        }

        return fen.replace(".", "");//Finally, remove all .dots
    }

    private char getFenCharacter(int piece) {
        switch (piece) {
            case RED_ON_RED:
                return 'A';
            case RED_ON_BLUE:
                return 'B';
            case BLUE_ON_BLUE:
                return 'C';
            case BLUE_ON_RED:
                return 'D';
            case RED:
                return 'X';
            case BLUE:
                return 'Y';
            default:
                return '.'; // Handle empty spaces as 0 which is an empty char (as int)
        }
    }

    private String cleanFen(String fen) {// Makes FEN easier to read using mappings that occupy 1 character
        for (String[] mapping : TEMP_MAPPINGS) {
            fen = fen.replace(mapping[0], mapping[1]);
        }
        return fen;
    }

    public int[][] fenToBoard(String fen) {
        int[][] board = new int[8][8];
        board[0] = new int[6];
        board[7] = new int[6];

        int row = 0, col = 0;
        fen = cleanFen(fen);

        for (int i = 0; i < fen.length(); i++) {
            char c = fen.charAt(i);
            if (c == '/') {
                row++;
                col = 0;
            } else if (Character.isDigit(c)) {
                int count = c - '0'; // Convert char to number
                for (int j = 0; j < count; j++) {
                    board[row][col++] = EMPTY;
                }
            } else {
                board[row][col++] = getPieceValue(c);
            }
        }
        board[0] = surroundArray(board[0]); //Add the corners to first and last row
        board[7] = surroundArray(board[7]);
        return board;
    }

    public static int[] surroundArray(int[] original) {
        // Create a new array that is two elements larger than the original
        int[] newArray = new int[original.length + 2];
        // Set the first and last elements to -1
        newArray[0] = CORNER;
        newArray[newArray.length - 1] = CORNER;

        // Copy the original array into the new array starting at index 1
        System.arraycopy(original, 0, newArray, 1, original.length);

        return newArray;
    }

    private int getPieceValue(char c) {
        switch (c) {
            case 'A':
                return RED_ON_RED;
            case 'B':
                return RED_ON_BLUE;
            case 'C':
                return BLUE_ON_BLUE;
            case 'D':
                return BLUE_ON_RED;
            case 'X':
                return RED;
            case 'Y':
                return BLUE;
            default:
                return EMPTY; // Default case handles unexpected characters
        }
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Arrays.deepToString(board));
        sb.append("\nFEN: ").append(boardToFen()).append("\n");
        return sb.toString();
    }

    public void printBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(this.board[i][j] + " ");
            }
            System.out.println();
        }
    }
}