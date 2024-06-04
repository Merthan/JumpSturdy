package ai;

import misc.Tools;
import model.BitBoard;

import java.util.ArrayList;
import java.util.List;

import static model.BitBoard.*;

import static misc.Tools.shift;

public class BitBoardManipulation {

    public static final int LOWER_SIX_BIT_MASK = 0x3F;

    public static final int RUHESUCHE_NOT_PERFORMED = Integer.MIN_VALUE;

    // No checks, move at this point must have been generated valid anyways. Returns long[] of modified bitboards.
    // No board passed for performance reasons again, only pass board when its called once, not when called in a tree etc
    public static long[] doMoveAndReturnModifiedBitBoards(byte from, byte to, boolean isRedTurn, long redSingles, long blueSingles, long redDoubles, long blueDoubles, long red_on_blue, long blue_on_red) {
        if (isRedTurn) {
            if ((redDoubles != 0 && (redDoubles & (1L << from)) != 0) || (red_on_blue != 0 && (red_on_blue & (1L << from)) != 0)) {
                return moveDoublePieceOnBitBoards(from, to, true, redSingles, blueSingles, redDoubles, blueDoubles, red_on_blue, blue_on_red);
            } else {
                return moveSinglePieceOnBitBoards(from, to, true, redSingles, blueSingles, redDoubles, blueDoubles, red_on_blue, blue_on_red);
            }
        } else {
            if ((blueDoubles != 0 && (blueDoubles & (1L << from)) != 0) || (blue_on_red != 0 && (blue_on_red & (1L << from)) != 0)) {
                return moveDoublePieceOnBitBoards(from, to, false, redSingles, blueSingles, redDoubles, blueDoubles, red_on_blue, blue_on_red);
            } else {
                return moveSinglePieceOnBitBoards(from, to, false, redSingles, blueSingles, redDoubles, blueDoubles, red_on_blue, blue_on_red);
            }
        }
    }


