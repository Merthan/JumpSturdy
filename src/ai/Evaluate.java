package ai;

import static ai.BitBoardManipulation.*;
import static model.BitBoard.bottomRowMask;
import static model.BitBoard.topRowMask;

public class Evaluate {
    /**
     * Shortened names cause they might be passed lots of times br= red_on_blue as in the board
     * Passing board would be simpler, but better performance without object
     * Copy this for most:
     * boolean redsTurn, long r, long b, long rr, long bb, long br, long rb
     */


    public static int evaluateMove(boolean redsTurn, byte fromIndex, byte toIndex, long r, long b, long rr, long bb, long br, long rb) {
        long[] bitboards = BitBoardManipulation.doMoveAndReturnModifiedBitBoards(fromIndex, toIndex, redsTurn, r, b, rr, bb, br, rb);
        if (redsTurn) {//Converting the method based on which caller, can be refactored in the future when BitBoardManipulation is changed
            return evaluateSimple(true, bitboards[0], bitboards[1], bitboards[2], bitboards[3], bitboards[5], bitboards[4]);
        } else {
            return evaluateSimple(false, bitboards[1], bitboards[0], bitboards[3], bitboards[2], bitboards[4], bitboards[5]);
        }
    }

    public static int evaluateMultipleMoves(boolean redsTurn, byte[] fromIndex, byte[] toIndex, long r, long b, long rr, long bb, long br, long rb) {
        long[] bitboards = BitBoardManipulation.doMoveAndReturnModifiedBitBoards(fromIndex[0], toIndex[0], redsTurn, r, b, rr, bb, br, rb);
        for (int i = 1; i < fromIndex.length; i++) {
            bitboards = BitBoardManipulation.doMoveAndReturnModifiedBitBoards(fromIndex[i], toIndex[i], redsTurn,
                    bitboards[redsTurn ? 0 : 1], bitboards[redsTurn ? 1 : 0], bitboards[redsTurn ? 2 : 3], bitboards[redsTurn ? 3 : 2], bitboards[redsTurn ? 5 : 4], bitboards[redsTurn ? 4 : 5]);
        }
        if (redsTurn) {//Converting the method based on which caller, can be refactored in the future when BitBoardManipulation is changed
            return evaluateSimple(true, bitboards[0], bitboards[1], bitboards[2], bitboards[3], bitboards[5], bitboards[4]);
        } else {
            return evaluateSimple(false, bitboards[1], bitboards[0], bitboards[3], bitboards[2], bitboards[4], bitboards[5]);
        }
    }


