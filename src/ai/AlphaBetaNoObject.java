package ai;

import ai.transpotest.FastTranspo;
import ai.transpotest.Zobrist;
import misc.Tools;
import model.BitBoard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static ai.Evaluate.MAXIMUM_WITH_BUFFER_POSITIVE;
import static model.BitBoard.WINNER_BLUE;
import static model.BitBoard.WINNER_RED;

public class AlphaBetaNoObject {


    public static int counter =0;
    public static int endReachedCounter = 0;
    public static int miscCounter = 0;
    public static int cutoffCounter = 0;


    static final boolean sortMovesBeforeEach = true;
    static final boolean useTranspositionTable = true; //switch Transposition Table
    public final static boolean saveSequence = true;

    public final static boolean log = false; //CHANGE WHEN NEEDED
    public final static boolean detailedLog = false;

    public static long ruhesucheTime=0;

    public long endTime;
    public int bestDepthReached;

    int transpoCounter = 0;



    public List<byte[]> findBestMoveNoObjects(boolean isRed, int timeLimitMillis, long r, long b, long rr, long bb, long br, long rb) {
        endTime = System.currentTimeMillis() + timeLimitMillis;

        bestDepthReached = 0;
        ArrayList<byte[]> bestMoveSequence = new ArrayList<>();
        int currentBestValue = isRed ? Integer.MIN_VALUE : Integer.MAX_VALUE; // Initialize based on the starting player, dont need the buffer variable
//TODO: Changed from not initialized to 0, might cause bugs

        int confirmedBestValue = isRed ? Integer.MIN_VALUE : Integer.MAX_VALUE; // Only after all options have gone through
        byte[] winningMoves = BitBoardManipulation.canWinWithMovesFusioned(isRed, r,b,rr,bb,br,rb);
        if(winningMoves!=null){ // If we can win, dont do alphabeta search, just do the moves to win
            if(winningMoves.length==2){
                return List.of(winningMoves);//new ArrayList<>(new byte[]{(byte)2});
            }
            if(winningMoves.length==3){
                return List.of(new byte[]{winningMoves[0], winningMoves[1]});
            }
        }

        byte[][] moves = BitBoard.generateSortedByteMovesForPiecesWithListStatic(isRed,r,b,rr,bb,br,rb);
        //without streams: 2061100n with streams: 2317900
        long start = System.nanoTime();
        //if(log) Tools.printRed("Before sort:"+Arrays.stream(moves).map(Tools::parseMoveToString).toList());
        //TODO: can be removed, no benefit I guess
        Arrays.sort(moves, Comparator.comparingInt(e -> (isRed?-1:1)* Evaluate.evaluateMoveComplex(isRed,e[0],e[1],r,b,rr,bb,br,rb)));
        //if(log) Tools.printBlue("After sort:"+Arrays.stream(moves).map(Tools::parseMoveToString).toList());
        if(detailedLog)System.out.println("Sorting took: "+(System.nanoTime()-start));
        //if(true)System.exit(0);
        int lastIndexReached=0;
        for (int depth = 1; ; depth++) {


            if(log) Tools.printInColor("|New Depth reached: "+depth+" currentbest:"+currentBestValue+" move length "+moves.length+" "+(BitBoard.currentWinningStateStaticOptimized(r,b,rr,bb,br,rb)),true);

            currentBestValue = isRed ? Integer.MIN_VALUE : Integer.MAX_VALUE; // Initialize based on the starting player, dont need the buffer variable
            //if(depth==6)break; //TODO REMOVE
            ArrayList<byte[]> currentBestMoveSequence = new ArrayList<>();
            //byte[][] moves = board.getAllPossibleMovesByte(isRed);
            //System.out.println("Moves here:" + legalMoves);
            //System.out.println("isRedTurn:" + isRed);

            boolean didCompleteAndResultsAreValid = true;

            for (int i=0;i<moves.length;i++) {
                byte[] move = moves[i];

                long[] executedMoveBoard = BitBoardManipulation.doMoveAndReturnModifiedBitBoards(move[0],move[1],isRed,r,b,rr,bb,br,rb);//.doMoveAndReturnBitboard(move,isRed);
                byte winState =BitBoard.currentWinningStateStaticOptimized(executedMoveBoard[0],executedMoveBoard[1],executedMoveBoard[2],executedMoveBoard[3],executedMoveBoard[4],executedMoveBoard[5]);
                if(winState == (isRed?WINNER_RED:WINNER_BLUE)){ // If found winning move first move, just do it
                    if(log) System.out.println("WON, NO ALPHABETA");
                    return List.of(move);
                }

                //List<String> childBestMoves = new ArrayList<>();
                if(depth>=1000){
                    //Math.abs(0);
                    BitBoard.fromLongArray(executedMoveBoard).printCommented("depth too high"+Tools.movesToString(moves)+" >>"+ (winningMoves != null ? winningMoves.length : -1));
                    //System.exit(0);
                }

                int moveValue = alphaBetaLessObjects(depth, Integer.MIN_VALUE, Integer.MAX_VALUE, !isRed, executedMoveBoard[0],executedMoveBoard[1],executedMoveBoard[2],executedMoveBoard[3],executedMoveBoard[4],executedMoveBoard[5]);
                if(detailedLog)System.out.println("|AlphaBetaStart: move: "+Tools.parseMoveToString(move)+" has value:"+moveValue+" \tsequence:empty");
                if (isRed ? moveValue > currentBestValue : moveValue < currentBestValue) { // Compare based on the starting player
                    currentBestValue = moveValue;
                    currentBestMoveSequence.clear();
                    currentBestMoveSequence.add(move);
                    //childBestMoves.stream().map(Tools::parseMove).toList()
                    //if(saveSequence)currentBestMoveSequence.addAll(childBestMoves);//Else we just add the move
                }



                if (System.currentTimeMillis() > endTime) {//TODO: BUG; WHEN NOT SET TO && FALSE; TOO MUCH DEPTH
                    didCompleteAndResultsAreValid = false;
                    if(log){
                        Tools.printRed("Time limit reached in findBest Moves, discarded dumb best move sequence: "+Tools.byteListToMoveSequence(currentBestMoveSequence)+" depth "+depth+", only completed "+i+"/"+moves.length+" last: "+Tools.parseMoveToString(move));
                        lastIndexReached=i;
                    }
                    break; // Time limit reached
                }
            }


            if(didCompleteAndResultsAreValid){
                bestMoveSequence = currentBestMoveSequence;
                confirmedBestValue = currentBestValue;
            }

            bestDepthReached = depth;

            if(detailedLog)System.out.println("AlphaBetaStart for DEPTH: "+depth +" bestmoveValue " +confirmedBestValue+ " sequence:"+Tools.byteListToMoveSequence(bestMoveSequence));


            if (System.currentTimeMillis() > endTime) {//TODO REMOVE false
                if(log)System.out.println("time limit reached in going through depths");
                break; // Time limit reached
            }


        }
        miscCounter = ((lastIndexReached+1) * 100) / moves.length;

        //if(log)System.out.println("Best Move Sequence: " + Tools.byteListToMoveSequence(bestMoveSequence));
        //if(log)System.out.println("Current best valuee: " + currentBestValue);
        //if(log)System.out.println("Depth Reached: " + bestDepthReached+" and last index was "+lastIndexReached+"/"+moves.length);
        if(log)System.out.println("AlphaBeta called: " + counter+" End Evaluated: "+endReachedCounter+ " Cuts: "+cutoffCounter+" Depth Reached: " + bestDepthReached+" and last index was "+lastIndexReached+"/"+moves.length+" misc: depth"+bestDepthReached+": "+miscCounter+"%"+" TranspoUsed:"+transpoCounter);
        //if(log)System.out.println("Time Elapsed: " + (System.currentTimeMillis() - (endTime - timeLimitMillis)) + " ms Ruhesuche took:"+ ruhesucheTime);
        ruhesucheTime=0;
        counter=0;
        endReachedCounter =0;
        transpoCounter=0;



        return bestMoveSequence;
    }



