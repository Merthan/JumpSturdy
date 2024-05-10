package model;

import java.util.List;
import java.util.Scanner;

public class Game {
    private BitBoard board;
    private RandomMoves moveGenerator;
    private JumpSturdyBoard beispiel ;
    private boolean isRedTurn;

    public Game() {
        board = new BitBoard();
        moveGenerator = new RandomMoves();
        beispiel = new JumpSturdyBoard() ;
        isRedTurn = true;
    }

    public void play(BitBoard board) {


        Scanner scanner = new Scanner(System.in);
        char winner = 'f';

        while (winner == 'f') {

            System.out.println("Possible moves for you");
            System.out.println(board.getAllPossibleMoves(isRedTurn));
            System.out.println();

            System.out.println(board); // Display the current board
            System.out.println("Enter your move");
            String playerMove = scanner.nextLine();

            if (isValidMove(board,playerMove)) {


                // My Turn
                makeMove(board,playerMove);
                System.out.println("My move:");
                System.out.println(board);


                winner = board.checkWinCondition(board.redSingles|board.redDoubles|board.red_on_blue, board.blueSingles|board.blueDoubles|board.blue_on_red); // check if it's a winning move

                if (winner != 'f') {
                    System.out.println("Game over "+(winner=='r'?"Red":"Blue") + " wins");
                    break;
                }


                // Bot Turn

                String botMove = moveGenerator.generateRandomMove(board.getAllPossibleMoves(false));
                makeMove(board,botMove);
                System.out.println("Bot's move:");
                System.out.println(board);

                // Check if there's a winner after the bot's move
                winner = board.checkWinCondition(board.redSingles, board.blueSingles);
            } else {
                System.out.println("Invalid move. Please enter a valid move.");
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

    private void makeMove(BitBoard board ,String move) {
        long[] indices = BitBoard.parseMove(move);
        if (isRedTurn) {
            if (board.redDoubles != 0 && (board.redDoubles & (1L << indices[0])) != 0) {
                board.moveDoublePiece(indices[0], indices[1], true);
            } else {
                board.moveSinglePiece(indices[0], indices[1], true);
            }
        } else {
            if (board.blueDoubles != 0 && (board.blueDoubles & (1L << indices[0])) != 0) {
                board.moveDoublePiece(indices[0], indices[1], false);
            } else {
                board.moveSinglePiece(indices[0], indices[1], false);
            }
        }
        isRedTurn = !isRedTurn; // Switch turns
    }

    public static void main(String[] args) {
        String[] fens = new String[]{"b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0","2bb3/5b02/1bb1bb2b0b0/2br3r01/2b0r04/5r0rr1/2rr2r02/3r02", "b05/6r01/2bb5/8/8/8/8/r05"};

        String fen = fens[0];

        JumpSturdyBoard temp = new JumpSturdyBoard(fen);
        BitBoard board = new BitBoard();

        board.readBoard(temp.board);
        //System.out.println(board.getAllPossibleMoves(false));
        Game game = new Game();
        game.play(board);
    }
}