    public static long[] moveSinglePieceOnBitBoards(byte fromIndex, byte toIndex, boolean isRed, long redSingles, long blueSingles, long redDoubles, long blueDoubles, long red_on_blue, long blue_on_red) {
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
                isRed ? ownOnEnemy : enemyOnOwn,//isRed = red_on_blue else blue_on_red
                isRed ? enemyOnOwn : ownOnEnemy//isRed= blue_onRed
        };

    }


    public static long[] moveDoublePieceOnBitBoards(byte fromIndex, byte toIndex, boolean isRed, long redSingles, long blueSingles, long redDoubles, long blueDoubles, long red_on_blue, long blue_on_red) {
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
                isRed ? ownOnEnemy : enemyOnOwn,//isRed = red_on_blue else blue_on_red
                isRed ? enemyOnOwn : ownOnEnemy//isRed= blue_onRed
        };
    }


    public static long calculateAttackedPositions(boolean isRedTurn, long redSingles, long blueSingles, long redDoubles, long blueDoubles, long red_on_blue, long blue_on_red) {
        int direction = isRedTurn ? 8 : -8;
        long singles = isRedTurn ? redSingles : blueSingles;
        long enemyPieces = isRedTurn ? (blue_on_red | blueDoubles | blueSingles) : (redSingles | redDoubles | red_on_blue); // Enemy single figures

        long singleLeftCapture = shift(singles & NOT_A_FILE, direction - 1) & enemyPieces;
        long singleRightCapture = shift(singles & NOT_H_FILE, direction + 1) & enemyPieces;


        //Double part
        //Only jump on enemies, not empty, same etc
        long jumpable = ((isRedTurn ? blueSingles : redSingles) | (isRedTurn ? blueDoubles : redDoubles) | (isRedTurn ? blue_on_red : red_on_blue));

        long doubles = isRedTurn ? (redDoubles | red_on_blue) : (blueDoubles | blue_on_red);
        long doubleTwoForwardOneLeft = shift(doubles & (isRedTurn ? NOT_A_FILE : NOT_H_FILE), isRedTurn ? 15 : -15);
        long doubleOneForwardTwoLeft = shift(doubles & (isRedTurn ? NOT_AB_FILE : NOT_GH_FILE), isRedTurn ? 6 : -6);

        long doubleTwoForwardOneRight = shift(doubles & (isRedTurn ? NOT_H_FILE : NOT_A_FILE), isRedTurn ? 17 : -17);
        long doubleOneForwardTwoRight = shift(doubles & (isRedTurn ? NOT_GH_FILE : NOT_AB_FILE), isRedTurn ? 10 : -10);
        //double needs to be jumpable, &
        return singleLeftCapture | singleRightCapture | (jumpable & (doubleTwoForwardOneLeft | doubleOneForwardTwoLeft | doubleTwoForwardOneRight | doubleOneForwardTwoRight));
    }

    public static long calculateAttackedPositionsForBoth(long redSingles, long blueSingles, long redDoubles, long blueDoubles, long red_on_blue, long blue_on_red) {
        return calculateAttackedPositions(true, redSingles, blueSingles, redDoubles, blueDoubles, red_on_blue, blue_on_red) | calculateAttackedPositions(false, redSingles, blueSingles, redDoubles, blueDoubles, red_on_blue, blue_on_red);
    }


    public static byte possibleFromPositionForToIndex(byte toIndex, boolean isRed, long redSingles, long blueSingles, long redDoubles, long blueDoubles, long red_on_blue, long blue_on_red) {

        long indexMask = 1L << toIndex; // Create the index mask with only the bit at indexEnd set

        int direction = isRed ? 8 : -8;
        long singles = isRed ? redSingles : blueSingles;


        long tempCompared;

        //TODO: Maybe single capture should be at the start if thats supposed to be the most likely
        if(toIndex==Tools.positionToIndex("E2")){
            System.out.println();
        }

        long forwardMoves = shift(indexMask, -direction); //& jumpableBeforeMask;
        tempCompared = (forwardMoves & singles);
        if (tempCompared != 0) return (byte) Long.numberOfTrailingZeros(tempCompared);

        //capture single
        long leftCapture = shift(indexMask & NOT_A_FILE, -direction - 1) & singles;
        tempCompared = (leftCapture & singles);
        if (tempCompared != 0) return (byte) Long.numberOfTrailingZeros(tempCompared);

        long rightCapture = shift(indexMask & NOT_H_FILE, -direction + 1) & singles;
        tempCompared = (rightCapture & singles);
        if (tempCompared != 0) return (byte) Long.numberOfTrailingZeros(tempCompared);

        //For left and right moves make sure there is no enemy piece at the to position as we cant beat them with a sideways move (only own or empty)
        long enemyPieces = isRed ? (blueSingles | blueDoubles | blue_on_red) : (redSingles | redDoubles | red_on_blue);

        long leftMoves = shift(indexMask & NOT_A_FILE & (~enemyPieces), -1);//Also dont move into enemy singles sideways, not possible
        tempCompared = (leftMoves & singles);
        if (tempCompared != 0) return (byte) Long.numberOfTrailingZeros(tempCompared);
        /*System.out.println("teeetst");

        Tools.displayBitboard(leftMoves);
        Tools.displayBitboard((~(isRed?blueSingles:redSingles)));
        System.out.println("StartTT");
        Tools.displayBitboard(blueSingles);
        Tools.displayBitboard(~blueSingles);
        Tools.displayBitboard(shift(indexMask & NOT_A_FILE, -1));*/


        long rightMoves = shift(indexMask & NOT_H_FILE & (~enemyPieces), 1); //Also dont move into enemy singles sideways, not possible
        tempCompared = (rightMoves & singles);
        if (tempCompared != 0) return (byte) Long.numberOfTrailingZeros(tempCompared);


        // End of singles part-----------------------


        // Here there needs to be a double at the index before

        long doubles = isRed ? (redDoubles | red_on_blue) : (blueDoubles | blue_on_red);

        long twoForwardOneLeft = shift(indexMask & (isRed ? NOT_H_FILE : NOT_A_FILE), isRed ? -15 : 15);
        tempCompared = (twoForwardOneLeft & doubles);
        if (tempCompared != 0) return (byte) Long.numberOfTrailingZeros(tempCompared);

        long oneForwardTwoLeft = shift(indexMask & (isRed ? NOT_GH_FILE : NOT_AB_FILE), isRed ? -6 : 6);
        tempCompared = (oneForwardTwoLeft & doubles);
        if (tempCompared != 0) return (byte) Long.numberOfTrailingZeros(tempCompared);

        long twoForwardOneRight = shift(indexMask & (isRed ? NOT_A_FILE : NOT_H_FILE), isRed ? -17 : 17);
        tempCompared = (twoForwardOneRight & doubles);
        if (tempCompared != 0) return (byte) Long.numberOfTrailingZeros(tempCompared);

        long oneForwardTwoRight = shift(indexMask & (isRed ? NOT_AB_FILE : NOT_GH_FILE), isRed ? -10 : 10);
        tempCompared = (oneForwardTwoRight & doubles);
        if (tempCompared != 0) return (byte) Long.numberOfTrailingZeros(tempCompared);

        throw new IllegalStateException("No possible from index found"); // Not purpose of method to return -1 or something, throws
        //Prob reversed but doesnt matter
        //long leftMoves = shift(singles & NOT_A_FILE, -1) & jumpableBeforeMask;
        //long rightMoves = shift(singles & NOT_H_FILE, 1) & jumpableBeforeMask;

        //long leftCapture = shift(singles & NOT_A_FILE, direction - 1) & enemyPieces; //+-7 9 so diagonal
        //long rightCapture = shift(singles & NOT_H_FILE, direction + 1) & enemyPieces;

    }
    public static long counter = 0;
    public static long[] previousArray = new long[]{};
    public static int ruhesuche(BitBoard board, boolean isRed) {
        long[] bitboardAsLongArray = new long[]{board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red};
        long attackedPositions = BitBoardManipulation.calculateAttackedPositions(isRed, bitboardAsLongArray[0], bitboardAsLongArray[1], bitboardAsLongArray[2], bitboardAsLongArray[3], bitboardAsLongArray[4], bitboardAsLongArray[5]);
        if (attackedPositions == 0) return RUHESUCHE_NOT_PERFORMED;
        boolean originalIsRed = isRed;
        counter = 0;
        List<String> doneMoves = new ArrayList<>();
        while (attackedPositions != 0) {
            //This gets a specific index thats attacked, IF there are multiple the first one is returned. This is basically the TO, the from we figure out
            byte mostForwardIndexOfAttacked = (byte) Long.numberOfTrailingZeros(attackedPositions);
            //Tools.displayBitboard(attackedPositions);
            //System.out.println("bluesingle:");
            //Tools.displayBitboard(bitboardAsLongArray[1]);
            //System.out.println("MostForward:"+mostForwardIndexOfAttacked+ " "+ Tools.indexToStringPosition(mostForwardIndexOfAttacked)+ " "+ Tools.indexToStringPosition((byte) (63-mostForwardIndexOfAttacked)));
            byte from = BitBoardManipulation.possibleFromPositionForToIndex(mostForwardIndexOfAttacked, isRed, bitboardAsLongArray[0], bitboardAsLongArray[1], bitboardAsLongArray[2], bitboardAsLongArray[3], bitboardAsLongArray[4], bitboardAsLongArray[5]);
            //For test:
            //System.out.println("Attack played as: "+(isRed?"red ":"blue ") + Tools.indexToStringPosition(from)+"-"+Tools.indexToStringPosition(mostForwardIndexOfAttacked));

            if(from == 43 && mostForwardIndexOfAttacked == 36){
                //36
                Tools.printDivider(20);
                System.out.println("badmove:");
                System.out.println(board.toFEN());
                BitBoard.fromLongArray(bitboardAsLongArray).printWithBitboard("CurrentAtE4Move",attackedPositions);
                long[] next = BitBoardManipulation.doMoveAndReturnModifiedBitBoards(from, mostForwardIndexOfAttacked, isRed, bitboardAsLongArray[0], bitboardAsLongArray[1], bitboardAsLongArray[2], bitboardAsLongArray[3], bitboardAsLongArray[4], bitboardAsLongArray[5]);
                BitBoard.fromLongArray(next).printWithBitboard("Next",0);
            }

            if(from == 36 && mostForwardIndexOfAttacked == 46){
                Tools.printDivider(40);
                System.out.println("badmove2");
                BitBoard.fromLongArray(bitboardAsLongArray).printWithBitboard("CurrentAtE4MoveNEXT",attackedPositions);
                long[] next = BitBoardManipulation.doMoveAndReturnModifiedBitBoards(from, mostForwardIndexOfAttacked, isRed, bitboardAsLongArray[0], bitboardAsLongArray[1], bitboardAsLongArray[2], bitboardAsLongArray[3], bitboardAsLongArray[4], bitboardAsLongArray[5]);
                BitBoard.fromLongArray(next).printWithBitboard("NextNEXT",0);
            }

            bitboardAsLongArray = BitBoardManipulation.doMoveAndReturnModifiedBitBoards(from, mostForwardIndexOfAttacked, isRed, bitboardAsLongArray[0], bitboardAsLongArray[1], bitboardAsLongArray[2], bitboardAsLongArray[3], bitboardAsLongArray[4], bitboardAsLongArray[5]);

            doneMoves.add(Tools.indexToStringPosition(from)+"-"+Tools.indexToStringPosition(mostForwardIndexOfAttacked));
            //After playing move, switch sides I guess to see what they'd play
            if(board.toFEN().equals("1bb3b0/4r03/1b01b02b0b0/4rr1b01/8/8/2r03r01/r01r0r0r0r0")){
                System.out.println("Attacked: isred:"+isRed+ " counter "+counter);
                Tools.displayBitboard(attackedPositions);
            }


            System.out.println("GeneralFEN:"+BitBoard.fromLongArray(bitboardAsLongArray).toFEN());
            BitBoard.fromLongArray(bitboardAsLongArray).printCommented("B:Counter "+counter);

            isRed = !isRed;
            attackedPositions = BitBoardManipulation.calculateAttackedPositions(isRed, bitboardAsLongArray[0], bitboardAsLongArray[1], bitboardAsLongArray[2], bitboardAsLongArray[3], bitboardAsLongArray[4], bitboardAsLongArray[5]);
            counter++;


            try{
                BitBoard.detectOverlap( bitboardAsLongArray[0], bitboardAsLongArray[1], bitboardAsLongArray[2], bitboardAsLongArray[3], bitboardAsLongArray[4], bitboardAsLongArray[5]);
            }catch (IllegalStateException E){
                Tools.printDivider();
                System.out.println("Move causing overlap: "+Tools.indexToStringPosition(from)+"-"+Tools.indexToStringPosition(mostForwardIndexOfAttacked)+ "as Red="+isRed);
                System.out.println(board);
                System.out.println(board.toFEN());
                System.out.println(doneMoves);
                System.out.println(BitBoard.fromLongArray(bitboardAsLongArray));
                BitBoard.fromLongArray(bitboardAsLongArray).printWithBitboard("EndWithAttacked",attackedPositions);


                System.out.println("Previous board:");
                System.out.println(BitBoard.fromLongArray(previousArray));

                System.out.println("Overlap2");

                board.doMove("D3-E4",false,true);
                System.out.println(board);
                board.doMove("E4-G3",true,true);


                System.exit(0);
            }

            if(counter>50){
                System.out.println("Cursed board: "+Tools.indexToStringPosition(from)+"-"+Tools.indexToStringPosition(mostForwardIndexOfAttacked));
                System.out.println(board);
                System.out.println(BitBoard.fromLongArray(bitboardAsLongArray));
                board.detectOverlap();
                System.out.println("firstoverlap");
                System.out.println(doneMoves);
                BitBoard.detectOverlap( bitboardAsLongArray[0], bitboardAsLongArray[1], bitboardAsLongArray[2], bitboardAsLongArray[3], bitboardAsLongArray[4], bitboardAsLongArray[5]);
                System.exit(0);
            }

        }

        previousArray = bitboardAsLongArray.clone();
        System.out.println("Ruhesuche end"+"X".repeat(50));
        //As soon as no attacks left anymore, calculate for starting party
        //TODO: remove the minus if evaluate is fixed to account for both teams
        //TODO: change return to what you guys might need, e.g. more than just evaluation int.
        return Evaluate.evaluateSimple(originalIsRed, bitboardAsLongArray[0], bitboardAsLongArray[1], bitboardAsLongArray[2], bitboardAsLongArray[3], bitboardAsLongArray[4], bitboardAsLongArray[5]) -
                Evaluate.evaluateSimple(!originalIsRed, bitboardAsLongArray[0], bitboardAsLongArray[1], bitboardAsLongArray[2], bitboardAsLongArray[3], bitboardAsLongArray[4], bitboardAsLongArray[5]);
    }

    public static int[] ruhesucheWithPositions(BitBoard board, boolean isRed) {
        long[] bitboardAsLongArray = new long[]{board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red};
        long attackedPositions = BitBoardManipulation.calculateAttackedPositions(isRed, bitboardAsLongArray[0], bitboardAsLongArray[1], bitboardAsLongArray[2], bitboardAsLongArray[3], bitboardAsLongArray[4], bitboardAsLongArray[5]);
        if (attackedPositions == 0) return null;
        int[] positionsAndAtTheEndScoreArray = new int[12 * 2 + 1]; //12 players each, estimate for max captures/possible MAXIMUM array size, better than List with boxing
        int counter = 0;
        boolean originalIsRed = isRed;
        while (attackedPositions != 0) {
            //This gets a specific index thats attacked, IF there are multiple the first one is returned. This is basically the TO, the from we figure out
            byte mostForwardIndexOfAttacked = (byte) Long.numberOfTrailingZeros(attackedPositions);
            //Tools.displayBitboard(attackedPositions);
            //System.out.println("bluesingle:");
            //Tools.displayBitboard(bitboardAsLongArray[1]);
            //System.out.println("MostForward:"+mostForwardIndexOfAttacked+ " "+ Tools.indexToStringPosition(mostForwardIndexOfAttacked)+ " "+ Tools.indexToStringPosition((byte) (63-mostForwardIndexOfAttacked)));
            byte from = BitBoardManipulation.possibleFromPositionForToIndex(mostForwardIndexOfAttacked, isRed, bitboardAsLongArray[0], bitboardAsLongArray[1], bitboardAsLongArray[2], bitboardAsLongArray[3], bitboardAsLongArray[4], bitboardAsLongArray[5]);
            //For test:
            //System.out.println("Attack played as: "+(isRed?"red ":"blue ") + Tools.indexToStringPosition(from)+"-"+Tools.indexToStringPosition(mostForwardIndexOfAttacked));

            bitboardAsLongArray = BitBoardManipulation.doMoveAndReturnModifiedBitBoards(from, mostForwardIndexOfAttacked, isRed, bitboardAsLongArray[0], bitboardAsLongArray[1], bitboardAsLongArray[2], bitboardAsLongArray[3], bitboardAsLongArray[4], bitboardAsLongArray[5]);

            positionsAndAtTheEndScoreArray[counter++] = from;
            positionsAndAtTheEndScoreArray[counter++] = mostForwardIndexOfAttacked;
            //After playing move, switch sides I guess to see what they'd play
            isRed = !isRed;
            attackedPositions = BitBoardManipulation.calculateAttackedPositions(isRed, bitboardAsLongArray[0], bitboardAsLongArray[1], bitboardAsLongArray[2], bitboardAsLongArray[3], bitboardAsLongArray[4], bitboardAsLongArray[5]);
        }
        //As soon as no attacks left anymore, calculate for starting party
        //TODO: remove the minus if evaluate is fixed to account for both teams
        //TODO: change return to what you guys might need, e.g. more than just evaluation int.

        int eval = Evaluate.evaluateSimple(originalIsRed, bitboardAsLongArray[0], bitboardAsLongArray[1], bitboardAsLongArray[2], bitboardAsLongArray[3], bitboardAsLongArray[4], bitboardAsLongArray[5]) -
                Evaluate.evaluateSimple(!originalIsRed, bitboardAsLongArray[0], bitboardAsLongArray[1], bitboardAsLongArray[2], bitboardAsLongArray[3], bitboardAsLongArray[4], bitboardAsLongArray[5]);

        positionsAndAtTheEndScoreArray[positionsAndAtTheEndScoreArray.length - 1] = eval;
        return positionsAndAtTheEndScoreArray; // TODO: READ [0] [1] = one move 0 to 1, then [2] [3] until one of the values is 0 (illegal/corner anyways), then break loop. Eval is at last index.
    }


    static final long secondLastRowCenter = 0x007E000000000000L;
    static final long secondTopRowCenter = 0x0000000000007E00L;
    static final long secondThirdRowTop = 0x0000000000FFFF00L;
    static final long secondThirdRowBottom = 0x00FFFF0000000000L;

    //For singles, only forward is checked as win captures/diagonal would just result in the enemy capturing them first
    public static boolean doesNextMoveWin(boolean isRedTurn, long redSingles, long blueSingles, long redDoubles, long blueDoubles, long red_on_blue, long blue_on_red) {
        long attackedPositions; // Not set here for performance reasons, only once needed

        if (isRedTurn) {

            long onLast = secondLastRowCenter & redSingles;
            //is on secondlast and is there any blocking enemy right beneath, shifted 8 bits
            if (onLast != 0 && ((onLast << 8) & (blueSingles|blueDoubles|blue_on_red)) == 0) {
                //calculate the positions where we are attacked currently
                attackedPositions = calculateAttackedPositions(false, redSingles, blueSingles, redDoubles, blueDoubles, red_on_blue, blue_on_red);
                if ((onLast & ~attackedPositions) != 0) { //Onlast minus attackedpositions isnt 0, meaning at least one thats not attacked
                    //System.out.println("Next move wins single");
                    return true;
                }
            }

            long onPreRows = (redDoubles | red_on_blue) & secondThirdRowBottom; // Any jumping on the row end-1 or end-2?
            if(onPreRows!=0){
                attackedPositions = calculateAttackedPositions(false, redSingles, blueSingles, redDoubles, blueDoubles, red_on_blue, blue_on_red);
                if((onPreRows & ~attackedPositions) !=0){//If there is at least one onPre that is NOT attacked
                    System.out.println("Next move wins double");
                    return true;
                }
            }


        } else {

            long onLast = secondTopRowCenter & blueSingles;
            //is on secondlast and is there any blocking enemy right above, shifted 8 bits
            if (onLast != 0 && ((onLast >>> 8) & (redSingles|redDoubles|red_on_blue)) == 0) {
                //calculate the positions where we are attacked currently
                attackedPositions = calculateAttackedPositions(true, redSingles, blueSingles, redDoubles, blueDoubles, red_on_blue, blue_on_red);
                if ((onLast & ~attackedPositions) != 0) {
                    System.out.println("Next move wins single b");
                    return true;
                }
            }

            long onPreRows = (blueDoubles | blue_on_red) & secondThirdRowTop; // Any jumping on the row end-1 or end-2?
            if(onPreRows!=0){
                attackedPositions = calculateAttackedPositions(true, redSingles, blueSingles, redDoubles, blueDoubles, red_on_blue, blue_on_red);
                if((onPreRows & ~attackedPositions) !=0){//If there is at least one onPre that is NOT attacked
                    System.out.println("Next move wins double b");
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        long l = 0x000000000000003FL;
        Tools.displayBitboard( l);
        System.out.println(Long.toBinaryString(l));
    }

}
