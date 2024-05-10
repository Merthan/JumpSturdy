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
            // Capture enemy single, transform to own single
            ownSingles |= (1L << toIndex);
            enemySingles &= ~(1L << toIndex);
        } else if ((enemyDoubles & (1L << toIndex)) != 0) {
            // Lands on enemy double, transforms to ownOnEnemy
            ownOnEnemy |= (1L << toIndex);
            enemyDoubles &= ~(1L << toIndex);
        } else if ((ownSingles & (1L << toIndex)) != 0) {
            // Lands on enemy double, transforms to ownOnEnemy
            ownDoubles |= (1L << toIndex);
            ownSingles &= ~(1L << toIndex);
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
    
    //Die Version von @Merthan
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

        //long emptyOrSingleDoubleable = (emptySpaces | (isRed ? redSingles : blueSingles) | (isRed? redDoubles : blueDoubles));
        long emptyOrSingleDoubleable = (emptySpaces | (redSingles|blueSingles) | (isRed?blueDoubles:redDoubles) | (isRed?blue_on_red:red_on_blue));

        //All possible moves for doubles. We can capture on all 4 fields, though do we need extra capture?
        long twoLeftOneForwardMoves = shift(doubles & NOT_AB_FILE, isRed ? moves[1] : -moves[1]) & emptyOrSingleDoubleable;
        long twoForwardOneLeftMoves = shift(doubles & NOT_AB_FILE, isRed ? moves[3] : -moves[3]) & emptyOrSingleDoubleable;

        long twoRightOneForwardMoves = shift(doubles & NOT_GH_FILE, isRed ? moves[0] : -moves[0]) & emptyOrSingleDoubleable;
        long twoForwardOneRightMoves = shift(doubles & NOT_GH_FILE, isRed ? moves[2] : -moves[2]) & emptyOrSingleDoubleable;

        return twoLeftOneForwardMoves | twoForwardOneLeftMoves | twoRightOneForwardMoves | twoForwardOneRightMoves;
    }


    /**
    * Gathers all possible moves for the current player.
    * This method decides the piece color based on whose turn it is,
    * gathers all singles and doubles, and calculates possible moves for each.
    *
    * @return List of all possible moves in standard chess notation, like "A2-A3".
    */
    public List<String> getAllPossibleMoves(boolean isRed) {
        List<String> moves = new ArrayList<>();
        long singles = isRed ? redSingles : blueSingles;
        long doubles = isRed ? redDoubles | red_on_blue : blueDoubles | blue_on_red;
    
        moves.addAll(generateMovesForPieces(singles, getPossibleMovesSingles(singles, isRed), isRed));
        moves.addAll(generateMovesForPieces(doubles, getPossibleMovesDoubles(doubles, isRed), isRed));
    
        return moves;
    }
    
    /**
    * Generates a list of moves from a given set of pieces and their possible moves.
    * This method iterates through each bit of the pieces' bitboard, checks if a piece is present,
    * and then calculates valid moves for it using another provided bitboard of possible moves.
    *
    * @param pieces A long representing the bitboard of pieces.
    * @param possibleMoves A long representing the bitboard of all possible moves for these pieces.
    * @param isRed A boolean indicating if the current moves are for Red pieces.
    * @return A list of moves in the format "from-to" like "E2-E4".
    */
    private List<String> generateMovesForPieces(long pieces, long possibleMoves, boolean isRed) {
        List<String> moveList = new ArrayList<>();
        for (int fromIndex = 0; fromIndex < 64; fromIndex++) {
            if ((pieces & (1L << fromIndex)) != 0) {  // There is a piece at fromIndex
                long movesFromThisPiece = possibleMoves & getPossibleMovesForIndividualPiece(fromIndex, isRed);
                for (int toIndex = 0; toIndex < 64; toIndex++) {
                    if ((movesFromThisPiece & (1L << toIndex)) != 0) {  // Valid move to toIndex
                        String fromPos = indexToPosition(fromIndex);
                        String toPos = indexToPosition(toIndex);
                        moveList.add(fromPos + "-" + toPos);
                    }
                }
            }
        }
        return moveList;
    }
    
    public long getPossibleMovesForIndividualPiece(int index, boolean isRed) {
        long singlePieceMask = 1L << index;
        long moves = 0L;
    
        // Überprüfen, ob es sich um einen Einzelstein handelt
        if (((isRed ? redSingles : blueSingles) & singlePieceMask) != 0) {
            // Erzeuge ein Bitboard, das nur diesen Stein enthält
            long singles = singlePieceMask;
    
            // Rufe die vorhandene Methode auf, um mögliche Züge für diesen einen Stein zu ermitteln
            moves |= getPossibleMovesSingles(singles, isRed);
        }
    
        // Überprüfen, ob es sich um einen Doppelstein handelt
        if (((isRed ? (redDoubles | red_on_blue) : (blueDoubles | blue_on_red)) & singlePieceMask) != 0) {
            // Erzeuge ein Bitboard, das nur diesen Stein enthält
            long doubles = singlePieceMask;
    
            // Rufe die vorhandene Methode auf, um mögliche Züge für diesen einen Doppelstein zu ermitteln
            moves |= getPossibleMovesDoubles(doubles, isRed);
        }
    
        return moves;
    }
    
    //TODO: Wir brauchen eine Methode movePiece, die unterscheidet zwischen moveSinglePiece und moveDoublePiece
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Append column labels (A-H)
        sb.append("   A  B  C  D   E  F  G  H\n");

        for (int row = 0; row < BOARD_HEIGHT; row++) {
            // Append row number
            sb.append((8 - row)).append(" ");
            // 😎😎😎😎😎😎😎😎😎😎
            for (int col = 0; col < BOARD_WIDTH; col++) {
                int index = row * BOARD_WIDTH + col;
                if ((redSingles & (1L << index)) != 0) {
                    sb.append("\uD83D\uDD34 ");//🔴   😎😎😎😎😎😎😎😎
                } else if ((blueSingles & (1L << index)) != 0) {
                    sb.append("\uD83D\uDD35 ");//🔵
                } else if ((redDoubles & (1L << index)) != 0) {
                    sb.append("\uD83D\uDFE5 ");//🟥
                } else if ((blueDoubles & (1L << index)) != 0) {
                    sb.append("\uD83D\uDFE6 ");//🟦
                } else if ((blue_on_red & (1L << index)) != 0) {
                    sb.append("\uD83D\uDD37 ");//🔷
                } else if ((red_on_blue & (1L << index)) != 0) {
                    sb.append("\uD83D\uDD36 ");//🔶 red diamond in this form doesnt exist, only diamond suit. Orange diamond instead then :)
                } else {
                    sb.append("⬜ ");
                }
            }
            sb.append(" ").append(8 - row).append("\n"); // Append row number
        }
        sb.append("   A  B  C  D   E  F  G  H\n");
        return sb.toString();
    }

    public void commentedBits(String comment, long bits) {
        System.out.println(comment);
        displayBitboard(bits);
    }

}