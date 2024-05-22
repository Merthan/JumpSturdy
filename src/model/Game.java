package model;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


public class Game {
    private BitBoard board;

    private boolean isRedTurn;

    final String PLAYER_ONE = "[\uD83D\uDC68\u200D\uD83D\uDCBBPlayer 1]";
    final String PLAYER_TWO ="[\uD83D\uDC7DPlayer 2]";
    public Game() {
        board = new BitBoard();
        isRedTurn = true;
    }

    public Game(String fen){
        isRedTurn=true;
        if(fen.contains(" ")){
            isRedTurn = fen.charAt(fen.length() - 1) == 'r';//Else its blue
            fen = fen.substring(0,fen.length()-2);//Remove rest
        }

        board = new BitBoard(fen);
    }

    public void playAgainst(BitBoard board,boolean alwaysRed){
        Scanner scanner = new Scanner(System.in);
        char winner = 'f';
        System.out.println(board); // Display the current board
        int result = Evaluate.evaluateMove(isRedTurn,(byte)9,(byte)17,board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red);
        System.out.println("Result after move applied:"+result);
        System.out.println("Result currently:"+ Evaluate.evaluateSimple(isRedTurn,board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red));
        while (winner == 'f') {
            //System.out.println("ParseMove:"+Arrays.toString(Tools.parseMove("B7-B6")));
            System.out.println("Possible moves for you"); //Sorted now
            Tools.printInColor(board.getAllPossibleMoves(isRedTurn).toString(),Tools.PURPLE);
            System.out.println();
            String player = isRedTurn?PLAYER_ONE:PLAYER_TWO;
            Tools.printInColor("Enter your move ⬇\uFE0F "+player,"\u001B[5m");
            String playerMove = Tools.cleanMove(scanner.nextLine());

            if (isValidMove(board,playerMove)) {
                // My Turn
                isRedTurn = board.doMove(playerMove,isRedTurn,true);//Do and switch turn
                System.out.println("ParseMove:"+Arrays.toString(Tools.parseMove(playerMove)));
                //BitBoardManipulation.doMoveAndReturnModifiedBitBoards((byte)9,(byte)17,isRedTurn, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,board.red_on_blue,board.blue_on_red);

                if(alwaysRed) isRedTurn = true;
                Tools.printInColor("\t\t"+player+" Move:",!isRedTurn);
                System.out.println(board);

                winner = board.checkWinCondition(); // check if it's a winning move

                if (winner != 'f') {
                    System.out.println("\u001B[41m\uD83C\uDFC5Game over "+(winner=='r'?"Red":"Blue") + " wins"+"\u001B[0m");
                    //System.out.println("\u001B[41mRed background\u001B[0m"); red background
                    break;
                }
                System.out.println("New Game FEN: "+board.toFEN());
                System.out.println("Game evaluated red:"+Evaluate.evaluateSimple(isRedTurn,board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red));
                System.out.println("Game evaluated blue:"+Evaluate.evaluateSimple(!isRedTurn,board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red));
                // Check if there's a winner after the bot's move
                winner = board.checkWinCondition();
            } else {
                Tools.printInColor("Invalid move. Please enter a valid move.\uD83E\uDD22",true);
            }
        }
    }

    public void play(BitBoard board, boolean smartBot) {


        Scanner scanner = new Scanner(System.in);
        char winner = 'f';
        System.out.println(board); // Display the current board
        while (winner == 'f') {

            System.out.println("Possible moves for you"); //Sorted now
            Tools.printInColor(board.getAllPossibleMoves(isRedTurn).toString(),Tools.PURPLE);
            System.out.println();

            Tools.printInColor("Enter your move ⬇\uFE0F","\u001B[5m");
            String playerMove = Tools.cleanMove(scanner.nextLine());

            if (isValidMove(board,playerMove)) {


                // My Turn
                isRedTurn = board.doMove(playerMove,isRedTurn,true);//Do and switch turn
                //System.out.println("My move:");
                Tools.printInColor("\t\t\uD83D\uDC68\u200D\uD83D\uDCBBMy move:",true);
                System.out.println(board);

                winner = board.checkWinCondition(); // check if it's a winning move

                if (winner != 'f') {
                    System.out.println("\u001B[41m\uD83C\uDFC5Game over "+(winner=='r'?"Red":"Blue") + " wins"+"\u001B[0m");
                    //System.out.println("\u001B[41mRed background\u001B[0m"); red background
                    break;
                }


                // Bot Turn (Random Move)
                if (!smartBot) {
                List<String> possibleMoves = board.getAllPossibleMoves(false);
                if(!possibleMoves.isEmpty()){
                    String botMove = possibleMoves.get(new Random().nextInt(possibleMoves.size()));
                    isRedTurn = board.doMove(botMove,isRedTurn,true);
                }else{
                    isRedTurn = !isRedTurn;//Change turn, dont move, keep rest the same. TODO: Maybe should immediately cancel game
                }
                //System.out.println("Bot's move:");
                Tools.printInColor("\t\t\uD83E\uDD16Bot's move:",false);
                System.out.println(board);

                System.out.println("Game evaluated red:"+Evaluate.evaluateSimple(isRedTurn,board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red));
                System.out.println("Game evaluated blue:"+Evaluate.evaluateSimple(!isRedTurn,board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red));
                // Check if there's a winner after the bot's move
                winner = board.checkWinCondition();
                }
                // Bot Turn (Smart Move)
                else {
                    List<String> possibleMoves = board.getAllPossibleMoves(false);
                    if(!possibleMoves.isEmpty()){
                        String botMove = SturdyJumpersAI.findBestMove(board,false);
                        isRedTurn = board.doMove(botMove,isRedTurn,true);
                    }else{
                        isRedTurn = !isRedTurn;//Change turn, dont move, keep rest the same. TODO: Maybe should immediately cancel game
                    }
                    Tools.printInColor("\t\t\uD83E\uDD16Bot's move:",false);
                    System.out.println(board);

                    System.out.println("Game evaluated red:"+Evaluate.evaluateSimple(isRedTurn,board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red));
                    System.out.println("Game evaluated blue:"+Evaluate.evaluateSimple(!isRedTurn,board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red));
                    // Check if there's a winner after the bot's move
                    winner = board.checkWinCondition();

                    if (winner != 'f') {
                        System.out.println("\u001B[41m\uD83C\uDFC5Game over "+(winner=='r'?"Red":"Blue") + " wins"+"\u001B[0m");
                        //System.out.println("\u001B[41mRed background\u001B[0m"); red background
                        break;
                    }
                }
            } else {
                Tools.printInColor("Invalid move. Please enter a valid move.\uD83E\uDD22",true);
            }
        }

        // Die Message können wir noch anpassen und in eine eigene klasse implementieren

       /* if (winner == 'd') {
            System.out.println("Draw");
        } else {
            System.out.println("The winner is: " + (winner == 'r' ? "Red" : "Blue"));
        } */
    }

    private boolean isValidMove(BitBoard board , String move) {
        List<String> possibleMoves = board.getAllPossibleMoves(isRedTurn);

        return possibleMoves.contains(move);
    }

    public static void main(String[] args) {
        String[] fens = new String[]{"b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0","2bb3/5b02/1bb1bb2b0b0/2br3r01/2b0r04/5r0rr1/2rr2r02/3r02", "b05/6r01/2bb5/8/8/8/8/r05"};

        String fen = fens[1];


        BitBoard board = new BitBoard(fen);

        //System.out.println(board.getAllPossibleMoves(false));
        Game game = new Game();
        game.play(board,true);
        //game.playAgainst(board,true);
    }
}


