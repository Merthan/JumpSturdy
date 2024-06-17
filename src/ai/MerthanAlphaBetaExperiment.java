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

/*    public int alphaBeta(BitBoard board, int depth, int alpha, int beta, boolean maximizingPlayer, boolean isRed, List<String> bestMoves) {
        if (System.currentTimeMillis() > endTime) {
            return 0; // Return a neutral value if time limit is reached
        }
        if (depth == 0 || board.checkWinCondition() != BitBoard.WINNER_ONGOING) {
            int eval = Evaluate.evaluateComplex(true, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
            System.out.println("Eval at depth 0 or terminal state: " + depth + " EVAL:"+eval);
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
                System.out.println("Maximizing: Move: " + move + ", Eval: " + eval);
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
            System.out.println("Maximizing: Best Moves: " + bestMoves);
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
                System.out.println("Minimizing: Move: " + move + ", Eval: " + eval);
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
            System.out.println("Minimizing: Best Moves: " + bestMoves);
            return minEval;
        }
    }*/

    // current best
    public int alphaBeta(BitBoard board, int depth, int alpha, int beta, boolean maximizingPlayer, boolean isRed, List<String> bestMoves) {
        if (System.currentTimeMillis() > endTime) {
            return 0; // Return a neutral value if time limit is reached
        }
        if (depth == 0 || board.checkWinCondition() != BitBoard.WINNER_ONGOING) {
            //TODO: hardcoded true/false instead of isRed
            int eval = Evaluate.evaluateComplex(false, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);

            return eval;
        }

        List<String> legalMoves = board.getAllPossibleMoveStringsDeprecated(isRed);
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

/*    public int alphaBeta(BitBoard board, int depth, int alpha, int beta, boolean maximizingPlayer, boolean isRed, List<byte[]> bestMoves) {
        if (System.currentTimeMillis() > endTime) {
            return 0; // Return a neutral value if time limit is reached
        }
        boolean canWin = null != BitBoardManipulation.canWinWithMovesFusioned(isRed, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
        if (depth == 0 || board.checkWinCondition() != BitBoard.WINNER_ONGOING||canWin) {
            //TODO: isRed worked, now replaced by true
            int eval = Evaluate.evaluateComplex(true, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
            //Depth 0, to account for attacked pieces afterwards

            if(depth == 0){//If ruhesuche gets a value, return ruhesuche (doesnt take long)
                //Only do Ruhesuche if canwin not set to "winning soon" aka fusion or on winning spot already to not return a false (after FORCED ruhesuche/attacking) value
                int[] ruhesucheArray = (Math.abs(eval)<2000000000)? BitBoardManipulation.ruhesucheWithPositions(board,isRed):null;
                return (ruhesucheArray == null)? eval : ruhesucheArray[ruhesucheArray.length-1];
            }
            return eval;
        }

        byte[][] moves = board.getAllPossibleMovesByte(isRed);

        List<byte[]> currentBestMoves = new ArrayList<>();

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (byte[] move : moves) {
                BitBoard executedMoveBoard = board.doMoveAndReturnBitboard(move,maximizingPlayer);

                List<byte[]> childBestMoves = new ArrayList<>();
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
            bestMoves.clear();
            bestMoves.addAll(currentBestMoves);
            //System.out.println("Maximizing Player Best Moves: " + bestMoves);
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (byte[] move : moves) {
                BitBoard executedMoveBoard = board.doMoveAndReturnBitboard(move,maximizingPlayer);

                List<byte[]> childBestMoves = new ArrayList<>();
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
            bestMoves.clear();
            bestMoves.addAll(currentBestMoves);
            //System.out.println("Minimizing Player Best Moves: " + bestMoves);
            return minEval;
        }
    }*/

/*TODO: CURRENT, ERROR

    public int alphaBeta(BitBoard board, int depth, int alpha, int beta, boolean maximizingPlayer, boolean isRed, List<byte[]> bestMoves) {
        if (System.currentTimeMillis() > endTime) {
            return 0; // Return a neutral value if time limit is reached
        }
        boolean canWin = null != BitBoardManipulation.canWinWithMovesFusioned(isRed, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
        if (depth == 0 || board.checkWinCondition() != BitBoard.WINNER_ONGOING||canWin) {
            //TODO: isRed worked, now replaced by true
            int eval = Evaluate.evaluateComplex(true, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
            //Depth 0, to account for attacked pieces afterwards

            if(depth == 0){//If ruhesuche gets a value, return ruhesuche (doesnt take long)
                //Only do Ruhesuche if canwin not set to "winning soon" aka fusion or on winning spot already to not return a false (after FORCED ruhesuche/attacking) value
                int[] ruhesucheArray = (Math.abs(eval)<2000000000)? BitBoardManipulation.ruhesucheWithPositions(board,isRed):null;
                return (ruhesucheArray == null)? eval : ruhesucheArray[ruhesucheArray.length-1];
            }
            return eval;
        }

        byte[][] moves = board.getAllPossibleMovesByte(isRed);

        List<byte[]> currentBestMoves = new ArrayList<>();

        int evalBound = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (byte[] move : moves) {
            BitBoard executedMoveBoard = board.doMoveAndReturnBitboard(move, maximizingPlayer);

            List<byte[]> childBestMoves = new ArrayList<>();
            int eval = alphaBeta(executedMoveBoard, depth - 1, alpha, beta, !maximizingPlayer, isRed, childBestMoves);
            boolean condition = maximizingPlayer ? (eval > evalBound) : (eval < evalBound);

            if (condition) {
                evalBound = eval;
                currentBestMoves.clear();
                currentBestMoves.add(move);
                currentBestMoves.addAll(childBestMoves);
            }
            if (maximizingPlayer) {
                alpha = Math.max(alpha, eval);
            } else {
                beta = Math.min(beta, eval);
            }
            if (beta <= alpha) {
                break; // Alpha-Beta cutoff
            }
        }
        bestMoves.clear();
        bestMoves.addAll(currentBestMoves);
        //System.out.println((maximizingPlayer ? "Maximizing" : "Minimizing") + " Player Best Moves: " + bestMoves);
        return evalBound;
    }*/

/*    public ArrayList<String> findBestMove(BitBoard board, boolean isRed, int timeLimitMillis) {
        endTime = System.currentTimeMillis() + timeLimitMillis;
        bestDepthReached = 0;
        ArrayList<String> bestMoveSequence = new ArrayList<>();
        int currentBestValue;
        for (int depth = 1; ; depth++) {

            Tools.printInColor("New Depth reached: "+depth,true);

            currentBestValue = isRed ? Integer.MIN_VALUE : Integer.MAX_VALUE; // Initialize based on the starting player
            ArrayList<String> currentBestMoveSequence = new ArrayList<>();
            List<String> legalMoves = board.getAllPossibleMoveStringsDeprecated(isRed);
            //System.out.println("Moves here:" + legalMoves);
            //System.out.println("isRedTurn:" + isRed);

            for (String move : legalMoves) {

                BitBoard executedMoveBoard = BitBoard.fromLongArray(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                        Tools.parseMove(move)[0], Tools.parseMove(move)[1], isRed,
                        board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                        board.red_on_blue, board.blue_on_red));

                List<String> childBestMoves = new ArrayList<>();
                int moveValue = alphaBeta(executedMoveBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, !isRed, !isRed, childBestMoves);
                //System.out.println("AlphaBetaStart: move: "+move+" has value:"+moveValue);
                if (isRed ? moveValue > currentBestValue : moveValue < currentBestValue) { // Compare based on the starting player
                    currentBestValue = moveValue;
                    currentBestMoveSequence.clear();
                    currentBestMoveSequence.add(move);
                    currentBestMoveSequence.addAll(childBestMoves);
                }

                if (System.currentTimeMillis() > endTime) {
                    break; // Time limit reached
                }
            }
            System.out.println("AlphaBetaStart for DEPTH: "+depth +" bestmoveValue " +currentBestValue+ " sequence:"+bestMoveSequence);


            if (System.currentTimeMillis() > endTime) {
                break; // Time limit reached
            }

            bestMoveSequence = currentBestMoveSequence;
            bestDepthReached = depth;
        }

        // Print final search information
        System.out.println("Best Move Sequence: " + bestMoveSequence);
        System.out.println("Depth Reached: " + bestDepthReached);
        System.out.println("Current best valuee: " + currentBestValue);

        System.out.println("Time Elapsed: " + (System.currentTimeMillis() - (endTime - timeLimitMillis)) + " ms");

        return bestMoveSequence;
    }*/

    public ArrayList<byte[]> findBestMove(BitBoard board, boolean isRed, int timeLimitMillis) {
        endTime = System.currentTimeMillis() + timeLimitMillis;
        bestDepthReached = 0;
        ArrayList<byte[]> bestMoveSequence = new ArrayList<>();
        int currentBestValue;
        for (int depth = 1; ; depth++) {

            Tools.printInColor("New Depth reached: "+depth,true);

            currentBestValue = isRed ? Integer.MIN_VALUE : Integer.MAX_VALUE; // Initialize based on the starting player
            ArrayList<byte[]> currentBestMoveSequence = new ArrayList<>();
            byte[][] moves = board.getAllPossibleMovesByte(isRed);
            //System.out.println("Moves here:" + legalMoves);
            //System.out.println("isRedTurn:" + isRed);

            for (byte[] move : moves) {

                BitBoard executedMoveBoard = board.doMoveAndReturnBitboard(move,isRed);


                List<byte[]> childBestMoves = new ArrayList<>();
                int moveValue = alphaBeta(executedMoveBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, !isRed, !isRed, childBestMoves);
                //System.out.println("AlphaBetaStart: move: "+move+" has value:"+moveValue);
                if (isRed ? moveValue > currentBestValue : moveValue < currentBestValue) { // Compare based on the starting player
                    currentBestValue = moveValue;
                    currentBestMoveSequence.clear();
                    currentBestMoveSequence.add(move);
                    currentBestMoveSequence.addAll(childBestMoves);
                }

                if (System.currentTimeMillis() > endTime) {
                    break; // Time limit reached
                }
            }
            System.out.println("AlphaBetaStart for DEPTH: "+depth +" bestmoveValue " +currentBestValue+ " sequence:"+Tools.byteListToMoveSequence(bestMoveSequence));


            if (System.currentTimeMillis() > endTime) {
                break; // Time limit reached
            }

            bestMoveSequence = currentBestMoveSequence;
            bestDepthReached = depth;
        }

        // Print final search information
        System.out.println("Best Move Sequence: " + Tools.byteListToMoveSequence(bestMoveSequence));
        System.out.println("Depth Reached: " + bestDepthReached);
        System.out.println("Current best valuee: " + currentBestValue);

        System.out.println("Time Elapsed: " + (System.currentTimeMillis() - (endTime - timeLimitMillis)) + " ms");

        return bestMoveSequence;
    }

/*    public ArrayList<String> findBestMove(BitBoard board, boolean isRed, int timeLimitMillis) {
        endTime = System.currentTimeMillis() + timeLimitMillis;
        bestDepthReached = 0;
        ArrayList<String> bestMoveSequence = new ArrayList<>();

        for (int depth = 1; ; depth++) {
            int currentBestValue = Integer.MIN_VALUE;
            ArrayList<String> currentBestMoveSequence = new ArrayList<>();
            List<String> legalMoves = board.getAllPossibleMoves(isRed);
            System.out.println("Moves here:"+legalMoves);
            System.out.println("isRedTurn:"+isRed);
            for (String move : legalMoves) {
                BitBoard executedMoveBoard = BitBoard.fromLongArray(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                        Tools.parseMove(move)[0], Tools.parseMove(move)[1], isRed,
                        board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                        board.red_on_blue, board.blue_on_red));

                List<String> childBestMoves = new ArrayList<>();
                int moveValue = alphaBeta(executedMoveBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, false, isRed, childBestMoves);
                if (moveValue > currentBestValue) {
                    currentBestValue = moveValue;
*//*                    currentBestMoveSequence.clear();
                    currentBestMoveSequence.add(move);
                    currentBestMoveSequence.addAll(childBestMoves);*//*
                    bestMoveSequence.clear();
                    bestMoveSequence.add(move);
                    bestMoveSequence.addAll(childBestMoves);
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
    }*/

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

