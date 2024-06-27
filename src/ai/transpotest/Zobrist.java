package ai.transpotest;

import model.BitBoard;

import java.util.Random;

public class Zobrist {
    private static final int BOARD_SIZE = 64; // 8x8 board
    private static final int NUM_PIECE_TYPES = 6; // 6 piece types

    private final long[][] zobristTable; // Zobrist table for each piece type and position
    public long startZobristKey; // Current Zobrist key for the board state

    public Zobrist() {
        zobristTable = new long[6][64];
        initializeZobristTable();
        startZobristKey = calculateInitialZobristKey();
    }

    private void initializeZobristTable() {
        Random r = new Random();
        for (int pieceType = 0; pieceType < NUM_PIECE_TYPES; pieceType++) {
            for (int position = 0; position < BOARD_SIZE; position++) {
                zobristTable[pieceType][position] = r.nextLong();
            }
        }
    }

    private long calculateInitialZobristKey() {
        long zobristKey = 0L;
        for (int pieceType = 0; pieceType < NUM_PIECE_TYPES; pieceType++) {
            for (int position = 0; position < BOARD_SIZE; position++) {
                zobristKey ^= zobristTable[pieceType][position];
            }
        }
        return zobristKey;
    }

    public long applyMove(long currentZobristKey,byte fromIndex, byte toIndex, long r, long b, long rr, long bb, long br, long rb) {
        byte fromType = -1;
        long tempIndexed = (1L << fromIndex); // For performance, save here
        if((r & tempIndexed) != 0) fromType=0;
        if((b & tempIndexed) != 0) fromType=1;
        if((rr & tempIndexed) != 0) fromType=2;
        if((bb & tempIndexed) != 0) fromType=3;
        if((br & tempIndexed) != 0) fromType=4;
        if((rb & tempIndexed) != 0) fromType=5;
        byte toType = -1;
        long tempIndexed2 = (1L << toIndex);
        if((r &  tempIndexed2) != 0) toType=0;
        if((b &  tempIndexed2) != 0) toType=1;
        if((rr & tempIndexed2) != 0) toType=2;
        if((bb & tempIndexed2) != 0) toType=3;
        if((br & tempIndexed2) != 0) toType=4;
        if((rb & tempIndexed2) != 0) toType=5;

/*        if(fromType == 0){
            //Single move
            if(toType == 1){//Enemy single

            }
        }*/
        //TODO: Probably needs to be updated with actual move logic because from toindex isnt enough to
        //model double moves where one remains beneath (would need to be added etc)

/*        // Update Zobrist key for moving piece from 'fromPosition' to 'toPosition'
        currentZobristKey ^= zobristTable[fromType][fromIndex];*/
        if(fromIndex<2){//All single moves:

            currentZobristKey ^= zobristTable[fromType][fromIndex];//Here we can remove the single from the start, no
            //Complex logic like double leaving a piece required.
            boolean isRedSingle = fromIndex==0;
            switch (toType){

                case -1 -> {
                    currentZobristKey ^= zobristTable[fromType][toIndex];//Just put, empty
                }
                case 0 -> {
                    currentZobristKey ^= zobristTable[0][toIndex];
                    currentZobristKey ^= zobristTable[isRedSingle?2:1][toIndex]; // either double or singleblue(capture)
                }
                case 1 -> {
                    currentZobristKey ^= zobristTable[1][toIndex];
                    currentZobristKey ^= zobristTable[isRedSingle?1:3][toIndex];//redsingle or bluedouble
                }
                case 2 -> {// ALL OF THE NEXT ONES ASSUME NO WRONG MOVES AKA NO THIRD RED PIECE CAN GO ONTOP REDDOUBLE
                    currentZobristKey ^= zobristTable[2][toIndex];
                    currentZobristKey ^= zobristTable[5][toIndex];//Add blueonred, remove reddouble
                }
                case 3 -> {
                    currentZobristKey ^= zobristTable[3][toIndex];
                    currentZobristKey ^= zobristTable[4][toIndex];//remove bluedouble, add redonblue
                }
                case 4 -> {
                    currentZobristKey ^= zobristTable[4][toIndex];
                    currentZobristKey ^= zobristTable[3][toIndex];
                }
                case 5 -> {
                    currentZobristKey ^= zobristTable[5][toIndex];
                    currentZobristKey ^= zobristTable[2][toIndex];

                }
            }
            return currentZobristKey;
        }
        //THIS PART IS ONLY FOR DOUBLE PIECES: PREVIOUS WOULD ALREADY HAVE RETURNED OTHERWISE

        // If double piece, different peace remains
/*            if(fromIndex == 2||fromIndex==5){// doublered blueonred, red remains
                currentZobristKey ^= zobristTable[0][fromIndex];
            }else if(fromIndex == 3 || fromIndex == 4){//doubleblue, redonblue, blue remains
                currentZobristKey ^= zobristTable[1][fromIndex];
            }*/
        //int remainingSingle = ((fromType==2||fromType==5)?0:1);
        int remainingSingle = ((fromType==2||fromType==5)?0:1);
        int movingSingle = (fromType==2||fromType==4)?0:1;//Not same as above, same as belongs to team
        currentZobristKey ^= zobristTable[remainingSingle][fromIndex]; //reddouble,redOnBlue leave red etc and we know

        currentZobristKey ^= zobristTable[fromType][fromIndex];//REmove original double, it never stays

        switch (toType){

            case -1 -> {
                currentZobristKey ^= zobristTable[movingSingle][toIndex];//put single of team at empty pos, remaining was set above
            }
            case 0 -> {
                currentZobristKey ^= zobristTable[0][toIndex];//remove single red
                if(movingSingle==0){
                    currentZobristKey ^= zobristTable[2][toIndex];//put double red
                }else{
                    currentZobristKey ^= zobristTable[1][toIndex];//put single blue
                }
            }
            case 1 -> {
                currentZobristKey ^= zobristTable[1][toIndex];//remove single blue
                if(movingSingle==0){
                    currentZobristKey ^= zobristTable[0][toIndex];//put single red if red jumps on single blue
                }else{
                    currentZobristKey ^= zobristTable[3][toIndex];//put double blue
                }

            }
            case 2 -> { // ALL OF THE NEXT ONES ASSUME NO WRONG MOVES AKA NO THIRD RED PIECE CAN GO ONTOP REDDOUBLE
                //TEAM IS ASSUMED WHEN MOVING ON THEM
                currentZobristKey ^= zobristTable[2][toIndex];//remove double red
                currentZobristKey ^= zobristTable[5][toIndex];//add blue on red
            }
            case 3 -> {
                currentZobristKey ^= zobristTable[3][toIndex];//remove double blue
                currentZobristKey ^= zobristTable[4][toIndex];//add red on blue
            }
            case 4 -> {
                currentZobristKey ^= zobristTable[4][toIndex];//remove redonblue
                currentZobristKey ^= zobristTable[3][toIndex];//add doubleblue (red beat)
            }
            case 5 -> {
                currentZobristKey ^= zobristTable[5][toIndex];//remove blueonred
                currentZobristKey ^= zobristTable[2][toIndex];//add double red (blue beat)
            }
        }
        //currentZobristKey ^= zobristTable[fromType][toIndex];
        return currentZobristKey;
    }

    public void initializeCorrectBoardKey(BitBoard board){
        startZobristKey = computeInitialZobristKey(board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
    }


    public long computeInitialZobristKey(long r, long b, long rr, long bb, long br, long rb) {
        long initialZobristKey = 0L;

        initialZobristKey ^= computeZobristForBitboard(r, 0);
        initialZobristKey ^= computeZobristForBitboard(b, 1);
        initialZobristKey ^= computeZobristForBitboard(rr, 2);
        initialZobristKey ^= computeZobristForBitboard(bb, 3);
        initialZobristKey ^= computeZobristForBitboard(br, 4);
        initialZobristKey ^= computeZobristForBitboard(rb, 5);

        return initialZobristKey;
    }

    private long computeZobristForBitboard(long bitboard, int pieceType) {
        long zobristKey = 0L;
        while (bitboard != 0) {
            int position = Long.numberOfTrailingZeros(bitboard);
            zobristKey ^= zobristTable[pieceType][position];
            bitboard &= bitboard - 1; // Clear the least significant bit
        }
        return zobristKey;
    }


    public long getStartZobristKey() {
        return startZobristKey;
    }

    public long getZobristValue(int pieceType, int position) {
        return zobristTable[pieceType][position];
    }

}
