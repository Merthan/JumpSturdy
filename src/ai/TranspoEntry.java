package ai;

public class TranspoEntry {
    long hashKey;
    int depth;
    int value;
    String bestMove;
    int flag;


    public TranspoEntry(long hashKey, int depth, int value, String bestMove, int flag) {
        this.hashKey = hashKey;
        this.depth = depth;
        this.value = value;
        this.bestMove = bestMove;
        this.flag = flag;
    }
}
