package model;

import misc.Tools;

import java.util.*;
import java.util.stream.Collectors;

import static misc.deprecated.JumpSturdyBoard.*;
import static misc.deprecated.JumpSturdyBoard.BLUE;
import static misc.deprecated.JumpSturdyBoard.RED;
import static misc.Tools.*;

public class BitBoard {

    public boolean redsTurn = true;
    private static final int BOARD_WIDTH = 8;
    private static final int BOARD_HEIGHT = 8;

    public static final byte WINNER_ONGOING = 0;
    public static final byte WINNER_RED = 1;
    public static final byte WINNER_BLUE = 2;
    public static final byte WINNER_DRAW = 3;
    public static final long CORNER_MASK = ~(1L | (1L << 7) | (1L << 56) | (1L << 63));
    // Bitboards for the game pieces
    public long redSingles;   // Bitboard for red single pieces
    public long blueSingles;  // Bitboard for blue single pieces
    public long redDoubles;   // Bitboard for red double pieces (top knights)
    public long blueDoubles;  // Bitboard for blue double pieces (top knights)

    public long red_on_blue;   // Bitboard for red double pieces (top knights)
    public long blue_on_red;  // Bitboard for blue double pieces (top knights)


    // Masks for edges to handle movements correctly
    public static final long NOT_A_FILE = 0xfefefefefefefefeL; // 11111110...
    public static final long NOT_H_FILE = 0x7f7f7f7f7f7f7f7fL; // 01111111...
    public static final long NOT_AB_FILE = 0xFCFCFCFCFCFCFCFCL; // Not columns A and B
    public static final long NOT_GH_FILE = 0x3F3F3F3F3F3F3F3FL; // Not columns G and H

    public static final long topRowMask = 0xFFL;  // Mask for the top row (bits 0-7)
    public static final long bottomRowMask = 0xFFL << 56;  // Mask for the bottom row (bits 56-63)

    // Directions for single figures based on team
    private static final int RED_DIRECTION = 8;   // Red moves down
    private static final int BLUE_DIRECTION = -8;   // Blue moves up

