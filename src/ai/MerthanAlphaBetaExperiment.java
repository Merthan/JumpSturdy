package ai;

import misc.Tools;
import model.BitBoard;

import java.util.ArrayList;
import java.util.List;

public class MerthanAlphaBetaExperiment {

    public long endTime;
    public int bestDepthReached;

/*    public int alphaBeta(BitBoard board, int depth, int alpha, int beta, boolean maximizingPlayer, boolean isRed) {
        if (System.currentTimeMillis() > endTime) {
            return 0; // Return even/neutral value when time over limit
        }
        if (depth == 0 || board.checkWinCondition() != BitBoard.WINNER_ONGOING) {
            int eval = Evaluate.evaluateComplex(isRed, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);

            return eval;
        }

        List<String> legalMoves = board.getAllPossibleMoves(isRed);

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (String move : legalMoves) {
                BitBoard executedMoveBoard = BitBoard.fromLongArray(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                        Tools.parseMove(move)[0], Tools.parseMove(move)[1], isRed,
                        board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                        board.red_on_blue, board.blue_on_red));

                int eval = alphaBeta(executedMoveBoard, depth - 1, alpha, beta, false, isRed);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break; // Beta cutoff
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (String move : legalMoves) {
                BitBoard executedMoveBoard = BitBoard.fromLongArray(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                        Tools.parseMove(move)[0], Tools.parseMove(move)[1], isRed,
                        board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                        board.red_on_blue, board.blue_on_red));

                int eval = alphaBeta(executedMoveBoard, depth - 1, alpha, beta, true, isRed);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break; // Alpha cutoff
                }
            }
            return minEval;
        }
    }*/

/*    public int alphaBeta(BitBoard board, int depth, int alpha, int beta, boolean maximizingPlayer, boolean isRed, List<String> bestMoves) {
        if (System.currentTimeMillis() > endTime) {
            return 0; // Return a neutral value if time limit is reached
        }
        if (depth == 0 || board.checkWinCondition() != BitBoard.WINNER_ONGOING) {
            int eval = Evaluate.evaluateComplex(isRed, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);

            return eval;
        }

        List<String> legalMoves = board.getAllPossibleMoves(maximizingPlayer);
        List<String> currentBestMoves = new ArrayList<>();

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (String move : legalMoves) {
                BitBoard executedMoveBoard = BitBoard.fromLongArray(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                        Tools.parseMove(move)[0], Tools.parseMove(move)[1], maximizingPlayer,
                        board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                        board.red_on_blue, board.blue_on_red));

                List<String> childBestMoves = new ArrayList<>();
                int eval = alphaBeta(executedMoveBoard, depth - 1, alpha, beta, false, isRed, childBestMoves);
                if (eval > maxEval) {
                    maxEval = eval;
                    currentBestMoves.clear();
                    currentBestMoves.add(move);
                    currentBestMoves.addAll(childBestMoves);
                }
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break; // Beta cutoff
                }
            }
            bestMoves.addAll(currentBestMoves);
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (String move : legalMoves) {
                BitBoard executedMoveBoard = BitBoard.fromLongArray(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                        Tools.parseMove(move)[0], Tools.parseMove(move)[1], maximizingPlayer,
                        board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                        board.red_on_blue, board.blue_on_red));

                List<String> childBestMoves = new ArrayList<>();
                int eval = alphaBeta(executedMoveBoard, depth - 1, alpha, beta, true, isRed, childBestMoves);
                if (eval < minEval) {
                    minEval = eval;
                    currentBestMoves.clear();
                    currentBestMoves.add(move);
                    currentBestMoves.addAll(childBestMoves);
                }
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break; // Alpha cutoff
                }
            }
            bestMoves.addAll(currentBestMoves);
            return minEval;
        }
    }*/

