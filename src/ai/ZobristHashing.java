package ai;

import java.util.Random;

public class ZobristHashing {
    private static final int BOARD_SIZE = 64;
    private long[][][] zobristTable; // [piece type][position][player]

    public ZobristHashing() {
        zobristTable = new long[7][BOARD_SIZE][2];
        Random random = new Random(123456);
        for (int pieceType = 0; pieceType < 6; pieceType++) {
            for (int position = 0; position < BOARD_SIZE; position++) {
                for (int player = 0; player < 2; player++) {
                    zobristTable[pieceType][position][player] = random.nextLong();
                }
            }
        }
    }

    public long getZobristNumber(int pieceType, int position, int player) {
        return zobristTable[pieceType][position][player];
    }
}
