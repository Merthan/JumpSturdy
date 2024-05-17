package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class BitBoardTest {

    public final String DEFAULT_BOARD = "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0";
    JumpSturdyBoard jumpSturdyBoard = new JumpSturdyBoard();
    BitBoard board = new BitBoard();

    //Helper methods
    void setupBoardsWithFen(String fen){
        jumpSturdyBoard=new JumpSturdyBoard(fen);
        board.readBoard(jumpSturdyBoard.board);
    }
    void displayBoard(){
        System.out.println(board);
    }

    void doMoves(boolean displayEachBoard,boolean isRedsTurn,String... moves){
        for (String move : moves) {
            isRedsTurn = board.doMove(Tools.cleanMove(move),isRedsTurn,true);
            if(displayEachBoard){
                Tools.printInColor("Executed move: "+move,!isRedsTurn);
                //System.out.println("Executed move: "+move + " as "+(!isRedsTurn?"red":"blue"));
                displayBoard();
            }
        }
    }


    @BeforeEach
    void setUp() {
        setupBoardsWithFen(DEFAULT_BOARD);
    }

    @Test
    void coordinatesTest() {
        String position = "F7-F6";
        int[] expected = new int[]{ 1,5,  2,5};
        assertArrayEquals(JumpSturdyBoard.coordinatesFromMove(position),expected);
        position = "B1-G8";
        expected = new int[]{ 7,1,  0,6};
        assertArrayEquals(JumpSturdyBoard.coordinatesFromMove(position),expected);
    }

    @Test
    void evaluateMoveTest(){
        displayBoard();

        int startEval =Evaluate.evaluateSimple(board.redsTurn, board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red);
        String moveText = "B7-B6"; //CHANGE AS WANTED, CLEANED TOO
        byte[] move = Tools.parseMove(Tools.cleanMove(moveText));
        System.out.println("Move:"+moveText+" -> "+ move[0] +" to "+move[1]);
        int afterEval = Evaluate.evaluateMove(board.redsTurn,move[0],move[1], board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red);

        System.out.println("Start "+startEval+" After move "+ afterEval);
    }

    @Test
    void evaluateMultipleMoveTest(){
        displayBoard();

        int startEval =Evaluate.evaluateSimple(board.redsTurn, board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red);
        String[] moveText = new String[]{ "B7-B6", "B6-B5"}; //CHANGE AS WANTED, CLEANED TOO
        byte[][] move = Arrays.stream(moveText).map( text -> Tools.parseMove(Tools.cleanMove(text))).toArray(byte[][]::new);
        for (byte[] moveSingle : move) {
            System.out.println("Move:"+ Arrays.toString(moveText) +" -> "+ moveSingle[0] +" to "+moveSingle[1]);

        }

        System.out.println(Arrays.deepToString(move));

        int afterEval = Evaluate.evaluateMultipleMoves(board.redsTurn,move[0],move[1], board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red);


        //int afterEval = Evaluate.evaluateMove(board.redsTurn,move[0],move[1], board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red);

        System.out.println("Start multiple: "+startEval+" After moves "+ afterEval);
    }

    @Test
    void simpleMoveTest(){
        displayBoard();
        doMoves(true,true,"B7-B6","B2-B3","B6-B5","B3-B4","B5-C5","B4-B5","C5-C4","B5-B6","C4-C3","B6-C7");//,"C3-B2","G2-G3","B2-C1"
        //board.displayBitboard(board.getPossibleMovesSingles(board.redSingles,true));
        System.out.println(board.getAllPossibleMoves(true));
        //System.out.println("\u001B[32m" + "This is green text" + "\u001B[0m");
        assertEquals(board.checkWinCondition(),'f');
        assertThrows(IllegalMoveException.class, () -> { //Has to cause an exception, illegal move
            doMoves(true,true,"C3-B2");
        });
        doMoves(true,true,"C3-D2","G2-G3","D2-C1");

        assertEquals(board.checkWinCondition(),'r');
    }


    @Test
    void doubleMoveTest(){
        setupBoardsWithFen("b0b0b0b0b0b0/1b0b0b0b0b0b01/rb7/7br/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0");
        displayBoard();
        //doMoves();
        doMoves(true,true,"H4-G2");

        assertThrows(IllegalMoveException.class, () -> { //Has to cause an exception, illegal move
            doMoves(true,false,"A3-C2");
        });
        System.out.println(board.getAllPossibleMoves(false));
        doMoves(true,false,"A3-C4");
        //TODO: FIX KNIGHT MOVE HERE
    }


}
