package model;

public class Evaluate {
    /**
     * Shortened names cause they might be passed lots of times br= red_on_blue as in the board
     * Passing board would be simpler, but better performance without object
     * Copy this for most:
     boolean redsTurn,long r, long b, long rr, long bb, long br, long rb
     */

    public static int evaluateSimple(boolean redsTurn,long r, long b, long rr, long bb, long br, long rb) {
        return 2*(8-calculateDistanceToEnd(redsTurn, r, b, rr, bb, br, rb))+(calculatePieceWorthValue(redsTurn, r, b, rr, bb, br, rb)-calculatePieceWorthValue(!redsTurn, r, b, rr, bb, br, rb));
    }

    //start: 12, max 12 unless all enemies turned into bottom_on_double, then a single is worth double.
    // Red_on_blue blue has zero value here, maybe too little? But worse than a single (1) cause blocked so maybe fitting
    public static int calculatePieceWorthValue(boolean redsTurn,long r, long b, long rr, long bb, long br, long rb){
        return Long.bitCount(redsTurn?r:b) + 2*Long.bitCount(redsTurn?rr:bb) + 2*Long.bitCount(redsTurn?br:rb);//doubles have double the value, (at least, as 2 figures inside) red_on_blue also has 2x value (change?)
    }


    //TODO: Check performance when not used as method, keep as method at first
    //start: 6, end 0, max 7 when as far away as possible (last field)
    public static int calculateDistanceToEnd(boolean redsTurn,long r, long b, long rr, long bb, long br, long rb) {
        // Find the position of the highest set bit (one) in the long bitboard
        int position = redsTurn?Long.numberOfLeadingZeros(r|rr|br):Long.numberOfTrailingZeros(b|bb|rb);//Reverse
        //BitBoard.displayBitboard(r|rr|br);
        //System.out.println("pos:"+position+" >>"+Tools.bitboardToString(r|rr|br) + redsTurn);
        return position/8;
    }



}