    // Constructor to initialize the game board
    public BitBoard() {
        //removed, probably not necessary unless set in a weird way
        //setupInitialBoard();
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

    public BitBoard(String fen) {
        readFEN(fen);
    }

    public void readFEN(String fen) {

        // Define the FEN mappings
        final String[][] TEMP_MAPPINGS = {
                {"r0", "X"},
                {"b0", "Y"},
                {"rr", "A"},
                {"rb", "D"}, // Blue on red
                {"bb", "C"},
                {"br", "B"} // Red on blue
        };

        for (String[] mapping : TEMP_MAPPINGS) { // For easier indexes
            fen = fen.replace(mapping[0], mapping[1]);
        }

        String[] rows = fen.split("/");

        for (int row = 0; row < rows.length; row++) {
            int col = (row == 0 || row == 7) ? 1 : 0;
            for (int i = 0; i < rows[row].length(); i++) {
                char c = rows[row].charAt(i);
                if (Character.isDigit(c)) {
                    // Empty squares
                    col += c - '0';
                } else {
                    byte index = (byte) ((7 - row) * 8 + col);
                    switch (c) {
                        case 'X' -> {
                            redSingles |= 1L << index;
                        }
                        case 'Y' -> {
                            blueSingles |= 1L << index;
                        }
                        case 'A' -> {
                            redDoubles |= 1L << index;
                        }
                        case 'C' -> {
                            blueDoubles |= 1L << index;
                        }
                        case 'D' -> {
                            blue_on_red |= 1L << index;
                        }
                        case 'B' -> {
                            red_on_blue |= 1L << index;
                        }
                    }
                    col++;
                }
            }
        }
    }

    public String toFEN() {
        StringBuilder fen = new StringBuilder();

        for (int row = 0; row < 8; row++) {
            int emptyCount = 0;

            for (int col = (row == 0 || row == 7) ? 1 : 0; col < ((row == 0 || row == 7) ? 7 : 8); col++) {//Start end have corners,6 length
                int index = (7 - row) * 8 + col;
                if (index == 0 || index == 7 || index == 56) {//63 never happens
                    continue; // Skip corners
                }
                String piece; // Set depending on bitboards
                if ((redSingles & (1L << index)) != 0) {
                    piece = "r0";
                } else if ((blueSingles & (1L << index)) != 0) {
                    piece = "b0";
                } else if ((redDoubles & (1L << index)) != 0) {
                    piece = "rr";
                } else if ((blueDoubles & (1L << index)) != 0) {
                    piece = "bb";
                } else if ((blue_on_red & (1L << index)) != 0) {
                    piece = "rb";
                } else if ((red_on_blue & (1L << index)) != 0) {
                    piece = "br";
                } else {
                    emptyCount++;
                    piece = "";
                }

                if (!piece.isEmpty()) { // Count if empty for number
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(piece);
                } else if (col == 7 || col == 6 && (row == 0 || row == 7)) {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                }
            }
            if (row < 7) {
                fen.append('/');
            }
        }
        return fen.toString();
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
                    case RED:
                        redSingles |= 1L << index;
                        break;
                    case BLUE:
                        blueSingles |= 1L << index;
                        break;
                }
            }
        }
    }

    //Returns if the piece at position belongs to red
    public boolean isItRedsTurnByPositionOfPieces(byte position) {
        detectOverlap(redSingles, blueSingles, redDoubles, blueDoubles, red_on_blue, blue_on_red);
        boolean redSingleBitSet = (redSingles & (1L << position)) != 0;
        boolean blueSingleBitSet = (blueSingles & (1L << position)) != 0;
        boolean redDoubleBitSet = (redDoubles & (1L << position)) != 0;
        boolean blueDoubleBitSet = (blueDoubles & (1L << position)) != 0;
        boolean redOnBlueBitSet = (red_on_blue & (1L << position)) != 0;
        boolean blueOnRedBitSet = (blue_on_red & (1L << position)) != 0;
        if (!redSingleBitSet && !blueSingleBitSet && !redDoubleBitSet && !blueDoubleBitSet && !redOnBlueBitSet && !blueOnRedBitSet) {
            throw new RuntimeException("Invalid position, either empty or corner" + position + " " + Tools.indexToStringPosition(position));
        }
        return redSingleBitSet || redDoubleBitSet || redOnBlueBitSet;//No check for all necessary as otherwise would throw, blues turn otherwise
    }

    //Static as it might be used in other classes too
    //TODO: REMOVE LATER FOR PERFORMANCE REASONS WHEN NOT THROWING
