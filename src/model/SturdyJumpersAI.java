package model;

import java.util.List;

public class SturdyJumpersAI {
    private static final int MAX_DEPTH = 3; // Example depth limit

    public String findBestMove(BitBoard board, Boolean isRed) {
        int bestValue = Integer.MIN_VALUE;
        String bestMove = null;
        List<String> legalMoves = board.getAllPossibleMoves(isRed);

        for (String move : legalMoves) {
            BitBoard newBoard = board.doMove(move, isRed, false);
            int moveValue = minimax(newBoard, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, isRed, false);
            if (moveValue > bestValue) {
                bestValue = moveValue;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int minimax(BitBoard board, int depth, int alpha, int beta, boolean isRed, boolean maximizingPlayer) {
        if (depth == 0 || board.checkWinCondition() != 'f' || board.checkWinCondition() != 'd') {
            return Evaluate.evaluateSimple(isRed,board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red);
        }

        List<String> legalMoves = board.getAllPossibleMoves(isRed);

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (String move : legalMoves) {
                BitBoard newBoard = board.doMove(move, isRed, false);
                int eval = minimax(newBoard, depth - 1, alpha, beta, isRed, false);
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
                BitBoard newBoard = board.doMove(move, isRed, false);
                int eval = minimax(newBoard, depth - 1, alpha, beta, isRed, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break; // Alpha cut-off
                }
            }
            return minEval;
        }
    }

}
