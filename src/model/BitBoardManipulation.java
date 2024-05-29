package model;

import static model.BitBoard.*;
import static model.Tools.shift;

public class BitBoardManipulation {

    public static final int LOWER_SIX_BIT_MASK = 0x3F;

    // No checks, move at this point must have been generated valid anyways. Returns long[] of modified bitboards.
    // No board passed for performance reasons again, only pass board when its called once, not when called in a tree etc
    public static long[] doMoveAndReturnModifiedBitBoards(byte from,byte to, boolean isRedTurn, long redSingles, long blueSingles,long redDoubles,long blueDoubles, long red_on_blue,long blue_on_red) {
        if (isRedTurn) {
            if ((redDoubles != 0 && (redDoubles & (1L << from)) != 0) || (red_on_blue != 0 && (red_on_blue & (1L << from)) != 0)) {
                return moveDoublePieceOnBitBoards(from, to, true, redSingles, blueSingles, redDoubles, blueDoubles, blue_on_red, red_on_blue);
            } else {
                return moveSinglePieceOnBitBoards(from, to, true, redSingles, blueSingles, redDoubles, blueDoubles, blue_on_red, red_on_blue);
            }
        } else {
            if ((blueDoubles != 0 && (blueDoubles & (1L << from)) != 0)|| (blue_on_red != 0 && (blue_on_red & (1L << from)) != 0)) {
                return moveDoublePieceOnBitBoards(from, to, false, redSingles, blueSingles, redDoubles, blueDoubles, blue_on_red, red_on_blue);
            } else {
                return moveSinglePieceOnBitBoards(from, to, false, redSingles, blueSingles, redDoubles, blueDoubles, blue_on_red, red_on_blue);
            }
        }
    }


    public static long[] moveSinglePieceOnBitBoards(byte fromIndex, byte toIndex, boolean isRed,  long redSingles, long blueSingles,long redDoubles,long blueDoubles, long red_on_blue, long blue_on_red) {
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
        return new long[]{
                isRed ? ownSingles : enemySingles,
                isRed ? enemySingles : ownSingles,
                isRed ? ownDoubles : enemyDoubles,
                isRed ? enemyDoubles : ownDoubles,
                isRed ? enemyOnOwn : ownOnEnemy,
                isRed ? ownOnEnemy : enemyOnOwn
        };

    }


    public static long[] moveDoublePieceOnBitBoards(byte fromIndex, byte toIndex, boolean isRed, long redSingles, long blueSingles,long redDoubles,long blueDoubles, long red_on_blue, long blue_on_red ) {
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
        return new long[]{
                isRed ? ownSingles : enemySingles,
                isRed ? enemySingles : ownSingles,
                isRed ? ownDoubles : enemyDoubles,
                isRed ? enemyDoubles : ownDoubles,
                isRed ? enemyOnOwn : ownOnEnemy,
                isRed ? ownOnEnemy : enemyOnOwn
        };
    }


    public static long calculateAttackedPositions(boolean isRedTurn, long redSingles, long blueSingles,long redDoubles,long blueDoubles, long red_on_blue,long blue_on_red){
        int direction = isRedTurn ? 8 : -8;
        long singles = isRedTurn?redSingles:blueSingles;
        long enemyPieces = isRedTurn ? (blue_on_red | blueDoubles | blueSingles) : (redSingles | redDoubles | red_on_blue); // Enemy single figures

        long leftCapture = shift(singles & NOT_A_FILE, direction - 1) & enemyPieces;
        long rightCapture = shift(singles & NOT_H_FILE, direction + 1) & enemyPieces;



        //Double part
        //Only jump on enemies, not empty, same etc
        long jumpable = ((isRedTurn?blueSingles:redSingles) | (isRedTurn?blueDoubles:redDoubles) | (isRedTurn?blue_on_red:red_on_blue));

        long doubles = isRedTurn? (redDoubles|red_on_blue):(blueDoubles|blue_on_red);
        long doubleTwoForwardOneLeft = shift(doubles & (isRedTurn?NOT_A_FILE:NOT_H_FILE), isRedTurn ? 15 : -15);
        long doubleOneForwardTwoLeft = shift(doubles & (isRedTurn?NOT_AB_FILE:NOT_GH_FILE), isRedTurn ? 6 : -6);

        long doubleTwoForwardOneRight = shift(doubles & (isRedTurn?NOT_H_FILE:NOT_A_FILE), isRedTurn ? 17 : -17);
        long doubleOneForwardTwoRight = shift(doubles & (isRedTurn?NOT_GH_FILE:NOT_AB_FILE), isRedTurn ? 10 : -10);

        return leftCapture | rightCapture | (jumpable& (doubleTwoForwardOneLeft|doubleOneForwardTwoLeft|doubleTwoForwardOneRight|doubleOneForwardTwoRight) );
    }

