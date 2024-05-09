package model;

import java.util.*;

import static model.JumpSturdyBoard.*;

public class BitBoard {

    public boolean redsTurn = true;
    private static final int BOARD_WIDTH = 8;
    private static final int BOARD_HEIGHT = 8;
    private static final long CORNER_MASK = ~(1L | (1L << 7) | (1L << 56) | (1L << 63));
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
    private static long NOT_AB_FILE = 0xfcfcfcfcfcfcfcfcl;
    private static long NOT_GH_FILE = 0x3f3f3f3f3f3f3f3fl;

    // Directions for single figures based on team
    private static final int RED_DIRECTION = 8;   // Red moves down
    private static final int BLUE_DIRECTION = -8;   // Blue moves up

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
        red_on_blue = 0L;
        blue_on_red = 0L;
    }

    // Method to read a 2D array and convert it into a bitboard
    public void readBoard(int[][] board) {
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                int piece = board[row][col];
                int index = row * BOARD_WIDTH + col;
                switch (piece) {
                    case EMPTY, CORNER:
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
                    case RED://TODO: For some reason red and blue are swapped here, not sure why it doesnt work without swapping
                        redSingles |= 1L << index;
                        break;
                    case BLUE:
                        blueSingles |= 1L << index;
                        break;
                }
            }
        }
    }

    public char checkWinCondition(long redPieces, long bluePieces) {
        // Define masks for the top and bottom rows
        long topRowMask = 0xFFL;  // Mask for the top row (bits 0-7)
        long bottomRowMask = 0xFFL << 56;  // Mask for the bottom row (bits 56-63)

        // Check if any blue piece is in the top row
        if ((bluePieces & topRowMask) != 0) {
            return 'b';
        }
        // Check if any red piece is in the bottom row
        else if ((redPieces & bottomRowMask) != 0) {
            return 'r';
        }
        // Check if there are any red pieces on the field, if no -> blue wins
        else if (Long.bitCount(redPieces) == 0) {
            return 'b';
        }
        // Chef if there are any blue pieces on the field, if no -> red wins
        else if (Long.bitCount(bluePieces) == 0) {
            return 'r';
        }
        // Check if there are any possible moves for blue
        else if (Long.bitCount(getMovesForTeam(false)) == 0){
            // Check if there are any possible moves for red, if no -> draw
            if(Long.bitCount(getMovesForTeam(true)) == 0) return 'd';
            else return 'r';
        }
        // Check if there are any possible moves for red
        else if(Long.bitCount(getMovesForTeam(true)) == 0){
            // Check if there are any possible moves for blue, if no -> draw
            if(Long.bitCount(getMovesForTeam(false)) == 0) return 'd';
            else return 'b';
        }
        return 'f';
    }


    public void moveSinglePiece(long fromIndex, long toIndex, boolean isRed) {
        long ownSingles = isRed ? redSingles : blueSingles;
        long enemySingles = isRed ? blueSingles : redSingles;
        long ownDoubles = isRed ? redDoubles : blueDoubles;
        long enemyDoubles = isRed ? blueDoubles : redDoubles;
        long ownOnEnemy = isRed ? red_on_blue : blue_on_red;
        long enemyOnOwn = isRed ? blue_on_red : red_on_blue;

        // Clear the bit at the original position
        ownSingles &= ~(1L << fromIndex);

        // Handling different scenarios on the destination
        if ((enemySingles & (1L << toIndex)) != 0) {
            // Capture enemy single, transform to double
            ownSingles |= (1L << toIndex);
            enemySingles &= ~(1L << toIndex);
        } else if ((enemyDoubles & (1L << toIndex)) != 0) {
            // Lands on enemy double, transforms to ownOnEnemy
            ownOnEnemy |= (1L << toIndex);
            enemyDoubles &= ~(1L << toIndex);
        } else if ((enemyOnOwn & (1L << toIndex)) != 0) {
            // Lands on enemyOnOwn, transforms to own double
            ownDoubles |= (1L << toIndex);
            enemyOnOwn &= ~(1L << toIndex);
        } else {
            // Regular move to an empty square, becomes a single
            ownSingles |= (1L << toIndex);
        }

        // Update the bitboards
        redSingles = isRed ? ownSingles : enemySingles;
        blueSingles = isRed ? enemySingles : ownSingles;
        redDoubles = isRed ? ownDoubles : enemyDoubles;
        blueDoubles = isRed ? enemyDoubles : ownDoubles;
        red_on_blue = isRed ? ownOnEnemy : enemyOnOwn;
        blue_on_red = isRed ? enemyOnOwn : ownOnEnemy;
    }

    public void moveDoublePiece(long fromIndex, long toIndex, boolean isRed) {
        long ownDoubles = isRed ? redDoubles : blueDoubles;
        long ownSingles = isRed ? redSingles : blueSingles;
        long enemySingles = isRed ? blueSingles : redSingles;
        long enemyDoubles = isRed ? blueDoubles : redDoubles;
        long enemyOnOwn = isRed ? blue_on_red : red_on_blue;
        long ownOnEnemy = isRed ? red_on_blue : blue_on_red;

        // Remove the double piece from the original position
        ownDoubles &= ~(1L << fromIndex);

        // Determine the bottom type of the double
        boolean bottomIsEnemy = (ownOnEnemy & (1L << fromIndex)) != 0;
        System.out.println("bt" + bottomIsEnemy);
        // Handle the landing cases
        if ((ownSingles & (1L << toIndex)) != 0) {
            // Landing on own single, turn it into own double
            ownDoubles |= (1L << toIndex);
            ownSingles &= ~(1L << toIndex);
        } else if ((enemyOnOwn & (1L << toIndex)) != 0) {
            // Landing on enemy_on_own, turn it into own double
            ownDoubles |= (1L << toIndex);
            enemyOnOwn &= ~(1L << toIndex);
        } else if ((enemyDoubles & (1L << toIndex)) != 0) {
            // Landing on enemy_on_own, turn it into own double
            ownOnEnemy |= (1L << toIndex);
            enemyDoubles &= ~(1L << toIndex);
        } else if ((enemySingles & (1L << toIndex)) != 0) {
            // Regular move to empty space, place the top of the double as a single
            ownSingles |= (1L << toIndex);
            enemySingles &= ~(1L << toIndex);
        } else {//TODO: hope all cases are covered and nothing forgotten
            ownSingles |= (1L << toIndex);
        }

        // Always turn the former bottom of the double into a single at the original position
        if (bottomIsEnemy) {
            enemySingles |= (1L << fromIndex);
        } else {
            ownSingles |= (1L << fromIndex);
        }

        // Update the state using ternary operators
        redDoubles = isRed ? ownDoubles : enemyDoubles;
        blueDoubles = isRed ? enemyDoubles : ownDoubles;
        redSingles = isRed ? ownSingles : enemySingles;
        blueSingles = isRed ? enemySingles : ownSingles;
        blue_on_red = isRed ? enemyOnOwn : ownOnEnemy;
        red_on_blue = isRed ? ownOnEnemy : enemyOnOwn;
    }


    public long getMovesForTeam(boolean red) {
        if (red) {
            return getPossibleMovesSingles(redSingles, true) | getPossibleMovesDoubles(redDoubles | red_on_blue, true);
        } else {
            return getPossibleMovesSingles(blueSingles, false) | getPossibleMovesDoubles(blueDoubles | blue_on_red, false);
        }
    }

    public long getPossibleMovesSingles(long singles, boolean isRed) {
        int direction = isRed ? RED_DIRECTION : BLUE_DIRECTION;
        long emptySpaces = ~(redSingles | blueSingles | redDoubles | blueDoubles | red_on_blue | blue_on_red) & CORNER_MASK; // All empty spaces
        long enemyPieces = isRed ? (blue_on_red | blueDoubles | blueSingles) : (redSingles | redDoubles | red_on_blue); // Enemy single figures

        //commentedBits("Empty:",emptySpaces);
        //commentedBits("Cornermask",CORNER_MASK);
        //commentedBits("not_a",NOT_A_FILE);
        //commentedBits("Enemy:",enemyPieces);
        //commentedBits("Own:",singles);

        long emptyOrSingleDoubleable = (emptySpaces | (isRed ? redSingles : blueSingles));
        // Forward moves (no capture)
        long forwardMoves = shift(singles, direction) & emptyOrSingleDoubleable;//Removes all occupied spaces, TODO maybe readd doubleGenesis
        //commentedBits("Fwd:",forwardMoves);
        // Side moves (left and right)
        long leftMoves = shift(singles & NOT_A_FILE, -1) & emptyOrSingleDoubleable;
        long rightMoves = shift(singles & NOT_H_FILE, 1) & emptyOrSingleDoubleable;

        // Capture moves (diagonal)
        long leftCapture = shift(singles & NOT_A_FILE, direction - 1) & enemyPieces; //+-7 9 so diagonal
        long rightCapture = shift(singles & NOT_H_FILE, direction + 1) & enemyPieces;
        //System.out.println("Possible moves:");
        return forwardMoves | leftMoves | rightMoves | leftCapture | rightCapture;
    }

    // Return all possible moves as String List
    public List<String> getPossibleMovesSinglesString(long singles, boolean isRed) {
        int direction = isRed ? RED_DIRECTION : BLUE_DIRECTION;
        long emptySpaces = ~(redSingles | blueSingles | redDoubles | blueDoubles | red_on_blue | blue_on_red) & CORNER_MASK; // All empty spaces
        long enemyPieces = isRed ? (blue_on_red | blueDoubles | blueSingles) : (redSingles | redDoubles | red_on_blue); // Enemy single figures

        long emptyOrSingleDoubleable = (emptySpaces | (isRed ? redSingles : blueSingles));
        long forwardMoves = shift(singles, direction) & emptyOrSingleDoubleable;
        long leftMoves = shift(singles & NOT_A_FILE, -1) & emptyOrSingleDoubleable;
        long rightMoves = shift(singles & NOT_H_FILE, 1) & emptyOrSingleDoubleable;
        long leftCapture = shift(singles & NOT_A_FILE, direction - 1) & enemyPieces;
        long rightCapture = shift(singles & NOT_H_FILE, direction + 1) & enemyPieces;

        List<String> possibleMoves = new ArrayList<>();

        // Convert each set bit index to a source and destination square string
        

        return cleanCorners(possibleMoves);
    }

    // Translate Bitboards to Strings
    /*public String getMoveString(int srcIndex, int destIndex) {
        int srcRank = 8 - (srcIndex / 8);
        int srcFile = (srcIndex % 8) + 1;
        int destRank = 8 - (destIndex / 8);
        int destFile = (destIndex % 8) + 1;

        char srcFileChar = (char) ('A' + srcFile - 1);
        char destFileChar = (char) ('A' + destFile - 1);

        return "" + srcFileChar + srcRank + "-" + destFileChar + destRank;
    }*/

    public static List<String> cleanCorners(List<String> toRemove){
        Iterator<String> iterator = toRemove.iterator();
        while (iterator.hasNext()) {
            String string = iterator.next();
            if ((string.charAt(3) == 'A' && (string.charAt(4) == '1')) ||
                (string.charAt(3) == 'A' && (string.charAt(4) == '8')) ||
                (string.charAt(3) == 'H' && (string.charAt(4) == '1')) ||
                (string.charAt(3) == 'H' && (string.charAt(4) == '8'))) {
                iterator.remove();
            }
        }
        return toRemove;
    }
    


   /*public long getPossibleMovesDoubles(long doubles, boolean isRed) {
        int[] moves = {17, 15, 10, 6};// Precalculated, negative for other direction,

        // All occupied spaces
        long occupiedSpaces = redSingles | blueSingles | redDoubles | blueDoubles | red_on_blue | blue_on_red;
        long emptySpaces = ~occupiedSpaces & CORNER_MASK; // All empty spaces, excluding corners

        // Define jumpable spaces for doubles , TODO: Assuming you can jump from red double to red single to create another double
        long jumpableSpaces = isRed ? (blue_on_red | blueDoubles | blueSingles | redSingles) : (red_on_blue | redDoubles | redSingles | blueSingles);

        // Calculate moves
        long possibleMoves = 0L;
        for (int i = 0; i < moves.length; i++) {
            long moveTargets;
            int move = isRed ? moves[i] : -moves[i]; //Negative
            moveTargets = shift(doubles, move) & (emptySpaces | jumpableSpaces); // Shift right or down
            possibleMoves |= moveTargets;
        }

        return possibleMoves;
    }*/

    public long getPossibleMovesDoubles(long doubles, boolean isRed){
        int[] moves = {17, 15, 10, 6};

        // All occupied spaces
        long occupiedSpaces = redSingles | blueSingles | redDoubles | blueDoubles | red_on_blue | blue_on_red;
        long emptySpaces = ~occupiedSpaces & CORNER_MASK; // All empty spaces, excluding corners

        //long enemyPieces = isRed ? (blue_on_red | blueDoubles | blueSingles) : (redSingles | redDoubles | red_on_blue); // Enemy single figures
        //long jumpableSpaces = isRed ? (blue_on_red | blueDoubles | blueSingles | redSingles) : (red_on_blue | redDoubles | redSingles | blueSingles);

        long emptyOrSingleDoubleable = (emptySpaces | (isRed ? redSingles : blueSingles) | (isRed? redDoubles : blueDoubles));

        //All possible moves for doubles. We can capture on all 4 fields, though do we need extra capture
        long twoLeftOneForwardMoves = shift(doubles & NOT_AB_FILE, isRed ? moves[1] : -moves[1]) & emptyOrSingleDoubleable;
        long twoForwardOneLeftMoves = shift(doubles & NOT_AB_FILE, isRed ? moves[3] : -moves[3]) & emptyOrSingleDoubleable;

        long twoRightOneForwardMoves = shift(doubles & NOT_GH_FILE, isRed ? moves[0] : -moves[0]) & emptyOrSingleDoubleable;
        long twoForwardOneRightMoves = shift(doubles & NOT_GH_FILE, isRed ? moves[2] : -moves[2]) & emptyOrSingleDoubleable;

        return twoLeftOneForwardMoves | twoForwardOneLeftMoves | twoRightOneForwardMoves | twoForwardOneRightMoves;

    }

    public List<String> getPossibleMovesDoublesString(long doubles, boolean isRed){
        int[] moves = {17, 15, 10, 6};

        // All occupied spaces
        long occupiedSpaces = redSingles | blueSingles | redDoubles | blueDoubles | red_on_blue | blue_on_red;
        long emptySpaces = ~occupiedSpaces & CORNER_MASK; // All empty spaces, excluding corners

        //long enemyPieces = isRed ? (blue_on_red | blueDoubles | blueSingles) : (redSingles | redDoubles | red_on_blue); // Enemy single figures
        //long jumpableSpaces = isRed ? (blue_on_red | blueDoubles | blueSingles | redSingles) : (red_on_blue | redDoubles | redSingles | blueSingles);

        long emptyOrSingleDoubleable = (emptySpaces | (isRed ? redSingles : blueSingles) | (isRed? redDoubles : blueDoubles));

        //All possible moves for doubles. We can capture on all 4 fields, though do we need extra capture
        long twoLeftOneForwardMoves = shift(doubles & NOT_AB_FILE, isRed ? moves[3] : -moves[3]) & emptyOrSingleDoubleable;
        long twoForwardOneLeftMoves = shift(doubles & NOT_AB_FILE, isRed ? moves[1] : -moves[1]) & emptyOrSingleDoubleable;

        long twoRightOneForwardMoves = shift(doubles & NOT_GH_FILE, isRed ? moves[2] : -moves[2]) & emptyOrSingleDoubleable;
        long twoForwardOneRightMoves = shift(doubles & NOT_GH_FILE, isRed ? moves[0] : -moves[0]) & emptyOrSingleDoubleable;


        List<String> possibleMoves = new ArrayList<>();

        // Convert each set bit index to a source and destination square string
    

        return cleanCorners(possibleMoves);
    }

   /* public List<String> getPossibleMovesDoublesString(long doubles, boolean isRed) {
    int[] moves = {17, 15, 10, 6}; // Precalculated, negative for other direction

    // All occupied spaces
    long occupiedSpaces = redSingles | blueSingles | redDoubles | blueDoubles | red_on_blue | blue_on_red;
    long emptySpaces = ~occupiedSpaces & CORNER_MASK; // All empty spaces, excluding corners
    long jumpableSpaces = isRed ? (blue_on_red | blueDoubles | blueSingles | redSingles) : (red_on_blue | redDoubles | redSingles | blueSingles);

    // Calculate moves
    long possibleMoves = 0L;
    for (int i = 0; i < moves.length; i++) {
        long moveTargets;
        int move = isRed ? moves[i] : -moves[i]; // Negative
        moveTargets = shift(doubles, move) & (emptySpaces | jumpableSpaces); // Shift right or down
        possibleMoves |= moveTargets;
    }

    List<String> possibleMovesStrings = new ArrayList<>();
    
    // Convert each set bit index to a source and destination square string
    for (int i = 0; i < 64; i++) {
        long mask = 1L << i;
        if ((possibleMoves & mask) != 0) {
            possibleMovesStrings.add(getMoveString(i, i + moves[0])); // Add move as string
            possibleMovesStrings.add(getMoveString(i, i + moves[1])); // Add move as string
            possibleMovesStrings.add(getMoveString(i, i + moves[2])); // Add move as string
            possibleMovesStrings.add(getMoveString(i, i + moves[3])); // Add move as string
        }
    }
    
    return cleanCorners(possibleMovesStrings);
}
*/
    public static long positionToIndex(String position) {
        // Extract the column (file) and row from the position
        char letter = position.charAt(0); // 'F'
        char number = position.charAt(1); // '3'// 'F' - 'A' = 5// '3' - '1' = 2

        // Calculate the index for a 0-based array (bottom-left is 0,0)
        return (7 - (number - '1')) * 8 + (letter - 'A'); // Convert to 0-based index for an 8x8 board
    }

    public static String indexToPosition(long index) {
        int fileIndex = (int) (index % 8); // Calculate file index (column)
        int rankIndex = (int) (index / 8); // Calculate rank index (row)
        char file = (char) ('A' + fileIndex); // Convert file index to letter (A-H)
        char rank = (char) ('1' + (7 - rankIndex)); // Convert rank index to number (1-8), adjusting for 0-based index
        return "" + file + rank; // Concatenate to form the position string
    }

    // Method to parse a move like "F3-F4" and return fromIndex and toIndex
    public static long[] parseMove(String move) {
        // Split the move into the from and to parts
        String[] parts = move.split("-");
        // Convert each part to an index
        long fromIndex = positionToIndex(parts[0]);
        long toIndex = positionToIndex(parts[1]);
        // Return the indices as an array
        return new long[]{fromIndex, toIndex};
    }

    public static void main(String[] args) {
        // Example usage
        String move = "A8-H1";
        long[] indices = parseMove(move);
        System.out.println("From Index: " + indices[0] + ", To Index: " + indices[1]);
    }


    // Utility method to shift bitboards for movement
    private long shift(long bitboard, int offset) {
        return offset > 0 ? (bitboard << offset & CORNER_MASK) : (bitboard >>> -offset & CORNER_MASK);
    }

    public void displayBitboard(long bitboard) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int position = row * 8 + col;  // Start from top left, no inversion
                // IF Corner
                if ((row == 0 || row == 7) && (col == 0 || col == 7)) {
                    System.out.print("X ");
                } else if ((bitboard & (1L << position)) != 0) {
                    System.out.print("1 ");
                } else {
                    System.out.print("0 ");
                }
            }
            System.out.println();
        }
        System.out.println();
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

    public void commentedBits(String comment, long bits) {
        System.out.println(comment);
        displayBitboard(bits);
    }

    
  /*  public static List<String> getSetBitPositions(long bitboard) {
        List<String> positions = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            long mask = 1L << i;
            if ((bitboard & mask) != 0) {
                positions.add(getPositionFromIndex(i));
            }
        }
        return positions;
    }
    */

   /*  public static String getPositionFromIndex(int index) {
        int row = 8 - (index / 8);
        int column = (index % 8) + 1;
        char file = (char) ('A' + column - 1);
        return "" + file + row;
    } */

    // Method to transform possible moves into a list array of strings
     public static List<String> transformMoves(long possibleMoves, long redDoubles) {
       List<String> movesList = new ArrayList<>();
    
            for (int row = 0; row < 8; row++) {
                for (int column = 0; column < 8; column++) {
                    // Check if the corresponding bit is set in possibleMoves
                    if ((possibleMoves & (1L << (row * 8 + column))) != 0) {
                        // Convert row and column indices to board coordinates (e.g., 0,0 -> A8)
                        char sourceColChar = (char) ('A' + column);
                        int sourceRowNum = 8 - row;
    
                        // Format the move string (e.g., "H5-F4")
                        String move = "" + sourceColChar + sourceRowNum;
                        movesList.add(move);
                    }
                }
            }
            return movesList;
        }
          
    
}