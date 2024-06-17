package ai;

import model.BitBoard;
import misc.Tools;
import model.BoardException;

import java.util.*;

import static ai.BitBoardManipulation.*;


public class Game {
    private BitBoard board;

    private boolean isRedTurn;

    final String PLAYER_ONE = "[\uD83D\uDC68\u200D\uD83D\uDCBBPlayer 1]";
    final String PLAYER_TWO = "[\uD83D\uDC7DPlayer 2]";

    public Game() {
        board = new BitBoard();
        isRedTurn = true;
    }

    public Game(String fen) {
        isRedTurn = true;
        if (fen.contains(" ")) {
            isRedTurn = fen.charAt(fen.length() - 1) == 'r';//Else its blue
            fen = fen.substring(0, fen.length() - 2);//Remove rest
        }

        board = new BitBoard(fen);
    }

    public void playerVsPlayer(BitBoard board, boolean alwaysRed) {
        Scanner scanner = new Scanner(System.in);
        byte winner = BitBoard.WINNER_ONGOING;
        System.out.println(board); // Display the current board
        //int result = Evaluate.evaluateMove(isRedTurn, (byte)9, (byte)17, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
        //System.out.println("Result after move applied:" + result);
        Tools.printInColor("Result currently:" + (Evaluate.evaluateSimple(isRedTurn, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red) - Evaluate.evaluateSimple(!isRedTurn, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red)), isRedTurn);
        while (winner == BitBoard.WINNER_ONGOING) {
            //System.out.println("ParseMove:"+Arrays.toString(Tools.parseMove("B7-B6")));
            System.out.println("Possible moves for you"); //Sorted now
            List<String> possibleMoves = board.getAllPossibleMoveStringsDeprecated(isRedTurn);
            Tools.printInColor(possibleMoves.toString(), Tools.PURPLE);
            System.out.println();
            String player = isRedTurn ? PLAYER_ONE : PLAYER_TWO;

            long start = System.nanoTime();
            int result = BitBoardManipulation.ruhesuche(board, isRedTurn);
            System.out.println("Ruhesuche took nanos " + (System.nanoTime() - start) + " and result eval is:" + (result == RUHESUCHE_NOT_PERFORMED ? " NONE " : "" + result));

            //EXAMPLE FOR INCLUDING MOVES; NOT MUCH SLOWER
            long startNew = System.nanoTime();
            int[] resultNew = BitBoardManipulation.ruhesucheWithPositions(board, isRedTurn);
            long endNew = System.nanoTime()-startNew;
            if(resultNew!=null){
                for (int i = 0,j=1; i < resultNew.length;j++) {// Array has [0]-[1] [2]-[3] representing moves etc. Last pos eval
                    Tools.printInColor("Ruhesuche move: "+j+" "+Tools.indexToStringPosition((byte)resultNew[i++])+"-"+Tools.indexToStringPosition((byte)resultNew[i++]),Tools.CYAN);
                    if(resultNew[i]==0)break; // End reached, no indexes
                }
            }
            System.out.println("RuhesucheWithPos took nanos " + (endNew) + " and result eval is:" + (resultNew == null ? " NONE " : "" + resultNew[resultNew.length-1]));



            Tools.printInColor("Enter your move ⬇\uFE0F \t\t\tOr Modify: (add f5 rr) (remove f4)" + player, "\u001B[5m");
            String playerMove = Tools.moveMagician(scanner.nextLine(), possibleMoves);
            while(playerMove.startsWith("REMOVE")||playerMove.startsWith("ADD")){//Cheat for removing position eg "remove e3"
                if(playerMove.startsWith("REMOVE")){
                    board.removePositionDebug(Tools.positionToIndex(playerMove.split(" ")[1].toUpperCase()));
                }else{
                    board.addPositionDebug(Tools.positionToIndex(playerMove.split(" ")[1].toUpperCase()),playerMove.split(" ")[2]);
                }
                possibleMoves = board.getAllPossibleMoveStringsDeprecated(isRedTurn);
                board.printCommented("Modified at pos: "+Tools.positionToIndex(playerMove.split(" ")[1]));
                playerMove = Tools.moveMagician(scanner.nextLine(), possibleMoves);


            }

            if (isValidMove(board, playerMove)) {
                // My Turn
                isRedTurn = board.doMove(playerMove, isRedTurn, true);//Do and switch turn
                //System.out.println("ParseMove:" + Arrays.toString(Tools.parseMove(playerMove)));
                //BitBoardManipulation.doMoveAndReturnModifiedBitBoards((byte)9,(byte)17,isRedTurn, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,board.red_on_blue,board.blue_on_red);

                testPreWinMoveDetection(board,!isRedTurn);

/*                long startWin = System.nanoTime();
                boolean canWin = BitBoardManipulation.doesNextMoveWin(!isRedTurn, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
                Tools.printInColor("CanWin took nanos: " +(System.nanoTime()-startWin)+ " canWin> "+canWin,Tools.CYAN);

                long startAdvancedWin = System.nanoTime();
                byte[] pos = BitBoardManipulation.canWinInTwoMovesPreparingJumperForEnd(!isRedTurn, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
                long advancedWinDuration = (System.nanoTime()-startAdvancedWin);
                String additional="";
                if(pos != null){
                    additional = " "+Tools.indexToStringPosition(pos[0])+"-"+Tools.indexToStringPosition(pos[1])+"|"+Tools.indexToStringPosition(pos[1])+"-"+Tools.indexToStringPosition(pos[2]);
                }
                Tools.printInColor("CanWinTwoMoves took nanos: " + advancedWinDuration + " canWin> "+ ((pos==null)? "no":(Arrays.toString(pos)+additional)),Tools.CYAN);*/

/*                for (byte i = 57; i <= 62; i++) {
                    BitBoardManipulation.possibleFromPositionForToIndex(i,!isRedTurn, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red)
                }*/
                if (alwaysRed) isRedTurn = true;
                Tools.printInColor("\t\t" + player + " Move:", !isRedTurn);
                System.out.println(board);

                winner = board.checkWinCondition(); // check if it's a winning move

                if (winner != BitBoard.WINNER_ONGOING) {
                    System.out.println("\u001B[41m\uD83C\uDFC5Game over " + (winner == BitBoard.WINNER_RED ? "Red" : "Blue") + " wins" + "\u001B[0m");
                    //System.out.println("\u001B[41mRed background\u001B[0m"); red background
                    break;
                }
                System.out.println("New Game FEN: " + board.toFEN());
                System.out.println("Game evaluated red:" + Evaluate.evaluateSimple(true, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red));
                System.out.println("Game evaluated blue:" + Evaluate.evaluateSimple(false, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red));
                // Check if there's a winner after the bot's move
                winner = board.checkWinCondition();
            } else {
                Tools.printInColor("Invalid move. Please enter a valid move.\uD83E\uDD22", true);
            }
        }
    }

