package ai.transpotest;

import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import misc.Tools;
import model.BitBoard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FastTranspo {

    //public Map<Long, Long> transpositionTable;
    //Now using primitive faster library
    public Long2LongMap transpositionTable;
    public Zobrist zobrist;
    public FastTranspo() {
        //transpositionTable = new HashMap<>();
        transpositionTable = new Long2LongOpenHashMap();
        zobrist=new Zobrist();
    }

    // Pack evaluation, fromPos, toPos, depth, and type into a long
    public static long packEntry(int eval, byte fromPos, byte toPos, byte depth, byte type) {
        long packedEntry = 0L;
        packedEntry |= ((long) eval & 0xFFFFFFFFL) << 32;  // Evaluation 32 bits
        packedEntry |= ((long) fromPos & 0xFFL) << 24;     // fromPos 8 bits
        packedEntry |= ((long) toPos & 0xFFL) << 16;       // toPos 8 bits
        packedEntry |= ((long) depth & 0xFFL) << 8;        // depth 8 bits
        packedEntry |= ((long) type & 0xFFL);// type 8 bits
        return packedEntry;
    }

    public String transpositionTableToStringNormal() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Long, Long> entry : transpositionTable.entrySet()) {
            long key = entry.getKey();
            long value = entry.getValue();
            sb.append("Key: ").append(key).append(", Value: ").append(entryToString(value)).append("\n");
        }
        return sb.toString();
    }
    public String transpositionTableToString(boolean sortByDepth) {
        StringBuilder sb = new StringBuilder();

        // Stream the entries, optionally sort by depth
        transpositionTable.entrySet().stream()
                .sorted((e1, e2) -> {
                    if (sortByDepth) {
                        byte depth1 = (byte) ((e1.getValue() >> 8) & 0xFFL);
                        byte depth2 = (byte) ((e2.getValue() >> 8) & 0xFFL);
                        return Byte.compare(depth2, depth1); // Sort descending by depth
                    } else {
                        return Long.compare(e1.getKey(), e2.getKey()); // Sort by key
                    }
                })
                .forEach(entry -> {
                    long key = entry.getKey();
                    long value = entry.getValue();
                    sb.append(key).append(", Value: ").append(entryToString(value)).append("\n");
                });

        return sb.toString();
    }

    public static String entryToString(long packedEntry) {
        int eval = (int) ((packedEntry >> 32) & 0xFFFFFFFFL);
        byte fromPos = (byte) ((packedEntry >> 24) & 0xFFL);
        byte toPos = (byte) ((packedEntry >> 16) & 0xFFL);
        byte depth = (byte) ((packedEntry >> 8) & 0xFFL);
        byte type = (byte) (packedEntry & 0xFFL);

        return String.format("Entry: [Eval: %d, FromPos: %d, ToPos: %d, Depth: %d, Type: %d]", eval, fromPos, toPos, depth, type);
    }

    //@Deprecated//TEMP
/*    public void storeEntry(long zobristKey, int eval, byte fromPos, byte toPos, byte depth, byte type) {
        long packedEntry = packEntry(eval, fromPos, toPos, depth, type);
        transpositionTable.put(zobristKey, packedEntry);
    }*/

    public void storeEntry(long zobristKey, int eval, byte fromPos, byte toPos, byte depth, byte type) {
        transpositionTable.put(zobristKey, packEntry(eval, fromPos, toPos, depth, type));
    }

    public Map<Long, String> fenTableDebug = new HashMap<>();

    public void debugStoreEntry(long zobristKey, int eval, byte fromPos, byte toPos, byte depth, byte type, BitBoard board) {
        long packedEntry = packEntry(eval, fromPos, toPos, depth, type);
        transpositionTable.put(zobristKey, packedEntry);
        fenTableDebug.put(zobristKey,board.toFEN()+">>"+ board.previousMoves()+ " -- "+ Arrays.toString(board.previousMove));
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