    public int alphaBeta(BitBoard board, int depth, int alpha, int beta, boolean maximizingPlayer, boolean isRed, List<String> bestMoves) {
        if (System.currentTimeMillis() > endTime) {
            return 0; // Return a neutral value if time limit is reached
        }
        if (depth == 0 || board.checkWinCondition() != BitBoard.WINNER_ONGOING) {
            int eval = Evaluate.evaluateComplex(isRed, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);

            return eval;
        }

        List<String> legalMoves = board.getAllPossibleMoves(maximizingPlayer);
        List<String> currentBestMoves = new ArrayList<>();

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (String move : legalMoves) {
                BitBoard executedMoveBoard = BitBoard.fromLongArray(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                        Tools.parseMove(move)[0], Tools.parseMove(move)[1], maximizingPlayer,
                        board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                        board.red_on_blue, board.blue_on_red));

                List<String> childBestMoves = new ArrayList<>();
                int eval = alphaBeta(executedMoveBoard, depth - 1, alpha, beta, false, isRed, childBestMoves);
                if (eval > maxEval) {
                    maxEval = eval;
                    currentBestMoves.clear();
                    currentBestMoves.add(move);
                    currentBestMoves.addAll(childBestMoves);
                }
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break; // Beta cutoff
                }
            }
            bestMoves.addAll(currentBestMoves);
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (String move : legalMoves) {
                BitBoard executedMoveBoard = BitBoard.fromLongArray(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                        Tools.parseMove(move)[0], Tools.parseMove(move)[1], maximizingPlayer,
                        board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                        board.red_on_blue, board.blue_on_red));

                List<String> childBestMoves = new ArrayList<>();
                int eval = alphaBeta(executedMoveBoard, depth - 1, alpha, beta, true, isRed, childBestMoves);
                if (eval < minEval) {
                    minEval = eval;
                    currentBestMoves.clear();
                    currentBestMoves.add(move);
                    currentBestMoves.addAll(childBestMoves);
                }
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break; // Alpha cutoff
                }
            }
            bestMoves.addAll(currentBestMoves);
            return minEval;
        }
    }

    public ArrayList<String> findBestMove(BitBoard board, boolean isRed, int timeLimitMillis) {
        endTime = System.currentTimeMillis() + timeLimitMillis;
        bestDepthReached = 0;
        ArrayList<String> bestMoveSequence = new ArrayList<>();

        for (int depth = 1; ; depth++) {
            int currentBestValue = Integer.MIN_VALUE;
            ArrayList<String> currentBestMoveSequence = new ArrayList<>();
            List<String> legalMoves = board.getAllPossibleMoves(isRed);
            System.out.println("Moves here:"+legalMoves);

            for (String move : legalMoves) {
                BitBoard executedMoveBoard = BitBoard.fromLongArray(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                        Tools.parseMove(move)[0], Tools.parseMove(move)[1], isRed,
                        board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                        board.red_on_blue, board.blue_on_red));

                List<String> childBestMoves = new ArrayList<>();
                int moveValue = alphaBeta(executedMoveBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, false, isRed, childBestMoves);
                if (moveValue > currentBestValue) {
                    currentBestValue = moveValue;
                    currentBestMoveSequence.clear();
                    currentBestMoveSequence.add(move);
                    currentBestMoveSequence.addAll(childBestMoves);
                }
                if (System.currentTimeMillis() > endTime) {
                    break; // Time
                }
            }

            if (System.currentTimeMillis() > endTime) {
                break; // Time
            }

            bestMoveSequence = currentBestMoveSequence;
            bestDepthReached = depth;
        }

        // Print final search information
        System.out.println("Best Move Sequence: " + bestMoveSequence);
        System.out.println("Depth Reached: " + bestDepthReached);
        System.out.println("Time Elapsed: " + (System.currentTimeMillis() - (endTime - timeLimitMillis)) + " ms");

        return bestMoveSequence;
    }

/*    public String findBestMove(BitBoard board, boolean isRed, int timeLimitMillis) {
        endTime = System.currentTimeMillis() + timeLimitMillis;
        bestDepthReached = 0;
        String bestMove = null;
        int bestValue = Integer.MIN_VALUE;

        for (int depth = 1; ; depth++) {
            int currentBestValue = Integer.MIN_VALUE;
            String currentBestMove = null;
            List<String> legalMoves = board.getAllPossibleMoves(isRed);

            for (String move: legalMoves) {
                BitBoard executedMoveBoard = BitBoard.fromLongArray(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                        Tools.parseMove(move)[0], Tools.parseMove(move)[1], isRed,
                        board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                        board.red_on_blue, board.blue_on_red));

                int moveValue = alphaBeta(executedMoveBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, false, isRed);
                if (moveValue > currentBestValue) {
                    currentBestValue = moveValue;
                    currentBestMove = move;
                }
                if (System.currentTimeMillis() > endTime) {
                    break; // Time
                }
            }

            if (System.currentTimeMillis() > endTime) {
                break; // Time
            }

            bestValue = currentBestValue;
            bestMove = currentBestMove;
            bestDepthReached = depth;
        }
        System.out.println("Best Value ::"+bestValue);
        System.out.println("Best Move: " + bestMove);
        System.out.println("Depth Reached: " + bestDepthReached);
        System.out.println("Time Elapsed: " + (System.currentTimeMillis() - (endTime - timeLimitMillis)) + " ms");

        return bestMove;
    }*/



}