    private void testPreWinMoveDetection(BitBoard board,boolean isRedTurn) {
        long startWin = System.nanoTime();
        byte[] winningMoves = BitBoardManipulation.canWinWithMovesFusioned(isRedTurn, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
        long endWin = (System.nanoTime()-startWin);
        String winningMovesParsed="";
        if(winningMoves != null && winningMoves.length == 2){
            winningMovesParsed=Tools.indexToStringPosition(winningMoves[0])+"-"+Tools.indexToStringPosition(winningMoves[1]);
        } else if (winningMoves!=null && winningMoves.length ==3) {
            winningMovesParsed=Tools.indexToStringPosition(winningMoves[0])+"-"+Tools.indexToStringPosition(winningMoves[1])+"|"+Tools.indexToStringPosition(winningMoves[1])+"-"+Tools.indexToStringPosition(winningMoves[2]);
        }
        if(winningMoves!=null){
            Tools.printDivider();
        }
        byte[] simpleWinMove = BitBoardManipulation.canWinSimpleMoveWithoutEnemyTurn(isRedTurn,board.getPossibleMovesForTeam(isRedTurn), board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
        Tools.printInColor("CanWin for "+(isRedTurn?"Red":"Blue") +" took nanos: " +endWin+ " canWin> "+winningMovesParsed+Arrays.toString(winningMoves)+ " canWinSimple :"+(simpleWinMove!=null),Tools.CYAN);
    }

    public void playVsBot(BitBoard board, boolean smartBot) {


        Scanner scanner = new Scanner(System.in);
        byte winner = BitBoard.WINNER_ONGOING;
        System.out.println(board); // Display the current board
        while (winner == BitBoard.WINNER_ONGOING) {

            //printPossibleAndTestPredicted(board);


/*            System.out.println();
            long start = System.nanoTime();
            long result = BitBoardManipulation.calculateAttackedPositionsForBoth(board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red);
            System.out.println("Attack Took nanos" +(System.nanoTime()-start) );

            long attacked=BitBoardManipulation.calculateAttackedPositions(isRedTurn,board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red);
            if(attacked !=0L){
                byte mostForwardIndexOfAttacked = (byte) Long.numberOfTrailingZeros(attacked);
                byte from = BitBoardManipulation.possibleFromPositionForToIndex(mostForwardIndexOfAttacked,isRedTurn,board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red);
                System.out.println("Furthest Attack possible:"+ Tools.indexToStringPosition(from)+"-"+Tools.indexToStringPosition(mostForwardIndexOfAttacked));
            }*/
            //Tools.displayBitboard(result);


            List<String> possibleMovesForMatching = board.getAllPossibleMoveStringsDeprecated(isRedTurn);
            Tools.printInColor(possibleMovesForMatching.toString(), Tools.PURPLE);

            Tools.printInColor("Enter your move ⬇\uFE0F \t\t\t\t\t\tOr Modify, Examples: (add f5 rr) (remove f4)", "\u001B[5m");

            String playerMove = Tools.moveMagician(scanner.nextLine(), possibleMovesForMatching);

            while(playerMove.startsWith("REMOVE")||playerMove.startsWith("ADD")){//Cheat for removing position eg "remove e3"
                if(playerMove.startsWith("REMOVE")){
                    board.removePositionDebug(Tools.positionToIndex(playerMove.split(" ")[1].toUpperCase()));
                }else{
                    board.addPositionDebug(Tools.positionToIndex(playerMove.split(" ")[1].toUpperCase()),playerMove.split(" ")[2]);
                }
                possibleMovesForMatching = board.getAllPossibleMoveStringsDeprecated(isRedTurn);
                board.printCommented("Modified at pos: "+Tools.positionToIndex(playerMove.split(" ")[1]));
                playerMove = Tools.moveMagician(scanner.nextLine(), possibleMovesForMatching);
            }



            if (isValidMove(board, playerMove)) {

                System.out.println("Before Move CanWin:");
                testPreWinMoveDetection(board,isRedTurn);
                // My Turn
                isRedTurn = board.doMove(playerMove, isRedTurn, true);//Do and switch turn
                System.out.println("After Move CanWin:");
                //testPreWinMoveDetection(board,isRedTurn);
                //System.out.println("My move:");
                Tools.printInColor("\t\t\uD83D\uDC68\u200D\uD83D\uDCBBMy move: "+playerMove, isRedTurn);
                System.out.println(board);

                winner = board.checkWinCondition(); // check if it's a winning move

                if (winner != BitBoard.WINNER_ONGOING) {
                    System.out.println("\u001B[41m\uD83C\uDFC5Game over " + (winner == BitBoard.WINNER_RED ? "Red" : "Blue") + " wins" + "\u001B[0m");
                    //System.out.println("\u001B[41mRed background\u001B[0m"); red background
                    break;
                }


                // Bot Turn (Random Move)
                if (!smartBot) {
                    List<String> possibleMoves = board.getAllPossibleMoveStringsDeprecated(false);
                    String botMove = "NONE";
                    if (!possibleMoves.isEmpty()) {
                         botMove= possibleMoves.get(new Random().nextInt(possibleMoves.size()));
                        isRedTurn = board.doMove(botMove, isRedTurn, true);
                    } else {
                        isRedTurn = !isRedTurn;//Change turn, dont move, keep rest the same. TODO: Maybe should immediately cancel game
                    }
                    //System.out.println("Bot's move:");
                    Tools.printInColor("\t\t\uD83E\uDD16Bot's move: "+botMove, false);
                    System.out.println(board);


                    System.out.println("Game evaluated red:" + Evaluate.evaluateSimple(isRedTurn, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red));
                    System.out.println("Game evaluated blue:" + Evaluate.evaluateSimple(!isRedTurn, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red));
                    // Check if there's a winner after the bot's move
                    winner = board.checkWinCondition();
                }
                // Bot's Turn (Smart Move)
                else {

                    List<String> possibleMoves = board.getAllPossibleMoveStringsDeprecated(isRedTurn);
                    String botMove = "";
                    if (!possibleMoves.isEmpty()) {
                        //TODO: Experiment, change back perhaps
                        ArrayList<byte[]> moveSequence = new MerthanAlphaBetaExperiment().findBestMove(board,isRedTurn,2000);
                        Tools.printInColor("MoveSequence: "+Tools.byteListToMoveSequence(moveSequence),Tools.YELLOW);
                        botMove = Tools.parseMoveToString(moveSequence.get(0));//SturdyJumpersAI.findBestMove(SearchType.ALPHABETA, board, false);
                        isRedTurn = board.doMove(botMove, isRedTurn, true);
                    } else {
                        //isRedTurn = !isRedTurn; //Change turn, dont move, keep rest the same. TODO: Maybe should immediately cancel game
                    }
                    Tools.printInColor("\t\t\uD83E\uDD16Bot's move: "+botMove, isRedTurn);
                    System.out.println(board);
                    System.out.println(board.toFEN());
                    testPreWinMoveDetection(board,isRedTurn);

                    System.out.println("Game evaluated red:" + Evaluate.evaluateComplex(true, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red));
                    System.out.println("Game evaluated blue:" + Evaluate.evaluateComplex(false, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red));
                    System.out.println("Total times Ruhesuche executed: "+totalRuheSucheExecuted + "max times looped:"+maxTimesRuheSucheLooped+" total millis:"+totalTimeRuheSucheExecuted+" max time single:"+maxTimeSingleRuheSucheExecuted);
                    totalTimeRuheSucheExecuted=0;//Reset for next
                    // Check if there's a winner after the bot's move
                    winner = board.checkWinCondition();

                    if (winner != BitBoard.WINNER_ONGOING) {
                        System.out.println("\u001B[41m\uD83C\uDFC5Game over " + (winner == BitBoard.WINNER_RED ? "Red" : "Blue") + " wins" + "\u001B[0m");
                        //System.out.println("\u001B[41mRed background\u001B[0m"); red background
                        break;
                    }
                }

            } else {
                Tools.printInColor("Invalid move. Please enter a valid move.\uD83E\uDD22", true);
            }
        }

        // Die Message können wir noch anpassen und in eine eigene klasse implementieren

       /* if (winner == 'd') {
            System.out.println("Draw");
        } else {
            System.out.println("The winner is: " + (winner == 'r' ? "Red" : "Blue"));
        } */
    }
    public void buildBoardFromEmpty(){
        buildBoard(b("6/8/8/8/8/8/8/6")); // Helper, does it with empty bitboard
    }
    /**
     * Different than the other methods, add is optional e.g. "e5" -> r on e5 "e6 bb" -> bb on e6
     * Builds a board for debugging, prints the fen. Useful for eval and alphabeta debug
     * call with e.g. "e5" "e6" "f3 b"
     * */
    public void buildBoard(BitBoard board){
        Scanner scanner = new Scanner(System.in);
        System.out.println(board); // Display the current board
        String playerMove = Tools.moveMagician(scanner.nextLine(),null );

        int counter=1;
        while(true){//Cheat for removing position eg "remove e3"
            if(playerMove.startsWith("REMOVE")){
                board.removePositionDebug(Tools.positionToIndex(playerMove.split(" ")[1].toUpperCase()));
            }else if(playerMove.startsWith("ADD")){
                String figure = (playerMove.split(" ").length > 2)? playerMove.split(" ")[2]: "r"; // If just add f5 written, interpret as add f5 r
                board.addPositionDebug(Tools.positionToIndex(playerMove.split(" ")[1].toUpperCase()),figure);
            }else{
                String figure = (playerMove.split(" ").length > 1)? playerMove.split(" ")[1]: "r"; // If just add f5 written, interpret as add f5 r
                board.addPositionDebug(Tools.positionToIndex(playerMove.split(" ")[0].toUpperCase()),figure);
            }
            board.printCommented("Buildboard:"+board.toFEN());
            if(counter++>2){ //TODO: seems to not do anything when losing in the future is guaranteed? 1 player against two doesnt do anything
                List<byte[]> moveSequence = new MerthanAlphaBetaExperiment().findBestMove(board,false,2000);
                Tools.printInColor("MoveSequence: "+Tools.byteListToMoveSequence(moveSequence),Tools.YELLOW);
            }


            playerMove = Tools.moveMagician(scanner.nextLine(),null );
            //board.printCommented();
            //ALso test after modified instead of move done
            //doTestsWithManipulatedBitBoard(board,isRedTurn,"Modified at pos: "+Tools.positionToIndex(playerMove.split(" ")[1]));

        }
    }

    public void analyzeMoveSequence(BitBoard board,boolean isRedTurn,String... sequence){
        int previousEval = Evaluate.evaluateComplex(true,board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                board.red_on_blue, board.blue_on_red);
        board.printCommented("Original eval:"+previousEval);
        for (String move: sequence) {
            board.doMove(move,isRedTurn,true);
            int newEval = Evaluate.evaluateComplex(true,board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                    board.red_on_blue, board.blue_on_red);
            board.printCommented("Move: "+move+" new eval:"+newEval+" change:"+(newEval-previousEval));
            isRedTurn = !isRedTurn;
        }
    }

    public void manipulateAndTestBoard(BitBoard board,boolean alwaysSamePlayer){
        Scanner scanner = new Scanner(System.in);
        System.out.println(board); // Display the current board

        while(true){
            Tools.printInColor("Enter your move ⬇\uFE0F \t\t\t\t\t\tOr Modify, Examples: (add f5 rr) (remove f4)", "\u001B[5m");

            List<String> possibleMovesForMatching = board.getAllPossibleMoveStringsDeprecated(isRedTurn);
            //Tools.printInColor(possibleMovesForMatching.toString(), Tools.PURPLE);

            String playerMove = Tools.moveMagician(scanner.nextLine(), possibleMovesForMatching);

            while(playerMove.startsWith("REMOVE")||playerMove.startsWith("ADD")){//Cheat for removing position eg "remove e3"
                if(playerMove.startsWith("REMOVE")){
                    board.removePositionDebug(Tools.positionToIndex(playerMove.split(" ")[1].toUpperCase()));
                }else{
                    String figure = (playerMove.split(" ").length > 2)? playerMove.split(" ")[2]: "r"; // If just add f5 written, interpret as add f5 r
                    board.addPositionDebug(Tools.positionToIndex(playerMove.split(" ")[1].toUpperCase()),figure);
                }
                possibleMovesForMatching = board.getAllPossibleMoveStringsDeprecated(isRedTurn);
                //board.printCommented();
                //ALso test after modified instead of move done
                doTestsWithManipulatedBitBoard(board,isRedTurn,"Modified at pos: "+Tools.positionToIndex(playerMove.split(" ")[1]));
                playerMove = Tools.moveMagician(scanner.nextLine(), possibleMovesForMatching);
            }

            //System.out.println("playermove"+playerMove);
            if(isValidMove(board,playerMove)){
                isRedTurn = board.doMove(playerMove,isRedTurn,true);
                doTestsWithManipulatedBitBoard(board,!isRedTurn,playerMove);
                if(alwaysSamePlayer) isRedTurn = !isRedTurn; //Keep from turning blue
            }else{
                Tools.printInColor("Invalid move. Please enter a valid move.\uD83E\uDD22", true);
            }
        }
    }

    private void doTestsWithManipulatedBitBoard(BitBoard board, boolean isRedTurn,String playerMoveOrComment) {
        long start = System.nanoTime();
        int evaluated = Evaluate.evaluateComplex(isRedTurn,board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red);
        long end = System.nanoTime() -start;

        board.printCommented((isRedTurn?"Red: ":"Blue: ")+playerMoveOrComment+"\nEvaluated score:"+evaluated+ " in nanos: "+end);
    }

    private void getAllPossibleMovesAndRateThem(BitBoard board,boolean isRedTurn){


        int originalEval = Evaluate.evaluateComplex(true, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);

        byte[][] moves = board.getAllPossibleMovesByte(isRedTurn);

        board.printCommented("Original evaluation of board: "+originalEval);
        for (int i = 0; i < moves.length ; i++) {
            BitBoard withMove = board.doMoveAndReturnBitboard(moves[i],isRedTurn);
            int evaluated = Evaluate.evaluateComplex(true,withMove.redSingles, withMove.blueSingles, withMove.redDoubles, withMove.blueDoubles,
                    withMove.red_on_blue, withMove.blue_on_red);

            int change = evaluated-originalEval;
            String color = (change<0)?Tools.RED:Tools.BLUE;
            Tools.printInColor("Move "+Tools.parseMoveToString(moves[i])+" evaluation: "+evaluated+" change:"+ change,color);
        }

    }

    private void printPossibleAndTestPredicted(BitBoard board) {
        System.out.println("Possible moves for you"); //Sorted now
        long startTimeOld = System.nanoTime();
        List<String> allPossible = board.getAllPossibleMoveStringsDeprecated(isRedTurn);
        long endTimeOld = System.nanoTime();
        Tools.printInColor(allPossible.toString(), Tools.PURPLE);
        //Tools.printInColor(allPossible.stream().map(s -> s.));
        List<String> allPossiblePredicted = new ArrayList<>();

        int[] arr = allPossible.stream().mapToInt(str -> Tools.parseMove(str)[1]).toArray();

        long totalTime = 0;
        for (int i = 0; i < allPossible.size(); i++) {
            String move = allPossible.get(i);
            //byte[] moves = Tools.parseMove(move);
            long startTime = System.nanoTime();
            byte startPredicted = BitBoardManipulation.possibleFromPositionForToIndex(
                    (byte) arr[i], isRedTurn, board.redSingles, board.blueSingles,
                    board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red
            );
            if(startPredicted == 0) throw new BoardException(board,"invalid from");
            totalTime += (System.nanoTime() - startTime);
            String end = move.substring(3, 5);
            allPossiblePredicted.add(Tools.indexToStringPosition(startPredicted) + "_" + end);
        }
        System.out.println("Predicted: in nanos:" + (totalTime) + " other:" + (endTimeOld - startTimeOld));
        Tools.printInColor(allPossiblePredicted.toString(), Tools.PURPLE);
    }

    public void botGame(BitBoard board) {
        byte winner = BitBoard.WINNER_ONGOING;
        while (true) {
            System.out.println(board); // Display the current board
            Tools.printInColor(board.getAllPossibleMoveStringsDeprecated(isRedTurn).toString(), Tools.PURPLE);
            System.out.println();
            String botMove = SturdyJumpersAI.findBestMove(SearchType.ALPHABETA, board, isRedTurn);
            isRedTurn = board.doMove(botMove, isRedTurn, true); //Do and switch turn
            winner = board.checkWinCondition(); // check if it's a winning move
            if (winner != BitBoard.WINNER_ONGOING) {
                System.out.println("\u001B[41m\uD83C\uDFC5Game over " + (winner == BitBoard.WINNER_RED ? "Red" : "Blue") + " wins" + "\u001B[0m");
                //System.out.println("\u001B[41mRed background\u001B[0m"); red background
                break;
            }
        }
    }

    private boolean isValidMove(BitBoard board, String move) {
        List<String> possibleMoves = board.getAllPossibleMoveStringsDeprecated(isRedTurn);

        return possibleMoves.contains(move);
    }

    //Helper, creates instant board from fen
    public static BitBoard b(String fen){
        return new BitBoard(fen);
    }

    public static void main(String[] args) {
        String[] fens = new String[]{"b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0", "2bb3/5b02/1bb1bb2b0b0/2br3r01/2b0r04/5r0rr1/2rr2r02/3r02", "b05/6r01/2bb5/8/8/8/8/r05", "1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/b0r0r02rr2/4r01rr1/3r01r0",
                "6/8/2b03/8/r07/8/8/6"};

        String[] testsMstTwo = new String[]{/*Early Game*/"b0b0b0b0b0b0/2bbb02bb1/4b03/8/3r04/8/2rr1r0r01r0/r0r0r0r0r0r0" /*Mein Zug: h7g7*/,
                /*Mid Game*/"b0b01bb2/6b01/3bb4/4b0b02/3r04/3r04/r01r05/1r0rrrr2" /*Mein Zug: a7b7*/,
                /*End Game*/"b04b0/8/7r0/1b03b02/1rr5r0/4r0b02/b07/4r01" /*Mein Zug: f8e8*/};

        //String fen = "b0b0b0b0b0b0/2b0b0b0b0b01/8/1b06/8/1r0r05/3r0r0r0r01/r0r0r0r0r0r0";//fens[0];
        //String fen = "b0bb2b0b0/3b0r03/6b01/5b0b01/3r04/8/2r01r0r0r01/r01r0r0r0r0"; canWin test middle
        String fen = "b0b03b0/3b04/1b02r01b0b0/3r02b01/4r03/8/2r03r01/r01r0r0r0r0";//"1bb3b0/4r03/1b01b02b0b0/4rr1b01/8/8/2r03r01/r01r0r0r0r0";
        //"b0b03b0/3b04/1b02r01b0b0/3r02b01/4r03/8/2r03r01/r01r0r0r0r0"; //"b0b03bb/3b0r03/1b04b01/3r02b01/4b03/5r02/2r03r01/r01r0r0r0r0"; //"b0b03bb/3b0r03/1b04b01/3r01b0b01/4r03/8/2r02r0r01/r01r0r0r0r0";
        //fens[0];
        final String DEFAULT_BOARD = "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0";

        String searchTest = "6/1r0b04b0/8/8/8/8/1r0r0b04/6";
        String test = "6/1bb1b02b01/8/2r05/3r01b02/5r0r01/2rr2r02/6";

        BitBoard board = new BitBoard("b01b0bb1b0/1bbb0b0b0b0r01/6r01/8/8/8/1r0r0r0r03/r0r0r0r0r0r0");

        //System.out.println(board.getAllPossibleMoves(false));
        Game game = new Game();
        //game.playVsBot(board,true);
        //game.playAgainst(board, false);
        //game.botGame(board);
        //game.isRedTurn = false;//Blue starts

        board = b("3b01b0/1b02b01b01/1r06/1r01b02b01/6r01/8/2r01r0r02/3r0r0r0");
        //game.getAllPossibleMovesAndRateThem(board,true);
        //if(true)return;
        //canWinWithMovesFusioned(true,board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);




        //game.playVsBot(b("3b01b0/1b02b01b01/1r06/1r01b02b01/6r01/8/2r01r0r02/3r0r0r0"),true);//"3bb1b0/1b02b01b01/1r06/3r02b01/6r01/8/2r01r0r02/3r0r0r0"),true);

        game.playVsBot(b(DEFAULT_BOARD),true);
        //game.buildBoardFromEmpty();
        //game.analyzeMoveSequence(b("6/8/2b0b04/8/2r01r03/8/8/6"),false,"D3-C3, E5-E4, C3-E4, C5-B5, E4-E5, B5-A5, E5-E6, A5-A4, E6-E7, A4-B4, E7-E8".split(", "));
        //game.isRedTurn = true;//Start blue
        //game.manipulateAndTestBoard(b("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0"),false);


        //game.playerVsPlayer(new BitBoard("b0b0b0b0b0b0/2b0b0b01b01/6r01/5b02/1b03r02/8/1r0r0r0r03/r0r0r0r0r0r0"),false);
    }
}


