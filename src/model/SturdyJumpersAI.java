package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SturdyJumpersAI {
    private static final int MAX_DEPTH = 3; // Example depth limit
    private static int untersuchteZustaende = 0;

    public static String findBestMove(BitBoard board, boolean isRed, boolean withCutoffs) {
        int bestValue = Integer.MIN_VALUE;
        String bestMove = null;
        List<String> legalMoves = board.getAllPossibleMoves(isRed);

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
        return bestMove;
    }


    private static int alphaBetaSearch(BitBoard board, int depth, int alpha, int beta, boolean isRed, boolean maximizingPlayer) {
        if (depth == 0 || board.checkWinCondition() != 'f' || board.checkWinCondition() == 'd') {
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
        if (depth == 0 || board.checkWinCondition() != 'f' || board.checkWinCondition() == 'd') {
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
                int eval = alphaBetaSearch(newBoard, depth - 1, alpha, beta, isRed, true);
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
