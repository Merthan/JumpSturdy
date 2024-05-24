package model;
import java.util.List;

public class SturdyJumpersAI {
    private static final int MAX_DEPTH = 3; // Example depth limit
    private static int untersuchteZustaende = 0;

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
        //int untersuchteZustaende = 0;

        if(withCutoffs) {
            for (String move : legalMoves) {
                untersuchteZustaende++;
                BitBoard newBoard = board.longToBit(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(Tools.parseMove(move)[0], Tools.parseMove(move)[1], isRed, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red));
                int moveValue = alphaBetaSearch(newBoard, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, isRed, false);
                if (moveValue > bestValue) {
                    bestValue = moveValue;
                    bestMove = move;
                }
            }
        } else {
            for (String move : legalMoves) {
                untersuchteZustaende++;
                BitBoard newBoard = board.longToBit(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(Tools.parseMove(move)[0], Tools.parseMove(move)[1], isRed, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red));
                int moveValue = alphaBetaSearchWithoutCutoffs(newBoard, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, isRed, false);
                if (moveValue > bestValue) {
                    bestValue = moveValue;
                    bestMove = move;
                }
            }
        }
        return new TestWrapper(untersuchteZustaende, bestValue, bestMove, isRed);
    }


    private static int alphaBetaSearch(BitBoard board, int depth, int alpha, int beta, boolean isRed, boolean maximizingPlayer) {
        if (depth <= 0 || board.checkWinCondition() != 'f' || board.checkWinCondition() == 'd') {
            return Evaluate.evaluateSimple(isRed,board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red);
        }
        List<String> legalMoves = board.getAllPossibleMoves(isRed);
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (String move : legalMoves) {
                untersuchteZustaende++;
                BitBoard newBoard = board.longToBit(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(Tools.parseMove(move)[0],Tools.parseMove(move)[1],isRed,board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red) );
                int eval = alphaBetaSearch(newBoard, depth - 1, alpha, beta, isRed, false);
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
                int eval = alphaBetaSearch(newBoard, depth - 1, alpha, beta, isRed, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break; // Alpha cut-off
                }
            }
            return minEval;
        }
    }


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
                }*/
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
                }*/
            }
            return minEval;
        }
    }
}