    public int alphaBetaLessObjects(int depth, int alpha, int beta, boolean maximizingPlayer, long r, long b, long rr, long bb, long br, long rb) {
        if (System.currentTimeMillis() > endTime) {//Deactivated for debugging with  && false
            //return 0; // Return a neutral value if time limit is reached
            return maximizingPlayer? -MAXIMUM_WITH_BUFFER_POSITIVE : MAXIMUM_WITH_BUFFER_POSITIVE; // Return worst value when time limit reached to not pick these
        }
        counter++;


        boolean canWin = null != BitBoardManipulation.canWinWithMovesFusioned(maximizingPlayer, r,b,rr,bb,br,rb);
        boolean depthZero = depth == 0;
        boolean gameEnded = BitBoard.currentWinningStateStaticOptimized(r,b,rr,bb,br,rb) != BitBoard.WINNER_ONGOING;

        if (depthZero|| gameEnded ||canWin) {
            //TODO: isRed worked, now replaced by true
            int eval = Evaluate.evaluateComplex(r,b,rr,bb,br,rb);
            endReachedCounter++;
            //Depth 0, to account for attacked pieces afterwards

            if(depthZero){//If ruhesuche gets a value, return ruhesuche (doesnt take long)

                //Only do Ruhesuche if canwin not set to "winning soon" aka fusion or on winning spot already to not return a false (after FORCED ruhesuche/attacking) value
                //long start = System.currentTimeMillis();
                int ruhesucheEval = (Math.abs(eval)<2000000000)? BitBoardManipulation.ruhesucheWithPositionsOptimized(maximizingPlayer,r,b,rr,bb,br,rb):BitBoardManipulation.RUHESUCHE_NOT_PERFORMED; // If not winning/canwin next move, do ruhesuche. else it doesnt matter
                //ruhesucheTime += System.currentTimeMillis()-start;
                return (ruhesucheEval == BitBoardManipulation.RUHESUCHE_NOT_PERFORMED)? eval : ruhesucheEval;
/*                int ruhesucheArray = (Math.abs(eval)<2000000000)? BitBoardManipulation.ruhesuche(board,maximizingPlayer):BitBoardManipulation.RUHESUCHE_NOT_PERFORMED; // If not winning/canwin next move, do ruhesuche. else it doesnt matter
                ruhesucheTime += System.nanoTime()-start;
                return (ruhesucheArray == BitBoardManipulation.RUHESUCHE_NOT_PERFORMED)? eval : ruhesucheArray;*/

            }else{//Aka canWin || gameEnded
                //MODIFIED: previously only for canWin, Now its either canWin or gameEnded that shows a preference for higher depth/less moves
                return maximizingPlayer?(eval+depth):(eval-depth);//Prefer moves with higher depth, needs buffer so changed Integer.MAX_VALUE
            }
            //return eval;
        }

        byte[][] moves = BitBoard.generateSortedByteMovesForPiecesWithListStatic(maximizingPlayer,r,b,rr,bb,br,rb); //sortMovesBeforeEach ? board.getAllPossibleMovesByteSorted(maximizingPlayer):board.getAllPossibleMovesByte(maximizingPlayer);

        //Arrays.stream(moves).toList().stream().map(e->Tools.parseMoveToString(e)).collect(Collectors.joining(","))

        int evalBound = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;//These dont need the buffer variable
        for (byte[] move : moves) {
            long[] executedMoveBoard = BitBoardManipulation.doMoveAndReturnModifiedBitBoards(move[0],move[1],maximizingPlayer,r,b,rr,bb,br,rb);//board.doMoveAndReturnBitboard(move, maximizingPlayer);
            int eval = alphaBetaLessObjects(depth - 1, alpha, beta, !maximizingPlayer, executedMoveBoard[0], executedMoveBoard[1], executedMoveBoard[2], executedMoveBoard[3], executedMoveBoard[4], executedMoveBoard[5]);
            boolean condition = maximizingPlayer ? (eval > evalBound) : (eval < evalBound);

/*            if(eval>2100000000){
                System.out.println(depth+"won remove"+(eval-2100000000));

                System.out.println(executedMoveBoard.previousMoves()+ executedMoveBoard.previousMove);
                if(executedMoveBoard.previousMoves().contains("[C7-C6] [C3-C4] [C6-B6] [C4-B4]")){
                    executedMoveBoard.printCommented("Mod");

                }
                //executedMoveBoard.print();
                //System.out.println("evaal:"+executedMoveBoard.eval());
                //System.out.println("done");
            }*/

            if (condition) {
                evalBound = eval;
            }

            if (maximizingPlayer) {
                alpha = Math.max(alpha, eval);
            } else {
                beta = Math.min(beta, eval);
            }
            if (beta <= alpha) {
                cutoffCounter++;
                break; // Alpha-Beta cutoff
            }

        }
        //System.out.println((maximizingPlayer ? "Maximizing" : "Minimizing") + " Player Best Moves: " + bestMoves);
        return evalBound;
    }

}
