package model;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class SturdyJumpersAI {
   /*// private static int depth = 0; // Example depth limit
    private static int untersuchteZustaende = 0;

    private static boolean timeUp;
    private static final long TIME_LIMIT = 2000000000L;

    public static String findBestMove(SearchType searchType, BitBoard board, boolean isRed, int depth){
        return findBestMoveTestWrapper(searchType, board, isRed, depth).bestMove;
    }

    public static TestWrapper findBestMoveTestWrapper(SearchType searchType, BitBoard board, boolean isRed, int depth) {
        switch (searchType){
            case MINIMAX -> {
                return alphaBetaWrapper(board, isRed, false);
            }
            case ALPHABETA -> {
                return alphaBetaWrapper(board, isRed, true);
            }
        }
        return alphaBetaWrapper(board, isRed, true);
    }


    public static TestWrapper alphaBetaWrapper(BitBoard board, boolean isRed, boolean withCutoffs) {
        int bestValue = Integer.MIN_VALUE;
        String bestMove = null;
        List<String> legalMoves = board.getAllPossibleMoves(isRed);
        int depth = 1;
        long startTime = System.nanoTime();
        timeUp = false;
        //int untersuchteZustaende = 0;


        if(withCutoffs) {

        while (!timeUp) {
                for (String move : legalMoves) {
                    untersuchteZustaende++;
                    BitBoard newBoard = board.longToBit(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(Tools.parseMove(move)[0], Tools.parseMove(move)[1], isRed, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red));
                    int moveValue = alphaBetaSearch(newBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, isRed, false, startTime);
                    if (moveValue > bestValue) {
                        bestValue = moveValue;
                        bestMove = move;
                    }
                }
            depth++;
        }
        } else {
            for (String move : legalMoves) {
                untersuchteZustaende++;
                BitBoard newBoard = board.longToBit(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(Tools.parseMove(move)[0], Tools.parseMove(move)[1], isRed, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red));
                int moveValue = alphaBetaSearchWithoutCutoffs(newBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, isRed, false);
                if (moveValue > bestValue) {
                    bestValue = moveValue;
                    bestMove = move;
                }
            }
        }
        return new TestWrapper(untersuchteZustaende, bestValue, bestMove, isRed);
    }


    //AlphaBeta
    private static int alphaBetaSearch(BitBoard board, int depth, int alpha, int beta, boolean isRed, boolean maximizingPlayer, long startTime) {
        if (System.nanoTime() - startTime > TIME_LIMIT) {
            timeUp = true;
        }
        if (depth <= 0 || board.checkWinCondition() != 'f' || board.checkWinCondition() == 'd') {
            return Evaluate.evaluateSimple(isRed,board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red);
        }
        List<String> legalMoves = board.getAllPossibleMoves(isRed);
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (String move : legalMoves) {
                untersuchteZustaende++;
                BitBoard newBoard = board.longToBit(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(Tools.parseMove(move)[0],Tools.parseMove(move)[1],isRed,board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red) );
                int eval = alphaBetaSearch(newBoard, depth - 1, alpha, beta, isRed, false, startTime);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break; // Beta cut-off
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (String move : legalMoves) {
                untersuchteZustaende++;
                BitBoard newBoard = board.longToBit(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(Tools.parseMove(move)[0],Tools.parseMove(move)[1],isRed,board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red) );
                int eval = alphaBetaSearch(newBoard, depth - 1, alpha, beta, isRed, true, startTime);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break; // Alpha cut-off
                }
            }
            return minEval;
        }
    }


    //Minimax
    private static int alphaBetaSearchWithoutCutoffs(BitBoard board, int depth, int alpha, int beta, boolean isRed, boolean maximizingPlayer) {
        if (depth <= 0 || board.checkWinCondition() != 'f' || board.checkWinCondition() == 'd') {
            return Evaluate.evaluateSimple(isRed,board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red);
        }
        List<String> legalMoves = board.getAllPossibleMoves(isRed);
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (String move : legalMoves) {
                untersuchteZustaende++;
                BitBoard newBoard = board.longToBit(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(Tools.parseMove(move)[0],Tools.parseMove(move)[1],isRed,board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red) );
                int eval = alphaBetaSearchWithoutCutoffs(newBoard, depth - 1, alpha, beta, isRed, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                /*if (beta <= alpha) {
                    break; // Beta cut-off
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (String move : legalMoves) {
                untersuchteZustaende++;
                BitBoard newBoard = board.longToBit(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(Tools.parseMove(move)[0],Tools.parseMove(move)[1],isRed,board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red) );
                int eval = alphaBetaSearchWithoutCutoffs(newBoard, depth - 1, alpha, beta, isRed, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                /*if (beta <= alpha) {
                    break; // Alpha cut-off
                }
            }
            return minEval;
        }
    } */

    private static int untersuchteZustaende = 0;
    private static boolean timeUp;
    private static final long TIME_LIMIT = 2000000000L; // 2 seconds in nanoseconds

    public static String findBestMove(SearchType searchType, BitBoard board, boolean isRed, int depth){
        return findBestMoveTestWrapper(searchType, board, isRed, depth).bestMove;
    }

    public static TestWrapper findBestMoveTestWrapper(SearchType searchType, BitBoard board, boolean isRed, int depth) {
        switch (searchType){
            case MINIMAX -> {
                return alphaBetaWrapper(board, isRed, false);
            }
            case ALPHABETA -> {
                return alphaBetaWrapper(board, isRed, true);
            }
        }
        return alphaBetaWrapper(board, isRed, true);
    }

    public static TestWrapper alphaBetaWrapper(BitBoard board, boolean isRed, boolean withCutoffs) {
        int bestValue = Integer.MIN_VALUE;
        String bestMove = null;
        List<String> legalMoves = board.getAllPossibleMoves(isRed);
        int depth = 1;

        long startTime = System.nanoTime();
        timeUp = false;

        while (!timeUp) {
            for (String move : legalMoves) {
                untersuchteZustaende++;
                BitBoard newBoard = board.longToBit(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                        Tools.parseMove(move)[0], Tools.parseMove(move)[1], isRed,
                        board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                        board.red_on_blue, board.blue_on_red));
                List<String> moveList = new ArrayList<>();
                moveList.add(move);
                int moveValue = withCutoffs ?
                        alphaBetaSearch(newBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, isRed, false, startTime,moveList ) :
                        alphaBetaSearchWithoutCutoffs(newBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, isRed, false, startTime);
                if (moveValue > bestValue) {
                    bestValue = moveValue;
                    bestMove = move;
                }
                if (timeUp) break;
            }
            depth++;

        }
        System.out.println("Erreichte Tiefe ENDE: " + depth + " und bester Zug: " + bestMove + ". Value: " + bestValue);
        return new TestWrapper(untersuchteZustaende, bestValue, bestMove, isRed);
    }

    // AlphaBeta with time management
    private static int alphaBetaSearch(BitBoard board, int depth, int alpha, int beta, boolean isRed, boolean maximizingPlayer, long startTime,List<String> previousMoves) {
        if (System.nanoTime() - startTime > TIME_LIMIT) {
            timeUp = true;
            return maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }

        if (depth <= 0 || board.checkWinCondition() != 'f' || board.checkWinCondition() == 'd') {
            return Evaluate.evaluateSimple(isRed, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
        }

        List<String> legalMoves = board.getAllPossibleMoves(isRed);
        if (maximizingPlayer){
            int maxEval = Integer.MIN_VALUE;
            for (String move : legalMoves) {

                untersuchteZustaende++;
                BitBoard newBoard = board.longToBit(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                        Tools.parseMove(move)[0], Tools.parseMove(move)[1], isRed,
                        board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                        board.red_on_blue, board.blue_on_red));
                List<String> includingNew = new ArrayList<>(previousMoves);
                includingNew.add(move);
                int eval = alphaBetaSearch(newBoard, depth-1, alpha, beta, isRed, false, startTime,includingNew);

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break; // Beta cut-off
                }
                if (timeUp) break;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (String move : legalMoves) {
                untersuchteZustaende++;
                BitBoard newBoard = board.longToBit(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                        Tools.parseMove(move)[0], Tools.parseMove(move)[1], isRed,
                        board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                        board.red_on_blue, board.blue_on_red));
                List<String> includingNew = new ArrayList<>(previousMoves);
                includingNew.add(move);
                int eval = alphaBetaSearch(newBoard, depth-1, alpha, beta, isRed, true, startTime,includingNew);

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break; // Alpha cut-off
                }
                if (timeUp) break;
            }
            return minEval;
        }
    }

    // Minimax without cut-offs (not modified, can be used as is)
    private static int alphaBetaSearchWithoutCutoffs(BitBoard board, int depth, int alpha, int beta, boolean isRed, boolean maximizingPlayer, long startTime) {
        if (System.nanoTime() - startTime > TIME_LIMIT) {
            timeUp = true;
            return maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }
        if (depth <= 0 || board.checkWinCondition() != 'f' || board.checkWinCondition() == 'd') {
            return Evaluate.evaluateSimple(isRed, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
        }

        List<String> legalMoves = board.getAllPossibleMoves(isRed);
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (String move : legalMoves) {
                untersuchteZustaende++;
                BitBoard newBoard = board.longToBit(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                        Tools.parseMove(move)[0], Tools.parseMove(move)[1], isRed,
                        board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                        board.red_on_blue, board.blue_on_red));
                int eval = alphaBetaSearchWithoutCutoffs(newBoard, depth - 1, alpha, beta, isRed, false, startTime);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (String move : legalMoves) {
                untersuchteZustaende++;
                BitBoard newBoard = board.longToBit(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                        Tools.parseMove(move)[0], Tools.parseMove(move)[1], isRed,
                        board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                        board.red_on_blue, board.blue_on_red));
                int eval = alphaBetaSearchWithoutCutoffs(newBoard, depth - 1, alpha, beta, isRed, true, startTime);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
            }
            return minEval;
        }
    }
}
