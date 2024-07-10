package ai;

import ai.transpotest.FastTranspo;
import ai.transpotest.Zobrist;
import misc.Tools;
import misc.deprecated.TranspositionTable;
import misc.deprecated.ZobristHashing;
import model.BitBoard;
import model.BoardException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static ai.Evaluate.MAXIMUM_WITH_BUFFER_POSITIVE;
import static model.BitBoard.WINNER_BLUE;
import static model.BitBoard.WINNER_RED;

public class MerthanAlphaBetaExperiment {

    public static int DELETE_UPPER_DEPTH_LIMIT = 125;//Probably can stay
    public long endTime;
    public int bestDepthReached;
    public FastTranspo fastTranspo;
    public Zobrist zobrist;

    public static int counter = 0;
    public static int endReachedCounter = 0;
    public static int miscCounter = 0;
    public static int cutoffCounter = 0;


    static final boolean sortMovesBeforeEach = true;//Deprecated, now always true
    static final boolean useTranspositionTable = true; //switch Transposition Table
    public final static boolean saveSequence = false;
    public final static boolean dynamicTimeManagement = false;


    public final static boolean log = true; //CHANGE WHEN NEEDED
    public final static boolean detailedLog = false;

    public static long ruhesucheTime = 0;

    public int currentStartDepth = 0;


    int transpoCounter = 0;
    boolean timeoutReached = false;
    final static byte z = 0; //Faster, fixes compilation

    static int printCounter = 0;


