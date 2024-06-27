package ai;


import java.util.HashMap;
import java.util.Map;

public class TranspositionTable {
    private final Map<Long, TranspoEntry> table = new HashMap<>();

    public void store(long hashKey, int depth, int value, String bestMove, int flag) {
        table.put(hashKey, new TranspoEntry(hashKey, depth, value, bestMove, flag));
    }

    public TranspoEntry get(long hashKey) {
        return table.get(hashKey);
    }
}