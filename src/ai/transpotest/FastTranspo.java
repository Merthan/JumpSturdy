package ai.transpotest;

import java.util.HashMap;
import java.util.Map;

public class FastTranspo {

    public Map<Long, Long> transpositionTable;

    public Zobrist zobrist;
    public FastTranspo() {
        transpositionTable = new HashMap<>();
        zobrist=new Zobrist();
    }

    // Pack evaluation, fromPos, toPos, depth, and type into a long
    public long packEntry(int eval, byte fromPos, byte toPos, byte depth, byte type) {
        long packedEntry = 0L;
        packedEntry |= ((long) eval & 0xFFFFFFFFL) << 32;  // Evaluation 32 bits
        packedEntry |= ((long) fromPos & 0xFFL) << 24;     // fromPos 8 bits
        packedEntry |= ((long) toPos & 0xFFL) << 16;       // toPos 8 bits
        packedEntry |= ((long) depth & 0xFFL) << 8;        // depth 8 bits
        packedEntry |= ((long) type & 0xFFL);// type 8 bits
        return packedEntry;
    }


    public void storeEntry(long zobristKey, int eval, byte fromPos, byte toPos, byte depth, byte type) {
        long packedEntry = packEntry(eval, fromPos, toPos, depth, type);
        transpositionTable.put(zobristKey, packedEntry);
    }


    static int unpackEval(long packedEntry) {
        return (int) (packedEntry >> 32);
    }

    static byte unpackFromPos(long packedEntry) {
        return (byte) ((packedEntry >> 24) & 0xFFL);
    }

    static byte unpackToPos(long packedEntry) {
        return (byte) ((packedEntry >> 16) & 0xFFL);
    }

    static byte unpackDepth(long packedEntry) {
        return (byte) ((packedEntry >> 8) & 0xFFL);
    }

    //CUrrently: 0=exact 1=Lower 2=upper
    static byte unpackType(long packedEntry) {
        return (byte) (packedEntry & 0xFFL);
    }

    public void clear() {
        transpositionTable.clear();
    }

}