    public MerthanAlphaBetaExperiment() {
        if (useTranspositionTable) {
            fastTranspo = new FastTranspo();
            zobrist = fastTranspo.zobrist;
        }
    }


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
    public static byte[][] sortMovesPerformant(BitBoard board, boolean maximizingPlayer) {
        byte[][] moves = board.getAllPossibleMovesByte(maximizingPlayer);
        final int multiplier = (maximizingPlayer ? -1 : 1);

        int[][] movesAndEval = new int[moves.length][3];
        for (int i = 0; i < moves.length; i++) {
            byte[] move = moves[i];
            int eval = Evaluate.evaluateMoveComplex(true, move[0], move[1], board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
            movesAndEval[i] = new int[]{move[0], move[1], multiplier * eval};
        }
        Arrays.sort(movesAndEval, Comparator.comparingInt(a -> a[2]));//Compare by eval

        byte[][] byteArray = new byte[moves.length][2];

        for (int i = 0; i < moves.length; i++) {
            byteArray[i][0] = (byte) movesAndEval[i][0];
            byteArray[i][1] = (byte) movesAndEval[i][1];
        }
        return byteArray;
    }






    public int alphaBeta(BitBoard board, int depth, int alpha, int beta, boolean maximizingPlayer, List<byte[]> bestMoves, long incrementalZobristKey) {
        if (timeoutReached||System.currentTimeMillis() > endTime) {//Deactivated for debugging with  && false
            //return 0; // Return a neutral value if time limit is reached
            timeoutReached = true;//So no overhead calling currenttimemillis, gets reset in findbestmove
            return maximizingPlayer ? -MAXIMUM_WITH_BUFFER_POSITIVE : MAXIMUM_WITH_BUFFER_POSITIVE; // Return worst value when time limit reached to not pick these
        }
/*        if(incrementalZobristKey == 1669353610709612026L){//Test
            //System.out.println("hit");
        }*/

        int alphaOriginal = alpha;//TODO: moved up now (above the next if, so not updated if transpo value updated)
        int betaOriginal = beta; // Havent noticed a difference, still losing often to normal alphabeta
        //if(Arrays.equals(board.pre))
        //System.out.println("depth"+(currentStartDepth-depth));
        byte[] bestMoveFromTransposition = null;

        byte[][] moves = null; // Already declared here now, might need to be initialized and checked when seeing if bestSavedMove contained

        if (useTranspositionTable) {
            //Long boxedResult = fastTranspo.transpositionTable.get(incrementalZobristKey);
            //long entry = boxedResult == null ? 0 : boxedResult;

            long entry = fastTranspo.transpositionTable.getOrDefault(incrementalZobristKey,0);
            if (entry != 0) {
                //NEW; TEST; DELETE
/*                String s =fastTranspo.fenTableDebug.get(incrementalZobristKey);
                if(!(s.split(">>")[0]).equals(board.toFEN())){
                    Tools.printDivider();
                    System.out.println("Board mismatch");

                    board.printCommented("Current :"+board.previousMoves()+" | "+Arrays.toString(board.previousMove)+ " "+incrementalZobristKey);
                    new BitBoard((s.split(">>")[0])).printCommented("Saved, history :"+(s.split(">>")[1]));
                }*/

                if ((((entry >> 8) & 0xFFL)) >= depth) {//Unpacked, now lots more
                    transpoCounter++;
                    long type = (entry & 0xFFL);
                    if (type == 0) {//EXACT
                        if (saveSequence) {
                            bestMoves.clear();
                            bestMoves.add(new byte[]{(byte) ((entry >> 24) & 0xFFL), (byte) ((entry >> 16) & 0xFFL)});
                            //bestMoves.add(new byte[]{(byte)1,(byte)0});//TODO REMOVE
                        }
                        return (int) (entry >> 32);//Return eval
                    } else if (type == 1) {//Lower
                        alpha = Math.max(alpha, (int) (entry >> 32));
                        //evalBound = alpha;//maximizingPlayer? alpha: Integer.MAX_VALUE;//TODO: both of these are new, evalBound set here. Might be wrong
                    } else {//2 upper
                        beta = Math.min(beta, (int) (entry >> 32));
                        //evalBound = //maximizingPlayer? Integer.MIN_VALUE: beta;//TODO: these didnt work, other team won more
                    }
                    bestMoveFromTransposition = new byte[]{(byte) ((entry >> 24) & 0xFFL), (byte) ((entry >> 16) & 0xFFL)};
                    moves = board.getAllPossibleMovesByteSorted(maximizingPlayer); //: board.getAllPossibleMovesByte(maximizingPlayer);
                    byte fromPos = bestMoveFromTransposition[0];
                    boolean containsMove = false;
                    for (int i = 0; i < moves.length; i++) {
                        if (moves[i][0] == fromPos) {//Frompos saved for performance
                            if (moves[i][1] == bestMoveFromTransposition[1]) {
                                containsMove = true;
                            }
                        }
                    }
                    if (!containsMove) {
                        bestMoveFromTransposition = null; // IF MOVE NOT CONTAINED; DONT PASS A WRONG MOVE TO TRY IN ALPHABETA (HASH COLLISION;LOGIC ERROR ETC COULD CAUSE THIS)
                    }

                    if (alpha >= beta) {
                        if (saveSequence) {//Save again
                            bestMoves.clear();
                            bestMoves.add(new byte[]{(byte) ((entry >> 24) & 0xFFL), (byte) ((entry >> 16) & 0xFFL)});
                        }
                        return (int) (entry >> 32);//Return eval
                    }
                }
            }
        }

        counter++;

/*        if(board.previousMoves().contains("C1-B1")){
            Math.abs(0);
        }*/

        boolean canWin = null != BitBoardManipulation.canWinWithMovesFusioned(maximizingPlayer, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
        boolean depthZero = depth == 0;
        boolean gameEnded = board.currentWinningState() != BitBoard.WINNER_ONGOING;
        if (depthZero || gameEnded || canWin) {
            //TODO: isRed worked, now replaced by true
            int eval = Evaluate.evaluateComplex(board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
            endReachedCounter++;
            //Depth 0, to account for attacked pieces afterwards
            int finalEval;
            if (depthZero) {//If ruhesuche gets a value, return ruhesuche (doesnt take long)

                //Only do Ruhesuche if canwin not set to "winning soon" aka fusion or on winning spot already to not return a false (after FORCED ruhesuche/attacking) value
                long start = System.currentTimeMillis();
                int[] ruhesucheArray = (Math.abs(eval) < 2000000000) ? BitBoardManipulation.ruhesucheWithPositions(board, maximizingPlayer) : null; // If not winning/canwin next move, do ruhesuche. else it doesnt matter
                ruhesucheTime += System.currentTimeMillis() - start;
                finalEval = (ruhesucheArray == null) ? eval : ruhesucheArray[ruhesucheArray.length - 1];
                //if(useTranspositionTable)fastTranspo.debugStoreEntry(incrementalZobristKey,finalEval,z,z,(byte) depth,z,board);//EXACT
                //if(useTranspositionTable)fastTranspo.storeEntry(incrementalZobristKey,finalEval,z,z,(byte) depth,z,board);//EXACT
            } else {//Aka canWin || gameEnded
                //MODIFIED: previously only for canWin, Now its either canWin or gameEnded that shows a preference for higher depth/less moves
                finalEval = maximizingPlayer ? (eval + depth) : (eval - depth);
                //if(useTranspositionTable)fastTranspo.debugStoreEntry(incrementalZobristKey,finalEval,z,z,(byte) depth,z,board);//EXACT
                //return finalEval;//Prefer moves with higher depth, needs buffer so changed Integer.MAX_VALUE
            }
            if (useTranspositionTable) fastTranspo.storeEntry(incrementalZobristKey, finalEval, z, z, (byte) depth, z);//EXACT
            return finalEval;
        }
        if (moves == null) { // If not already initialized because we needed to check if transpo move is actually a valid move here (just in case); init only happening once for performance
            moves = board.getAllPossibleMovesByteSorted(maximizingPlayer); //: board.getAllPossibleMovesByte(maximizingPlayer);
        }
        //Arrays.stream(moves).toList().stream().map(e->Tools.parseMoveToString(e)).collect(Collectors.joining(","))
        List<byte[]> currentBestMoves;
        if (saveSequence) {
            currentBestMoves = new ArrayList<>();
        }

        int evalBound = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;//These dont need the buffer variable

        boolean cutoffOccurred = false;

        byte[] bestMove = null;
        //More complex now, checks transpo bestmove first if given
        for (int i = (useTranspositionTable && bestMoveFromTransposition != null) ? -1 : 0; i < moves.length; i++) {
            byte[] move = null;
            if (useTranspositionTable && i == -1) {//Also cant be null here cause then would start at 0
                move = bestMoveFromTransposition;
            } else {
                move = moves[i];
                if (useTranspositionTable && bestMoveFromTransposition != null
                        && bestMoveFromTransposition[0] == move[0] && bestMoveFromTransposition[1] == move[1]) {
                    //If all of this is the case and we are in the else, we have already looked at this move because transpo gave it (-1)
                    continue;
                }
            }

            long newZobristKeyAfterMove = 0;
            if (useTranspositionTable) {
                try {
                    newZobristKeyAfterMove = Zobrist.applyMove(incrementalZobristKey, move[0], move[1], board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
                } catch (BoardException E) {
                    E.printStackTrace();
                    board.printCommented("ZOBRIST EXCEPTION move:" + Tools.parseMoveToString(move));
                    Tools.printDivider();
                    throw new BoardException(board, "ZOBRIST EXCEPTION CAUGHT, debug:i=" + i + ">" + (Arrays.toString(bestMoveFromTransposition)) + " history:" + board.previousMoves());
                }
            }
            BitBoard executedMoveBoard = board.doMoveAndReturnBitboard(move, maximizingPlayer);
            List<byte[]> childBestMoves;
            int eval;

            if (saveSequence) {
                childBestMoves = new ArrayList<>();
                eval = alphaBeta(executedMoveBoard, depth - 1, alpha, beta, !maximizingPlayer, childBestMoves, newZobristKeyAfterMove);
            } else {
                eval = alphaBeta(executedMoveBoard, depth - 1, alpha, beta, !maximizingPlayer, null, newZobristKeyAfterMove);
            }

            if (maximizingPlayer) {
                if (eval > evalBound) {
                    evalBound = eval;
                    if (useTranspositionTable)
                        bestMove = move; //TODO: maybe slight performance impact but now best move is saved for transpo

                    if (saveSequence) {

                        currentBestMoves.clear();
                        currentBestMoves.add(move);
                        currentBestMoves.addAll(childBestMoves);
                    }
                    alpha = Math.max(alpha, eval);
                }
            } else {
                if (eval < evalBound) {
                    evalBound = eval;
                    if (useTranspositionTable)
                        bestMove = move; //TODO: maybe slight performance impact but now best move is saved for transpo

                    if (saveSequence) {

                        currentBestMoves.clear();
                        currentBestMoves.add(move);
                        currentBestMoves.addAll(childBestMoves);
                    }
                    beta = Math.min(beta, eval);
                }
            }


            if (beta <= alpha) {
                cutoffCounter++;
                cutoffOccurred = true;
                break; // Alpha-Beta cutoff
            }
            //AlphaBeta method was called: 9090503 and end point reached/Evaluated: 7787512 cutoffs: 1130481 misc0
            //AlphaBeta method was called: 5868583 and end point reached/Evaluated: 4222722 cutoffs: 1505685 misc0
        }


        if (saveSequence) {
            bestMoves.clear();
            bestMoves.addAll(currentBestMoves);
        }


        //System.out.println((maximizingPlayer ? "Maximizing" : "Minimizing") + " Player Best Moves: " + bestMoves);
        if (useTranspositionTable) {
            if (timeoutReached) {//Dont store wrong values in table
                return maximizingPlayer ? -MAXIMUM_WITH_BUFFER_POSITIVE : MAXIMUM_WITH_BUFFER_POSITIVE;
            }

            if (depth > 126) {
                return evalBound;//Depth too high to be stored but at this depth doesnt matter, in that case just ignore transpo
            }
            //newZobristKeyAfterMove =zobrist.applyMove(incrementalZobristKey,move[0],move[1],board.redSingles,board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
            //transpositionTable.put(incrementalZobristKey, depth, evalBound, alpha, beta, bestMoves.get(0));


/*            byte type = 0;//Start at exact
            if(evalBound <=alphaOriginal){
                type = 2;//upper
            }else if(evalBound>=betaOriginal){//TODO: testing once, not original was 2:8 (loss while original was 5:5)
                type = 1;//lower
            }

            if(cutoffOccurred && type ==0){
                //System.out.println("Cutoff occured when type 0, alpha "+alpha+" beta "+beta+" aO "+alphaOriginal+" b0 "+betaOriginal+" EvalBound: "+evalBound);
                //TODO: Seems to be an error, not storing anyways in this case
                return evalBound;
            }*/
            byte type = 0;
/*            if (cutoffOccurred) {
                // Cutoff occurred, set upper or lower bound
                if (evalBound <= alphaOriginal) {
                    type = 2;  // Upper bound
                } else if (evalBound >= betaOriginal) {
                    type = 1;  // Lower bound
                } else {
                    // should not happen if cutoff occurred correctly
                    //TODO: this doesnt hapen anymore ig
                    System.out.println("Error: Cutoff occurred but evalBound is within original alpha-beta bounds.");
                    return evalBound;  // Or handle this error case as appropriate
                }
            } else {
                // No cutoff occurred, store as exact if within original bounds
                // Handle this case as exact, as it's a fallback
                if (evalBound > alpha && evalBound < beta) {// TODO: was, with new no error anymore                 if (evalBound > alphaOriginal && evalBound < betaOriginal) {
                    // Shouldn't happen if evalBound, always within bounds during normal execution
                    System.out.println("Warning: EvalBound is not within original alpha-beta bounds but no cutoff occurred.");
                    return evalBound;
                }
                type = 0;  // Exact value
            }*/ //TODO: testing due to bad performance

            //byte type = 0;//Start at exact
            if (evalBound <= alphaOriginal) {
                type = 2;//upper
            } else if (evalBound >= betaOriginal) {//TODO: testing once, not original was 2:8 (loss while original was 5:5)
                type = 1;//lower
            }

            //if(detailedLog) Tools.printBlue("TranspoEval:"+type+" evalbound:"+evalBound+" a:"+alpha+" b:"+beta);
/*            byte[] prev = board.previousMove;
            byte from = prev[prev.length-2];//TODO: not sure if this or bestMove var
            byte to = prev[prev.length-1];*/
/*            if(incrementalZobristKey == 4195602139335292196L){
                System.out.println("lol");
            }*/
            //System.out.println("Stored:"+type);
/*            if((board.redSingles == 1599102L) && (board.blueSingles == 9112479692123799552L)){
                System.out.println("lol2");
            }*/

/*            if((currentStartDepth-depth)>2&&printCounter++<100){//TODO remove all of these print things
                long packed = fastTranspo.packEntry(evalBound,bestMove[0],bestMove[1],(byte)depth,type);
                //board.printCommented(incrementalZobristKey+" c"+printCounter+" depth: "+((currentStartDepth-depth))+ " packed: "+FastTranspo.entryToString(packed));
            }*/

            //boolean nn = bestMove !=null;
            if (bestMove == null) {//TODO new, not sure if makes sense, bestmove doesnt have a value sometimes anymore because evalbound is now set if transpo has value above
                return evalBound;
            }
            //board.print();
            fastTranspo.storeEntry(incrementalZobristKey, evalBound, bestMove[0], bestMove[1], (byte) depth, type);

        }
        return evalBound;
    }

    void debugStoreEntry() {

    }


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


    List<byte[]> allReturnedMovesForRemisDetection = new ArrayList<>();//TODO: paused, currently not used, should be handled in server

    int findBestMoveCounter =0;

    public List<byte[]> findBestMove(BitBoard board, boolean isRed, int timeLimitMillis) {
        timeoutReached = false;
        endTime = System.currentTimeMillis() + timeLimitMillis;

        endTime = dynamicTimeManagement ? (System.currentTimeMillis() + TIME_LIMIT) : (System.currentTimeMillis() + timeLimitMillis);
        bestDepthReached = 0;

        ArrayList<byte[]> bestMoveSequence = new ArrayList<>();

        if (useTranspositionTable) {
            zobrist.initializeCorrectBoardKey(board);//Sets the initial one, from here

            if(findBestMoveCounter++ % 10 == 0 && false){//TODO: enable if outofmemory error, then test. Clears table every 10th move
                fastTranspo.transpositionTable.clear();
            }
        }

        int currentBestValue = isRed ? Integer.MIN_VALUE : Integer.MAX_VALUE; // Initialize based on the starting player, dont need the buffer variable

        int confirmedBestValue = isRed ? Integer.MIN_VALUE : Integer.MAX_VALUE; // Only after all options have gone through
        byte[] winningMoves = BitBoardManipulation.canWinWithMovesFusioned(isRed, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
        if (winningMoves != null) { // If we can win, dont do alphabeta search, just do the moves to win
            if (winningMoves.length == 2) {
                return List.of(winningMoves);//new ArrayList<>(new byte[]{(byte)2});
            }
            if (winningMoves.length == 3) {
                return List.of(new byte[]{winningMoves[0], winningMoves[1]});
            }
        }

        byte[][] moves = board.getAllPossibleMovesByteSorted(isRed);// : board.getAllPossibleMovesByte(isRed);
        try {

            //without streams: 2061100n with streams: 2317900
            long start = System.nanoTime();
            //if(log) Tools.printRed("Before sort:"+Arrays.stream(moves).map(Tools::parseMoveToString).toList());
            //TODO: can be removed, no benefit I guess
            Arrays.sort(moves, Comparator.comparingInt(e -> (isRed ? -1 : 1) * Evaluate.evaluateMoveComplex(isRed, e[0], e[1], board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red)));
            //if(log) Tools.printBlue("After sort:"+Arrays.stream(moves).map(Tools::parseMoveToString).toList());
            if (detailedLog) System.out.println("Sorting took: " + (System.nanoTime() - start));
            //if(true)System.exit(0);
            int lastIndexReached = 0;
            for (int depth = 1; depth < DELETE_UPPER_DEPTH_LIMIT; depth++) {
                currentStartDepth = depth;

                if (log) Tools.printInColor("New Depth reached: " + depth + " currentbest:" + currentBestValue, true);

                currentBestValue = isRed ? Integer.MIN_VALUE : Integer.MAX_VALUE; // Initialize based on the starting player, dont need the buffer variable
                //if(depth==6)break; //TODO REMOVE
                ArrayList<byte[]> currentBestMoveSequence = new ArrayList<>();
                //byte[][] moves = board.getAllPossibleMovesByte(isRed);
                //System.out.println("Moves here:" + legalMoves);
                //System.out.println("isRedTurn:" + isRed);

                boolean didCompleteAndResultsAreValid = true;

                for (int i = 0; i < moves.length; i++) {

                    byte[] move = moves[i];
                    long updatedZobrist = useTranspositionTable ? Zobrist.applyMove(zobrist.currentBoardKey, move[0], move[1], board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red) : 0;

                    BitBoard executedMoveBoard = board.doMoveAndReturnBitboard(move, isRed);
                    byte winState = executedMoveBoard.currentWinningState();
                    if (winState == (isRed ? WINNER_RED : WINNER_BLUE)) { // If found winning move first move, just do it
                        if (log) System.out.println("WON, NO ALPHABETA");
                        return List.of(move);
                    }
                    //System.out.println("Prev:"+executedMoveBoard.previousMoves());

                    //List<String> childBestMoves = new ArrayList<>();
                    List<byte[]> childBestMoves = null;
                    if (saveSequence) childBestMoves = new ArrayList<>();
                    int moveValue = alphaBeta(executedMoveBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, !isRed, childBestMoves, updatedZobrist);
                    if (detailedLog)
                        System.out.println("AlphaBetaStart: move: " + Tools.parseMoveToString(move) + " has value:" + moveValue + " \tsequence:" + (childBestMoves != null ? childBestMoves.stream().map(Tools::parseMoveToString).toList() : "empty"));
                    if (isRed ? moveValue > currentBestValue : moveValue < currentBestValue) { // Compare based on the starting player
                        currentBestValue = moveValue;
                        currentBestMoveSequence.clear();
                        currentBestMoveSequence.add(move);
                        //childBestMoves.stream().map(Tools::parseMove).toList()
                        if (saveSequence) currentBestMoveSequence.addAll(childBestMoves);//Else we just add the move
                    }

                    if (System.currentTimeMillis() > endTime) {
                        didCompleteAndResultsAreValid = false;
                        if (log) {
                            Tools.printRed("Time limit reached in findBest Moves, discarded dumb best move sequence: " + Tools.byteListToMoveSequence(currentBestMoveSequence) + " depth " + depth + ", only completed " + i + "/" + moves.length + " last: " + Tools.parseMoveToString(move));
                            lastIndexReached = i;
                        }
                        break; // Time limit reached
                    }
                }

                if (didCompleteAndResultsAreValid) {
                    bestMoveSequence = currentBestMoveSequence;
                    confirmedBestValue = currentBestValue;
                }


                bestDepthReached = depth;

                if (detailedLog)
                    System.out.println("AlphaBetaStart for DEPTH: " + depth + " bestmoveValue " + confirmedBestValue + " sequence:" + Tools.byteListToMoveSequence(bestMoveSequence));


                if (System.currentTimeMillis() > endTime) {
                    if (log) System.out.println("time limit reached in going through depths");
                    break; // Time limit reached
                }


            }
            miscCounter = ((lastIndexReached + 1) * 100) / moves.length;

            if (log) System.out.println("Best Move Sequence: " + Tools.byteListToMoveSequence(bestMoveSequence));
            if (log) System.out.println("Current best valuee: " + confirmedBestValue);
            if (log) System.out.println("Depth Reached: " + bestDepthReached + " and last index was " + lastIndexReached + "/" + moves.length);
            if (log) System.out.println("AlphaBeta called: " + counter + " End Evaluated: " + endReachedCounter + " Cuts: " + cutoffCounter + " Depth Reached: " + bestDepthReached + " and last index was " + lastIndexReached + "/" + moves.length + " misc: depth" + bestDepthReached + ": " + miscCounter + "% TranspoCounter: " + transpoCounter);
            if (log) System.out.println("Time Elapsed: " + (System.currentTimeMillis() - (endTime - timeLimitMillis)) + " ms Ruhesuche took:" + ruhesucheTime);

            if (dynamicTimeManagement) {
                adjustTimeLimit(moves.length, board);
                System.out.println("New Limit Time Limit: " + TIME_LIMIT);
            }
            ruhesucheTime = 0;
            counter = 0;
            endReachedCounter = 0;
            transpoCounter = 0;

            //TODO: removed the next line, why would we clear the table?
            //if(useTranspositionTable)transpositionTable.clear();

            if (bestMoveSequence.isEmpty()) {
                Tools.printDivider();
                Tools.printRed("Error, move sequence empty, probably due to too little time e.g. 50ms? returning default value:" + Tools.parseMoveToString(moves[0]));
                allReturnedMovesForRemisDetection.add(moves[0]);
                return List.of(moves[0]);
            }

            allReturnedMovesForRemisDetection.add(bestMoveSequence.get(0));
            return bestMoveSequence;

        } catch (Exception E) {
            //IF any error occurs, return first move just in case. also print error
            Tools.printDivider();
            E.printStackTrace();
            board.print();
            allReturnedMovesForRemisDetection.add(moves[0]);
            return List.of(moves[0]);
        }
    }


    private static int evaluateGamePhase(BitBoard board) {
        // Heuristik zur Bestimmung der Spielphase (1: Eröffnung, 2: Mittelspiel, 3: Endspiel)
        int totalPieces = board.countTotalPieces();
        if (totalPieces > 20) {
            return 1;
        } else if (totalPieces > 10) {
            return 2;
        } else {
            return 3;
        }
    }

    private static int estimateRemainingMoves(BitBoard board) {
        //Heuristik zur Schätzung der verbleibenden Züge bis zum Spielende
        int totalPieces = board.countTotalPieces();
        if (totalPieces > 20) {
            return 40; // Schätzung für Eröffnung
        } else if (totalPieces > 10) {
            return 20; // Schätzung für Mittelspiel
        } else {
            return 10; // Schätzung für Endspiel
        }
    }

    private static int TIME_LIMIT = 2000; // 2 sec
    private static final int MAX_TIME_LIMIT = 4000;
    private static final int remainingTime = 30000;

    private static void adjustTimeLimit(int numMoves, BitBoard board) {
        //TODO you are multiplying a int by doubles and saving in int, is this working properly? Numbers are being cut off, test if saving in double and then e.g rounding is better
        int timeLimit = TIME_LIMIT;

        // Spielphasenanalyse: Anpassung des Zeitlimits basierend auf der Spielphase
        int gamePhase = evaluateGamePhase(board);
        switch (gamePhase) {
            case 1: // Eröffnung
                timeLimit *= 1.5; // Erhöhtes Zeitlimit in der Eröffnung
                break;
            case 2: // Mittelspiel
                timeLimit *= 1.2; // Mittel hohes Zeitlimit im Mittelspiel
                break;
            case 3: // Endspiel
                timeLimit *= 1.3; // Erhöhtes Zeitlimit im Endspiel
                break;
        }

        if (numMoves < 10) timeLimit *= 0.9;
        else if (numMoves > 30) timeLimit *= 1.1;


        // Verbleibende Zeit analysieren und anpassen
        int estimatedMovesLeft = estimateRemainingMoves(board);
        int timePerMove = remainingTime / estimatedMovesLeft;
        timeLimit = Math.min(timeLimit, timePerMove);

        TIME_LIMIT = Math.min(timeLimit, MAX_TIME_LIMIT);
    }


}

