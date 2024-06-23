package ai;

import misc.Tools;
import model.BitBoard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static ai.Evaluate.MAXIMUM_WITH_BUFFER_POSITIVE;
import static model.BitBoard.WINNER_BLUE;
import static model.BitBoard.WINNER_RED;

public class MerthanAlphaBetaExperiment {

    public long endTime;
    public int bestDepthReached;


/*        //TODO REMOVE
        if((board.redSingles &(1L << Tools.positionToIndex("C1")))!=0){
            System.out.println("won here");
        }
        if(board.previousMove[0] == 49 && board.previousMove[1] == 58){//B2-C1
            System.out.println("won here 2");
            miscCounter++;
        }*/

/*        if(board.previousMove[0] == 49 && board.previousMove[1] == 58){//B2-C1 is 49 58, C1-B2 is 58,49
            System.out.println("won here 2");
            miscCounter++;
        }*/

/*        if(board.previousMove[board.previousMove.length-2] == 57 && board.previousMove[board.previousMove.length-1] == 48){//B2-C1 is 49 58, C1-B2 is 57,48
            //System.out.println("prevented here 2");
            miscCounter++;
        }*/

    //Required so that Evaluation isnt called lots of times while sorting, so needs to be saved somewhere in relation to the moves
    public static byte[][] sortMovesPerformant(BitBoard board,boolean maximizingPlayer){
        byte[][] moves = board.getAllPossibleMovesByte(maximizingPlayer);
        final int multiplier = (maximizingPlayer?-1:1);

        int[][] movesAndEval = new int[moves.length][3];
        for (int i = 0; i < moves.length; i++) {
            byte[] move = moves[i];
            int eval = Evaluate.evaluateMoveComplex(true,move[0],move[1],board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
            movesAndEval[i]= new int[]{move[0],move[1],multiplier*eval};
        }
        Arrays.sort(movesAndEval, Comparator.comparingInt(a -> a[2]));//Compare by eval

        byte[][] byteArray = new byte[moves.length][2];

        for (int i = 0; i < moves.length; i++) {
            byteArray[i][0] = (byte) movesAndEval[i][0];
            byteArray[i][1] = (byte) movesAndEval[i][1];
        }
        return byteArray;
    }

    static final boolean sortMovesBeforeEach =true;


    public static int counter =0;
    public static int endReachedCounter = 0;
    public static int miscCounter = 0;
    public static int cutoffCounter = 0;
    public int alphaBeta(BitBoard board, int depth, int alpha, int beta, boolean maximizingPlayer,List<byte[]> bestMoves) {
        if (System.currentTimeMillis() > endTime) {//Deactivated for debugging with  && false
            //return 0; // Return a neutral value if time limit is reached
            return maximizingPlayer? -MAXIMUM_WITH_BUFFER_POSITIVE : MAXIMUM_WITH_BUFFER_POSITIVE; // Return worst value when time limit reached to not pick these
        }
        counter++;


        boolean canWin = null != BitBoardManipulation.canWinWithMovesFusioned(maximizingPlayer, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
        boolean depthZero = depth == 0;
        boolean gameEnded = board.currentWinningState() != BitBoard.WINNER_ONGOING;

        if (depthZero|| gameEnded ||canWin) {
            //TODO: isRed worked, now replaced by true
            int eval = Evaluate.evaluateComplex(true, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
            endReachedCounter++;
            //Depth 0, to account for attacked pieces afterwards
            if(log && gameEnded){
                //board.printCommented("GameEnded at depth "+depth+ " state:"+board.currentWinningState());
                //System.out.println(board.eval() + board.previousMoves()+counter);
            }

            if(depthZero){//If ruhesuche gets a value, return ruhesuche (doesnt take long)
                //Only do Ruhesuche if canwin not set to "winning soon" aka fusion or on winning spot already to not return a false (after FORCED ruhesuche/attacking) value
                int[] ruhesucheArray = (Math.abs(eval)<2000000000)? BitBoardManipulation.ruhesucheWithPositions(board,maximizingPlayer):null; // If not winning/canwin next move, do ruhesuche. else it doesnt matter
                return (ruhesucheArray == null)? eval : ruhesucheArray[ruhesucheArray.length-1];
            }else{//Aka canWin || gameEnded
                //MODIFIED: previously only for canWin, Now its either canWin or gameEnded that shows a preference for higher depth/less moves
                return maximizingPlayer?(eval+depth):(eval-depth);//Prefer moves with higher depth, needs buffer so changed Integer.MAX_VALUE
            }
            //return eval;
        }

        byte[][] moves = board.getAllPossibleMovesByte(maximizingPlayer);
        if(sortMovesBeforeEach){
            final int multiplier = (maximizingPlayer?-1:1);
            //Arrays.sort(moves, Comparator.comparingInt(e ->multiplier * Evaluate.evaluateMoveComplex(maximizingPlayer,e[0],e[1],board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red)));
            //moves = sortMovesPerformant(board,maximizingPlayer);

        }

        //Arrays.stream(moves).toList().stream().map(e->Tools.parseMoveToString(e)).collect(Collectors.joining(","))
        List<byte[]> currentBestMoves;
        if(saveSequence){
            currentBestMoves = new ArrayList<>();
        }

        int evalBound = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;//These dont need the buffer variable
        for (byte[] move : moves) {
            BitBoard executedMoveBoard = board.doMoveAndReturnBitboard(move, maximizingPlayer);
            List<byte[]> childBestMoves;
            int eval;
            if(saveSequence){
                childBestMoves= new ArrayList<>();
                eval = alphaBeta(executedMoveBoard, depth - 1, alpha, beta, !maximizingPlayer, childBestMoves);
            }else{
                eval = alphaBeta(executedMoveBoard, depth - 1, alpha, beta, !maximizingPlayer, null);
            }
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
                if(saveSequence){
                    currentBestMoves.clear();
                    currentBestMoves.add(move);
                    currentBestMoves.addAll(childBestMoves);
                }
                //System.out.println("Debug: New best move: " + Tools.parseMoveToString(move) + " with eval " + evalBound+ "for max:"+maximizingPlayer);
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
        if(saveSequence){
            bestMoves.clear();
            bestMoves.addAll(currentBestMoves);
        }


        //System.out.println((maximizingPlayer ? "Maximizing" : "Minimizing") + " Player Best Moves: " + bestMoves);
        return evalBound;
    }

    public final static boolean saveSequence = true;

    public final static boolean log = false; //CHANGE WHEN NEEDED
    public final static boolean detailedLog = false;

    public List<byte[]> findBestMove(BitBoard board, boolean isRed, int timeLimitMillis) {
        endTime = System.currentTimeMillis() + timeLimitMillis;
        bestDepthReached = 0;
        ArrayList<byte[]> bestMoveSequence = new ArrayList<>();
        int currentBestValue;
        int confirmedBestValue = isRed ? Integer.MIN_VALUE : Integer.MAX_VALUE; // Only after all options have gone through
        byte[] winningMoves = BitBoardManipulation.canWinWithMovesFusioned(isRed, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
        if(winningMoves!=null){ // If we can win, dont do alphabeta search, just do the moves to win
            if(winningMoves.length==2){
                return List.of(winningMoves);//new ArrayList<>(new byte[]{(byte)2});
            }
            if(winningMoves.length==3){
                return List.of(new byte[]{winningMoves[0], winningMoves[1]});
            }
        }

        byte[][] moves = board.getAllPossibleMovesByte(isRed);
        //without streams: 2061100n with streams: 2317900
        long start = System.nanoTime();
        //if(log) Tools.printRed("Before sort:"+Arrays.stream(moves).map(Tools::parseMoveToString).toList());
        //TODO: can be removed, no benefit I guess
        Arrays.sort(moves, Comparator.comparingInt(e -> (isRed?-1:1)* Evaluate.evaluateMoveComplex(isRed,e[0],e[1],board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red)));
        //if(log) Tools.printBlue("After sort:"+Arrays.stream(moves).map(Tools::parseMoveToString).toList());
        if(detailedLog)System.out.println("Sorting took: "+(System.nanoTime()-start));
        //if(true)System.exit(0);
        for (int depth = 1; ; depth++) {


            if(log)Tools.printInColor("New Depth reached: "+depth,true);

            currentBestValue = isRed ? Integer.MIN_VALUE : Integer.MAX_VALUE; // Initialize based on the starting player, dont need the buffer variable
            //if(depth==6)break; //TODO REMOVE
            ArrayList<byte[]> currentBestMoveSequence = new ArrayList<>();
            //byte[][] moves = board.getAllPossibleMovesByte(isRed);
            //System.out.println("Moves here:" + legalMoves);
            //System.out.println("isRedTurn:" + isRed);

            boolean didCompleteAndResultsAreValid = true;

            for (int i=0;i<moves.length;i++) {
                byte[] move = moves[i];

                BitBoard executedMoveBoard = board.doMoveAndReturnBitboard(move,isRed);
                byte winState =executedMoveBoard.currentWinningState();
                if(winState == (isRed?WINNER_RED:WINNER_BLUE)){ // If found winning move first move, just do it
                    if(log) System.out.println("WON, NO ALPHABETA");
                    return List.of(move);
                }

                //List<String> childBestMoves = new ArrayList<>();
                List<byte[]> childBestMoves = null;
                if(saveSequence)childBestMoves = new ArrayList<>();

                int moveValue = alphaBeta(executedMoveBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, !isRed, childBestMoves);
                if(detailedLog)System.out.println("AlphaBetaStart: move: "+Tools.parseMoveToString(move)+" has value:"+moveValue+" \tsequence:"+ (childBestMoves != null ? childBestMoves.stream().map(Tools::parseMoveToString).toList() : "empty"));
                if (isRed ? moveValue > currentBestValue : moveValue < currentBestValue) { // Compare based on the starting player
                    currentBestValue = moveValue;
                    currentBestMoveSequence.clear();
                    currentBestMoveSequence.add(move);
                    //childBestMoves.stream().map(Tools::parseMove).toList()
                    if(saveSequence)currentBestMoveSequence.addAll(childBestMoves);//Else we just add the move
                }

                if (System.currentTimeMillis() > endTime) {
                    didCompleteAndResultsAreValid = false;
                    if(log){
                        Tools.printRed("Time limit reached in findBest Moves, discarded dumb best move sequence: "+Tools.byteListToMoveSequence(currentBestMoveSequence)+" depth "+depth+", only completed "+i+"/"+moves.length+" last: "+Tools.parseMoveToString(move));
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


            if (System.currentTimeMillis() > endTime) {
                if(log)System.out.println("time limit reached in going through depths");
                break; // Time limit reached
            }


        }


        if(log)System.out.println("Best Move Sequence: " + Tools.byteListToMoveSequence(bestMoveSequence));
        if(log)System.out.println("Depth Reached: " + bestDepthReached);
        if(log)System.out.println("Current best valuee: " + currentBestValue);
        if(log)System.out.println("AlphaBeta method was called: " + counter+" and end point reached/Evaluated: "+endReachedCounter+ " cutoffs: "+cutoffCounter+" misc"+miscCounter);
        if(log)System.out.println("Time Elapsed: " + (System.currentTimeMillis() - (endTime - timeLimitMillis)) + " ms");
        counter=0;
        endReachedCounter =0;



        return bestMoveSequence;
    }



}