    public static long calculateAttackedPositionsForBoth(long redSingles, long blueSingles,long redDoubles,long blueDoubles, long red_on_blue,long blue_on_red){
        return calculateAttackedPositions(true,redSingles,blueSingles,redDoubles,blueDoubles,red_on_blue,blue_on_red) | calculateAttackedPositions(false,redSingles,blueSingles,redDoubles,blueDoubles,red_on_blue,blue_on_red);
    }



    public static byte possibleFromPositionForToIndex(byte toIndex,boolean isRed, long redSingles, long blueSingles,long redDoubles,long blueDoubles, long red_on_blue,long blue_on_red ){

        long indexMask = 1L << toIndex; // Create the index mask with only the bit at indexEnd set

        int direction = isRed ? 8 : -8;
        long singles = isRed?redSingles:blueSingles;


        long tempCompared;

        //TODO: Maybe single capture should be at the start if thats supposed to be the most likely



        long forwardMoves = shift(indexMask, -direction); //& jumpableBeforeMask;
        tempCompared =(forwardMoves & singles);
        if(tempCompared != 0) return (byte) Long.numberOfTrailingZeros(tempCompared);

        //capture single
        long leftCapture = shift(indexMask & NOT_A_FILE, -direction - 1) & singles;
        tempCompared =(leftCapture& singles);
        if(tempCompared != 0) return (byte) Long.numberOfTrailingZeros(tempCompared);

        long rightCapture = shift(indexMask & NOT_H_FILE, -direction + 1) & singles;
        tempCompared =(rightCapture& singles);
        if(tempCompared != 0) return (byte) Long.numberOfTrailingZeros(tempCompared);


        long leftMoves = shift(indexMask & NOT_A_FILE, -1);
        tempCompared =(leftMoves& singles);
        if(tempCompared != 0) return (byte) Long.numberOfTrailingZeros(tempCompared);

        long rightMoves = shift(indexMask & NOT_H_FILE, 1);
        tempCompared =(rightMoves& singles);
        if(tempCompared != 0) return (byte) Long.numberOfTrailingZeros(tempCompared);



        // End of singles part-----------------------


        // Here there needs to be a double at the index before

        long doubles = isRed?(redDoubles|red_on_blue):(blueDoubles|blue_on_red);

        long twoForwardOneLeft = shift(indexMask & (isRed ? NOT_H_FILE : NOT_A_FILE), isRed ? -15 : 15);
        tempCompared =(twoForwardOneLeft & doubles);
        if(tempCompared != 0) return (byte) Long.numberOfTrailingZeros(tempCompared);

        long oneForwardTwoLeft = shift(indexMask & (isRed ? NOT_GH_FILE : NOT_AB_FILE), isRed ? -6 : 6);
        tempCompared =(oneForwardTwoLeft & doubles);
        if(tempCompared != 0) return (byte) Long.numberOfTrailingZeros(tempCompared);

        long twoForwardOneRight = shift(indexMask & (isRed ? NOT_A_FILE : NOT_H_FILE), isRed ? -17 : 17);
        tempCompared =(twoForwardOneRight & doubles);
        if(tempCompared != 0) return (byte) Long.numberOfTrailingZeros(tempCompared);

        long oneForwardTwoRight = shift(indexMask & (isRed ? NOT_AB_FILE : NOT_GH_FILE), isRed ? -10 : 10);
        tempCompared =(oneForwardTwoRight & doubles);
        if(tempCompared != 0) return (byte) Long.numberOfTrailingZeros(tempCompared);

        throw new IllegalStateException("No possible from index found"); // Not purpose of method to return -1 or something, throws
        //Prob reversed but doesnt matter
        //long leftMoves = shift(singles & NOT_A_FILE, -1) & jumpableBeforeMask;
        //long rightMoves = shift(singles & NOT_H_FILE, 1) & jumpableBeforeMask;

        //long leftCapture = shift(singles & NOT_A_FILE, direction - 1) & enemyPieces; //+-7 9 so diagonal
        //long rightCapture = shift(singles & NOT_H_FILE, direction + 1) & enemyPieces;

    }



    }