    public static int evaluateSimple(boolean redsTurn, long r, long b, long rr, long bb, long br, long rb) {
        int pieceWorthValue = calculatePieceWorthValue(redsTurn, r, b, rr, bb, br, rb);
        int opponentPieceWorthValue = calculatePieceWorthValue(!redsTurn, r, b, rr, bb, br, rb);
        int distanceToEnd = calculateDistanceToEnd(redsTurn, r, b, rr, bb, br, rb);
        int bonusForNearWin = calculateNearWinBonus(redsTurn, r, b, rr, bb, br, rb);

        return 2 * (8 - distanceToEnd) + (pieceWorthValue - opponentPieceWorthValue) + bonusForNearWin;
    }
    public static int evaluateComplex(boolean isRed, long r, long b, long rr, long bb, long br, long rb) {
/*        long ownSingles = isRed ? r : b;
        long enemySingles = isRed ? b : r;
        long ownDoubles = isRed ? rr : bb;
        long enemyDoubles = isRed ? bb : rr;
        long ownOnEnemy = isRed ? br : rb;
        long enemyOnOwn = isRed ? rb : br;*/

        long redFigures = (r|rr|br);
        long blueFigures = (b|bb|rb);

        //IF won
        if((redFigures&bottomRowMask) != 0){
            return isRed? Integer.MAX_VALUE : Integer.MIN_VALUE; // MAX if won as red, else MIN as other side has won
        } else if ((blueFigures & topRowMask) != 0) {
            return !isRed? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }

        //Needs to be calculated to make canWin faster
        long ownFiguresThatCanWinIfPromotedAndCloseFigures = isRed? (redFigures&secondThirdRowBottom) : (blueFigures&secondThirdRowTop);//Closer(goal) figures are handled before, so no need to include
        long enemyFiguresThatCanWinIfPromotedAndCloseFigures = !isRed? (redFigures&secondThirdRowBottom) : (blueFigures&secondThirdRowTop);


        //If winning imminent (no way to prevent by enemy side) TODO: not checking if enemy win imminent, but probably doesnt matter as... we cant prevent it anyways without going steps before
        if(ownFiguresThatCanWinIfPromotedAndCloseFigures!=0){ // If there are ANY, then we can check as its possible. However this doesnt guarantee canWin so if false we continue with normal eval below
            byte[] canWin = BitBoardManipulation.canWinWithMovesFusioned(isRed, r, b, rr, bb, br, rb);
            // If we can win, dont return MAX Value as not won yet (move remaining, dont say winning is worth the same) but make it obvious by using max-1
            if(canWin!=null){
                return Integer.MAX_VALUE- (canWin.length-1);//if 2 turns away (length 3), length-2 else if 1 turn away -1 (also so that is more favorable)
            }
        }


        //start: 12, max 12 unless all enemies turned into bottom_on_double, then a single is worth double.
        // Red_on_blue blue has zero value here, maybe too little? But worse than a single (1) cause blocked so maybe fitting
        //doubles have double the value, (at least, as 2 figures inside) red_on_blue also has 2x value (change?)
        int pieceWorthValue = Long.bitCount(isRed ? r : b) + (2 * Long.bitCount(isRed ? rr : bb)) + 2 * Long.bitCount(isRed ? br : rb);
        int enemyPieceWorthValue = Long.bitCount(!isRed ? r : b) + (2 * Long.bitCount(!isRed ? rr : bb)) + 2 * Long.bitCount(!isRed ? br : rb);

        long attackedFrom = BitBoardManipulation.calculateAttackedPositions(!isRed, r, b, rr, bb, br, rb); // Checks if attacking more than being attacked
        long attacking = BitBoardManipulation.calculateAttackedPositions(isRed, r, b, rr, bb, br, rb);

        int attackedMoreThanEnemy = Long.bitCount(attacking)-Long.bitCount(attackedFrom);

        int distanceToEnd =  (isRed ? Long.numberOfLeadingZeros(redFigures) : Long.numberOfTrailingZeros(blueFigures))/8;//Reverse
        int enemyDistanceToEnd = (!isRed ? Long.numberOfLeadingZeros(redFigures) : Long.numberOfTrailingZeros(blueFigures))/8;//Reverse



        int thirdLastRowMoreFiguresThanEnemy = Long.bitCount(ownFiguresThatCanWinIfPromotedAndCloseFigures) - Long.bitCount(enemyFiguresThatCanWinIfPromotedAndCloseFigures);

        //DEBUG: COMMENT OUT WHEN NOT NEEDED/ALPHABETA
        //System.out.println("pieceWorthValue: " + pieceWorthValue);
        //System.out.println("enemyPieceWorthValue: " + enemyPieceWorthValue);

        //System.out.println("8 - distanceToEnd: " + (8 - distanceToEnd));

/*        System.out.println("pieceWorthValue - enemyPieceWorthValue: " + (pieceWorthValue - enemyPieceWorthValue));
        System.out.println("attackedMoreThanEnemy: " + attackedMoreThanEnemy);
        System.out.println("distanceToEndMoreThanEnemy:"+(enemyDistanceToEnd-distanceToEnd));
        System.out.println("thirdLastRowMoreFiguresThanEnemy: " + thirdLastRowMoreFiguresThanEnemy);*/
        //if(true) return 0;

        return (pieceWorthValue - enemyPieceWorthValue) +
                (attackedMoreThanEnemy) +
                //(8-distanceToEnd) +
                (enemyDistanceToEnd-distanceToEnd)+
                (thirdLastRowMoreFiguresThanEnemy);
    }

    //start: 12, max 12 unless all enemies turned into bottom_on_double, then a single is worth double.
    // Red_on_blue blue has zero value here, maybe too little? But worse than a single (1) cause blocked so maybe fitting
    public static int calculatePieceWorthValue(boolean redsTurn, long r, long b, long rr, long bb, long br, long rb) {
        return Long.bitCount(redsTurn ? r : b) + 2 * Long.bitCount(redsTurn ? rr : bb) + 2 * Long.bitCount(redsTurn ? br : rb);//doubles have double the value, (at least, as 2 figures inside) red_on_blue also has 2x value (change?)
    }


    //TODO: Check performance when not used as method, keep as method at first
    //start: 6, end 0, max 7 when as far away as possible (last field)
    public static int calculateDistanceToEnd(boolean redsTurn, long r, long b, long rr, long bb, long br, long rb) {
        // Find the position of the highest set bit (one) in the long bitboard
        int position = redsTurn ? Long.numberOfLeadingZeros(r | rr | br) : Long.numberOfTrailingZeros(b | bb | rb);//Reverse
        //BitBoard.displayBitboard(r|rr|br);
        //System.out.println("pos:"+position+" >>"+Tools.bitboardToString(r|rr|br) + redsTurn);
        return position / 8;
    }


    public static int calculateNearWinBonus(boolean redsTurn, long r, long b, long rr, long bb, long br, long rb) {
        int bonus = 0;
        long bluePieces = b | bb | rb;
        long redPieces = r | rr | br;

        final long topRowMask = 0xFFL;  // Mask for the top row (bits 0-7)
        final long bottomRowMask = 0xFFL << 56;  // Mask for the bottom row (bits 56-63)
        final long penultimateTopRowMask = 0xFFL << 8;
        final long penultimateBottomRowMask = 0xFFL << 48;

        if ((bluePieces & topRowMask) != 0) {
            //Tools.displayBitboard(bluePieces);
        }

        // Bonus for red pieces in the penultimate row
        if (redsTurn) {
            bonus += Long.bitCount(redPieces & penultimateBottomRowMask) * 100; // Add significant bonus for potential win moves
            bonus += Long.bitCount(redPieces & bottomRowMask) * 200; // Add higher bonus for pieces in the base row

        } else {
            bonus += Long.bitCount(bluePieces & penultimateTopRowMask) * 100;
            bonus += Long.bitCount(bluePieces & topRowMask) * 200;

        }

        return bonus;
    }
}