/*    public static void detectOverlap(long redSingles, long blueSingles, long redDoubles, long blueDoubles, long red_on_blue, long blue_on_red) {  //Should not be called after bug has been detected/only in tests for performance reasons
        boolean noOverlap = true;

        for (byte position = 0; position < 64; position++) {
            int bitCount = 0;

            if ((redSingles & (1L << position)) != 0) bitCount++;
            if ((blueSingles & (1L << position)) != 0) bitCount++;
            if ((redDoubles & (1L << position)) != 0) bitCount++;
            if ((blueDoubles & (1L << position)) != 0) bitCount++;
            if ((red_on_blue & (1L << position)) != 0) bitCount++;
            if ((blue_on_red & (1L << position)) != 0) bitCount++;

            if (bitCount > 1) {
                noOverlap = false;
                Tools.printInColor("Overlap detected at position: " + position + " " + Tools.indexToStringPosition(position), true);
            }
        }

        if (!noOverlap) {
            // Tools.printInColor("No overlap in bit positions.", false);
            throw new IllegalStateException("Overlaps in bitboards detected");
        }
    }*/

    public static void detectOverlap(long redSingles, long blueSingles, long redDoubles, long blueDoubles, long red_on_blue, long blue_on_red) {
        boolean noOverlap = true;

        for (byte position = 0; position < 64; position++) {
            int bitCount = 0;
            StringBuilder overlapInfo = new StringBuilder("Overlap detected at position: " + position + " " + Tools.indexToStringPosition(position) + " in bitboards: ");

            if ((redSingles & (1L << position)) != 0) {
                bitCount++;
                overlapInfo.append("redSingles ");
            }
            if ((blueSingles & (1L << position)) != 0) {
                bitCount++;
                overlapInfo.append("blueSingles ");
            }
            if ((redDoubles & (1L << position)) != 0) {
                bitCount++;
                overlapInfo.append("redDoubles ");
            }
            if ((blueDoubles & (1L << position)) != 0) {
                bitCount++;
                overlapInfo.append("blueDoubles ");
            }
            if ((red_on_blue & (1L << position)) != 0) {
                bitCount++;
                overlapInfo.append("red_on_blue ");
            }
            if ((blue_on_red & (1L << position)) != 0) {
                bitCount++;
                overlapInfo.append("blue_on_red ");
            }

            if (bitCount > 1) {
                noOverlap = false;
                Tools.printInColor(overlapInfo.toString(), true);
            }
        }

        if (!noOverlap) {
            throw new IllegalStateException("Overlaps in bitboards detected");
        }
    }

    public void detectOverlap(){//Member for easier call
        detectOverlap(redSingles,blueSingles,redDoubles,blueDoubles,red_on_blue,blue_on_red);
    }

    public byte checkWinCondition() {
        // Define masks for the top and bottom rows

        long bluePieces = blueSingles | blueDoubles | blue_on_red;
        long redPieces = redSingles | redDoubles | red_on_blue;

        // Check if any blue piece is in the top row or no red pieces
        if ((bluePieces & topRowMask) != 0 || redPieces == 0) {
            return WINNER_BLUE;
        }
        // Check if any red piece is in the bottom row or no blue pieces
        else if ((redPieces & bottomRowMask) != 0 || bluePieces == 0) {
            return WINNER_RED;
        }
        // Check if there are any possible moves for blue
        else if (getMovesForTeam(false) == 0) {
            // Check if there are any possible moves for red, if no -> draw
            if (getMovesForTeam(true) == 0) return WINNER_DRAW;
            else return WINNER_RED;
        }
        // Check if there are any possible moves for red
        else if (getMovesForTeam(true) == 0) {
            // Check if there are any possible moves for blue, if no -> draw
            if (getMovesForTeam(false) == 0) return WINNER_DRAW;
            else return WINNER_BLUE;
        }
        return WINNER_ONGOING;
    }

    public boolean doMove(String move, boolean isRedTurn, boolean checkIfPossible) {
        byte[] indices = parseMove(move);
        if (isItRedsTurnByPositionOfPieces(indices[0]) != isRedTurn) {
            throw new IllegalMoveException("Player can't move enemy piece:"+indices[0]+"-"+indices[1]+" > "+Tools.parseMoveToString(indices));
        }
        if (checkIfPossible) {
            long possibleMoves = getPossibleMovesForIndividualPiece(indices[0], isRedTurn);

            if ((possibleMoves & (1L << indices[1])) == 0) { //Move not included, index conversion
                throw new IllegalMoveException("Move is not possible:" + move);
            }
        }
        if (isRedTurn) {
            if ((redDoubles != 0 && (redDoubles & (1L << indices[0])) != 0)
                    || (red_on_blue != 0 && (red_on_blue & (1L << indices[0])) != 0)) {
                moveDoublePiece(indices[0], indices[1], true);
            } else {
                moveSinglePiece(indices[0], indices[1], true);
            }
        } else {
            if ((blueDoubles != 0 && (blueDoubles & (1L << indices[0])) != 0)
                    || (blue_on_red != 0 && (blue_on_red & (1L << indices[0])) != 0)) {
                moveDoublePiece(indices[0], indices[1], false);
            } else {
                moveSinglePiece(indices[0], indices[1], false);
            }
        }
        return !isRedTurn; //Now optionally returns whose turn it is in case we test chaining same team calls etc in later steps
    }

    /*
    public void doMoveVoid(String move, boolean isRedTurn,boolean checkIfPossible) {
        byte[] indices = parseMove(move);
        if(isItRedsTurnByPositionOfPieces(indices[0])!=isRedTurn){
            throw new IllegalMoveException("Player cant move enemy piece");
        }
        if(checkIfPossible){
            long possibleMoves =getPossibleMovesForIndividualPiece(indices[0],isRedTurn);

            if((possibleMoves & (1L << indices[1])) == 0){//Move not included, index conversion
                throw new IllegalMoveException("Move is not possible:" +move);
            }
        }
        if (isRedTurn) {
            if ((redDoubles != 0 && (redDoubles & (1L << indices[0])) != 0)
                    ||(red_on_blue != 0 && (red_on_blue & (1L << indices[0])) != 0)    ) {
                moveDoublePiece(indices[0], indices[1], true);
            } else {
                moveSinglePiece(indices[0], indices[1], true);
            }
        } else {
            if ((blueDoubles != 0 && (blueDoubles & (1L << indices[0])) != 0)||(blue_on_red != 0 && (blue_on_red & (1L << indices[0])) != 0)) {
                moveDoublePiece(indices[0], indices[1], false);
            } else {
                moveSinglePiece(indices[0], indices[1], false);
            }
        }
    }*/


    public void moveSinglePiece(byte fromIndex, byte toIndex, boolean isRed) {
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

    public void moveDoublePiece(byte fromIndex, byte toIndex, boolean isRed) {
        long ownDoubles = isRed ? redDoubles : blueDoubles;
        long ownSingles = isRed ? redSingles : blueSingles;
        long enemySingles = isRed ? blueSingles : redSingles;
        long enemyDoubles = isRed ? blueDoubles : redDoubles;
        long enemyOnOwn = isRed ? blue_on_red : red_on_blue;
        long ownOnEnemy = isRed ? red_on_blue : blue_on_red;

        // Remove the double piece from the original position
        ownDoubles &= ~(1L << fromIndex);
        ownOnEnemy &= ~(1L << fromIndex);
        // Determine the bottom type of the double
        boolean bottomIsEnemy = (ownOnEnemy & (1L << fromIndex)) != 0;
        //System.out.println("bt" + bottomIsEnemy);
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
            // Landing on enemy double, turn it into own double
            ownOnEnemy |= (1L << toIndex);
            enemyDoubles &= ~(1L << toIndex);
        } else if ((enemySingles & (1L << toIndex)) != 0) {
            // Landing on enemy single, turn it into own single
            ownSingles |= (1L << toIndex);
            enemySingles &= ~(1L << toIndex);
        } else { //TODO: hope all cases are covered and nothing forgotten
            // Regular move to empty space, place the top of the double as a single
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

        long jumpable = (emptySpaces | (isRed ? redSingles : blueSingles));
        // Forward moves (no capture)
        long forwardMoves = shift(singles, direction) & jumpable;//Removes all occupied spaces, TODO: maybe read doubleGenesis
        //commentedBits("Fwd:",forwardMoves);
        // Side moves (left and right)
        long leftMoves = shift(singles & NOT_A_FILE, -1) & jumpable;
        long rightMoves = shift(singles & NOT_H_FILE, 1) & jumpable;

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


/*    public long getPossibleMovesDoubles(long doubles, boolean isRed){//TODO: FIX KNIGHTS ON EDGES WRONG POSSIBLE MOVES
        int[] moves = {17, 15, 10, 6};

        // All occupied spaces
        long occupiedSpaces = redSingles | blueSingles | redDoubles | blueDoubles | red_on_blue | blue_on_red;
        long emptySpaces = ~occupiedSpaces & CORNER_MASK; // All empty spaces, excluding corners

        //long emptyOrSingleDoubleable = (emptySpaces | (isRed ? redSingles : blueSingles) | (isRed? redDoubles : blueDoubles));
        long jumpable = (emptySpaces | (redSingles|blueSingles) | (isRed?blueDoubles:redDoubles) | (isRed?blue_on_red:red_on_blue));

        //All possible moves for doubles. We can capture on all 4 fields, though do we need extra capture?
*//*        long twoLeftOneForwardMoves = shift(doubles & (isRed?NOT_AB_FILE:NOT_GH_FILE), isRed ? moves[1] : -moves[1]) & jumpable;
        long twoForwardOneLeftMoves = shift(doubles & NOT_AB_FILE, isRed ? moves[3] : -moves[3]) & jumpable;

        long twoRightOneForwardMoves = shift(doubles & NOT_GH_FILE, isRed ? moves[0] : -moves[0]) & jumpable;
        long twoForwardOneRightMoves = shift(doubles & NOT_GH_FILE, isRed ? moves[2] : -moves[2]) & jumpable;*//*
        long twoLeftOneForwardMoves, twoForwardOneLeftMoves, twoRightOneForwardMoves, twoForwardOneRightMoves;

        if (isRed) {
            twoLeftOneForwardMoves = shift(doubles & NOT_AB_FILE, moves[1]) & jumpable;
            twoForwardOneLeftMoves = shift(doubles & NOT_A_FILE, moves[3]) & jumpable;

            twoRightOneForwardMoves = shift(doubles & NOT_GH_FILE, moves[0]) & jumpable;
            twoForwardOneRightMoves = shift(doubles & NOT_H_FILE, moves[2]) & jumpable;
        } else {
            twoLeftOneForwardMoves = shift(doubles & NOT_GH_FILE, -moves[1]) & jumpable;
            twoForwardOneLeftMoves = shift(doubles & NOT_H_FILE, -moves[3]) & jumpable;

            twoRightOneForwardMoves = shift(doubles & NOT_AB_FILE, -moves[0]) & jumpable;
            twoForwardOneRightMoves = shift(doubles & NOT_A_FILE, -moves[2]) & jumpable;
        }


        System.out.println("BB moves,"+isRed);
        displayBitboard(twoLeftOneForwardMoves | twoForwardOneLeftMoves | twoRightOneForwardMoves | twoForwardOneRightMoves);
        return twoLeftOneForwardMoves | twoForwardOneLeftMoves | twoRightOneForwardMoves | twoForwardOneRightMoves;
    }*/

    public long getPossibleMovesDoubles(long doubles, boolean isRed) {//FIXED
        // All occupied spaces
        long occupiedSpaces = redSingles | blueSingles | redDoubles | blueDoubles | red_on_blue | blue_on_red;
        long emptySpaces = ~occupiedSpaces & CORNER_MASK; // All empty spaces, excluding corners

        //long emptyOrSingleDoubleable = (emptySpaces | (isRed ? redSingles : blueSingles) | (isRed? redDoubles : blueDoubles));
        long jumpable = (emptySpaces | (redSingles | blueSingles) | (isRed ? blueDoubles : redDoubles) | (isRed ? blue_on_red : red_on_blue));

        long twoForwardOneLeft = shift(doubles & (isRed ? NOT_A_FILE : NOT_H_FILE), isRed ? 15 : -15);
        long oneForwardTwoLeft = shift(doubles & (isRed ? NOT_AB_FILE : NOT_GH_FILE), isRed ? 6 : -6);

        long twoForwardOneRight = shift(doubles & (isRed ? NOT_H_FILE : NOT_A_FILE), isRed ? 17 : -17);
        long oneForwardTwoRight = shift(doubles & (isRed ? NOT_GH_FILE : NOT_AB_FILE), isRed ? 10 : -10);

        return jumpable & (twoForwardOneLeft | oneForwardTwoLeft | twoForwardOneRight | oneForwardTwoRight);
        //All possible moves for doubles. We can capture on all 4 fields, though do we need extra capture?

/*        long twoLeftOneForwardMoves = shift(doubles & (isRed?NOT_AB_FILE:NOT_GH_FILE), isRed?15:-15) & jumpable;
        long twoForwardOneLeftMoves = shift(doubles & (isRed?NOT_A_FILE:NOT_H_FILE), isRed?6:-6) & jumpable;
        long twoRightOneForwardMoves = shift(doubles & (isRed?NOT_GH_FILE:NOT_AB_FILE), isRed?17:-17) & jumpable;
        long twoForwardOneRightMoves = shift(doubles & (isRed?NOT_H_FILE:NOT_A_FILE), isRed?10:-10) & jumpable;*/

/*        long twoForwardOneLeft = shift(doubles & (isRed?NOT_H_FILE:NOT_A_FILE), isRed?15:-15) & jumpable;
        long oneForwardTwoLeft = shift(doubles & (isRed?NOT_GH_FILE:NOT_AB_FILE), isRed?6:-6) & jumpable;
        System.out.println("HERE");
        Tools.displayBitboard(oneForwardTwoLeft);
        Tools.displayBitboard(doubles);
        Tools.displayBitboard((isRed?NOT_GH_FILE:NOT_AB_FILE));


        long twoForwardOneRight = shift(doubles & (isRed?NOT_A_FILE:NOT_H_FILE), isRed?17:-17) & jumpable;
        long oneForwardTwoRight = shift(doubles & (isRed?NOT_AB_FILE:NOT_GH_FILE), isRed?10:-10) & jumpable;*/

        /*      System.out.println("HERE");
        Tools.displayBitboard(oneForwardTwoLeft);
        Tools.displayBitboard(doubles);
        Tools.displayBitboard((isRed?NOT_GH_FILE:NOT_AB_FILE));*/
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

        return moves.stream().sorted().collect(Collectors.toList());
    }

    /**
     * Generates a list of moves from a given set of pieces and their possible moves.
     * This method iterates through each bit of the pieces' bitboard, checks if a piece is present,
     * and then calculates valid moves for it using another provided bitboard of possible moves.
     *
     * @param pieces        A long representing the bitboard of pieces.
     * @param possibleMoves A long representing the bitboard of all possible moves for these pieces.
     * @param isRed         A boolean indicating if the current moves are for Red pieces.
     * @return A list of moves in the format "from-to" like "E2-E4".
     */
    private List<String> generateMovesForPieces(long pieces, long possibleMoves, boolean isRed) {
        List<String> moveList = new ArrayList<>();
        for (byte fromIndex = 0; fromIndex < 64; fromIndex++) {
            if ((pieces & (1L << fromIndex)) != 0) {  // There is a piece at fromIndex
                long movesFromThisPiece = possibleMoves & getPossibleMovesForIndividualPiece(fromIndex, isRed);
                for (byte toIndex = 0; toIndex < 64; toIndex++) {
                    if ((movesFromThisPiece & (1L << toIndex)) != 0) {  // Valid move to toIndex
                        moveList.add(indexToStringPosition(fromIndex) + "-" + indexToStringPosition(toIndex));
                    }
                }
            }
        }
        return moveList;
    }

    public long getPossibleMovesForIndividualPiece(byte index, boolean isRed) {
        long singlePieceMask = 1L << index;
        long moves = 0L;

        // Überprüfen, ob es sich um einen Einzelstein handelt
        if (((isRed ? redSingles : blueSingles) & singlePieceMask) != 0) {
            // Erzeuge ein Bitboard, das nur diesen Stein enthält
            // Rufe die vorhandene Methode auf, um mögliche Züge für diesen einen Stein zu ermitteln
            moves |= getPossibleMovesSingles(singlePieceMask, isRed);
        }

        // Überprüfen, ob es sich um einen Doppelstein handelt
        if (((isRed ? (redDoubles | red_on_blue) : (blueDoubles | blue_on_red)) & singlePieceMask) != 0) {
            // Erzeuge ein Bitboard, das nur diesen Stein enthält

            // Rufe die vorhandene Methode auf, um mögliche Züge für diesen einen Doppelstein zu ermitteln
            moves |= getPossibleMovesDoubles(singlePieceMask, isRed);
        }

        return moves;
    }

/*    public BitBoard longToBit (long[] bitBoards){ removed, member method taking performance and readability
        BitBoard newBoard = new BitBoard();
        newBoard.redSingles = bitBoards[0];
        newBoard.blueSingles = bitBoards[1];
        newBoard.redDoubles = bitBoards[2];
        newBoard.blueDoubles = bitBoards[3];
        newBoard.red_on_blue = bitBoards[4];
        newBoard.blue_on_red = bitBoards[5];
        return newBoard;
    }*/

    public static BitBoard fromLongArray(long[] bitBoards) {
        BitBoard newBoard = new BitBoard();
        newBoard.redSingles = bitBoards[0];
        newBoard.blueSingles = bitBoards[1];
        newBoard.redDoubles = bitBoards[2];
        newBoard.blueDoubles = bitBoards[3];
        newBoard.red_on_blue = bitBoards[4];
        newBoard.blue_on_red = bitBoards[5];
        return newBoard;
    }

    public static void main(String[] args) {
        // Example usage
        String move = "A8-H1";
        byte[] indices = parseMove(move);
        System.out.println("From Index: " + indices[0] + ", To Index: " + indices[1]);

    }

    public void print(){
        System.out.println(this);
    }

    public void printCommented(String comment){//Also shifts on purpose to make it more obvious
        System.out.println("\n"+"_".repeat(70)+"\n"+comment+"\n|\t\t\t"+this.toString().replace("\n","\n|\t\t\t")+"\n|"+"_".repeat(70));
    }

    public void printWithBitboard(String comment, long bitboard){//Also shifts on purpose to make it more obvious
        ///System.out.println(comment+"\n|\t\t\t"+this.toString().replace("\n","\n|\t\t\t")+"\n|"+"_".repeat(50));
        String toString = toString();
        String[] toStringSplit = toString.split("\n");
        String[] bitboardString = Tools.bitboardAsString(bitboard).split("\n");

        StringBuilder b = new StringBuilder(toStringSplit[0]+"\n");
        for (int i = 0; i < bitboardString.length; i++) {
            b.append(toStringSplit[i + 1]).append("  ").append(bitboardString[i]).append("\n");
        }
        b.append(toStringSplit[toStringSplit.length-1]);
        System.out.println("\n"+"_".repeat(70));
        System.out.println(comment+"\n|\t\t\t"+b.toString().replace("\n","\n|\t\t\t")+"\n| "+toFEN()+"\n|"+"_".repeat(70));
    }

    /**
     * Method using emojis 😎 to display the JumpSturdy board effectively
     *
     * @return String representation with Emojis and multiple control characters for advanced visualisation
     * Credits: Merthan Erdem
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Append column labels (A-H)
        sb.append("   A  B  C  D   E  F  G  H\n");

        for (int row = 0; row < 8; row++) {
            // Append row number
            sb.append((8 - row)).append(" ");
            // 😎😎😎😎😎😎😎😎😎
            for (int col = 0; col < 8; col++) {
                int index = row * 8 + col;
                if (index == 0 || index == 7 || index == 56 || index == 63) {// all corners
                    sb.append("\uD83D\uDD33 ");//🔳
                    continue;
                }
                if ((redSingles & (1L << index)) != 0) {
                    sb.append("\uD83D\uDD34 ");//🔴   😎
                } else if ((blueSingles & (1L << index)) != 0) {
                    sb.append("\uD83D\uDD35 ");//🔵
                } else if ((redDoubles & (1L << index)) != 0) {
                    sb.append("\uD83D\uDFE5 ");//🟥
                } else if ((blueDoubles & (1L << index)) != 0) {
                    sb.append("\uD83D\uDFE6 ");//🟦
                } else if ((blue_on_red & (1L << index)) != 0) {
                    sb.append("\u001B[41m\uD83D\uDD35\u001B[0m ");// NO FITTING EMOJI, uses Red Background Control Characters - blue on red
                } else if ((red_on_blue & (1L << index)) != 0) {
                    sb.append("\u001B[44m\uD83D\uDD34\u001B[0m ");// NO FITTING EMOJI, uses Blue Background Control Characters - blue on red
                } else {
                    sb.append("⬜ ");
                }
            }
            sb.append(" ").append(8 - row).append("\n"); // Append row number
        }
        sb.append("   A  B  C  D   E  F  G  H\n");
        return sb.toString();
    }


}