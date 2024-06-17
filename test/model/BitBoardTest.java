package model;

import ai.BitBoardManipulation;
import ai.Evaluate;
import misc.deprecated.JumpSturdyBoard;
import misc.Tools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BitBoardTest {

    public final String DEFAULT_BOARD = "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0";
    JumpSturdyBoard jumpSturdyBoard = new JumpSturdyBoard();
    BitBoard board = new BitBoard();

    //Helper methods
    void setupBoardsWithFen(String fen) {
        //jumpSturdyBoard=new JumpSturdyBoard(fen);
        board = new BitBoard(fen);
        //board.readBoard(jumpSturdyBoard.board);
    }

    void displayBoard() {
        System.out.println(board);
    }

    void doMoves(boolean displayEachBoard, boolean isRedsTurn, String... moves) {
        for (String move : moves) {
            isRedsTurn = board.doMove(Tools.cleanMove(move), isRedsTurn, true);
            if (displayEachBoard) {
                Tools.printInColor("Executed move: " + move, !isRedsTurn);
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
        int[] expected = new int[]{1, 5, 2, 5};
        assertArrayEquals(JumpSturdyBoard.coordinatesFromMove(position), expected);
        position = "B1-G8";
        expected = new int[]{7, 1, 0, 6};
        assertArrayEquals(JumpSturdyBoard.coordinatesFromMove(position), expected);
    }

    @Test
    void fromFenTest() {
        String[] fens = new String[]{"b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0", "2bb3/5b02/1bb1bb2b0b0/2br3r01/2b0r04/5r0rr1/2rr2r02/3r02", "b05/8/2bb5/8/8/8/8/r05"};
        String fen = fens[1];
        BitBoard b = new BitBoard(fen);
        System.out.println(b);
        Tools.displayBitboard(b.redSingles);
        System.out.println(b.toFEN());

        assertEquals(fen, b.toFEN());
        setupBoardsWithFen(fen);
        displayBoard();

    }
    @Test
    void doMoveOnLongArrayTest(){
        String[] fens = new String[]{
                "6/8/8/2r06/8/3br4/8/6",
                "6/8/8/2b06/8/3br4/8/6",
                "6/8/3rb4/8/2r06/8/8/6",
                "6/8/8/2r06/8/3br4/8/6",

        };
        BitBoard[] boards = new BitBoard[fens.length];

        for (int i = 0; i < fens.length; i++) {
            String fen = fens[i];
            boards[i] = (new BitBoard(fen));
        }

        for (int i = 0; i < fens.length; i++) {
            boards[i].printCommented("FEN:"+fens[i]);
        }
    }

    @Test
    void ruhesucheErrorTest(){
        String fen = "1bb3b0/4r03/1b01b02b0b0/4rr1b01/8/8/2r03r01/r01r0r0r0r0";
        BitBoard bitBoard = new BitBoard(fen);
        bitBoard.printCommented("Error causing bitboard");
        long[] b = new long[]{bitBoard.redSingles, bitBoard.blueSingles, bitBoard.redDoubles, bitBoard.blueDoubles, bitBoard.red_on_blue, bitBoard.blue_on_red};

        //Tools.displayBitboard(68719476736L);

        long[] modified = BitBoardManipulation.doMoveAndReturnModifiedBitBoards(Tools.positionToIndex("D3"),Tools.positionToIndex("E4"),false,b[0],b[1],b[2],b[3],b[4],b[5]);
        BitBoard modifiedBitboard = BitBoard.fromLongArray(modified);
        modifiedBitboard.detectOverlap();
        modifiedBitboard.printCommented("Modified");

        Tools.printDivider();

        bitBoard.doMove("D3-E4",false,true);
        bitBoard.printCommented("NormalBitboard after move");
        modifiedBitboard.printCommented("After move done");

    }

    @Test
    void evaluateMoveTest() {
        displayBoard();

        int startEval = Evaluate.evaluateSimple(board.redsTurn, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
        String moveText = "B7-B6"; //CHANGE AS WANTED, CLEANED TOO
        byte[] move = Tools.parseMove(Tools.cleanMove(moveText));
        System.out.println("Move:" + moveText + " -> " + move[0] + " to " + move[1]);
        int afterEval = Evaluate.evaluateMove(board.redsTurn, move[0], move[1], board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);

        System.out.println("Start " + startEval + " After move " + afterEval);
    }

    @Test
    void evaluateMultipleMoveTest() {
        displayBoard();

        int startEval = Evaluate.evaluateSimple(board.redsTurn, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
        String[] moveText = new String[]{"B7-B6", "B6-B5"}; //CHANGE AS WANTED, CLEANED TOO
        byte[][] move = Arrays.stream(moveText).map(text -> Tools.parseMove(Tools.cleanMove(text))).toArray(byte[][]::new);
        for (byte[] moveSingle : move) {
            System.out.println("Move:" + Arrays.toString(moveText) + " -> " + moveSingle[0] + " to " + moveSingle[1]);

        }

        System.out.println(Arrays.deepToString(move));

        int afterEval = Evaluate.evaluateMultipleMoves(board.redsTurn, move[0], move[1], board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);


        //int afterEval = Evaluate.evaluateMove(board.redsTurn,move[0],move[1], board.redSingles,board.blueSingles,board.redDoubles,board.blueDoubles,board.red_on_blue,board.blue_on_red);

        System.out.println("Start multiple: " + startEval + " After moves " + afterEval);
    }

    @Test
    void simpleMoveTest() {
        displayBoard();
        doMoves(true, true, "B7-B6", "B2-B3", "B6-B5", "B3-B4", "B5-C5", "B4-B5", "C5-C4", "B5-B6", "C4-C3", "B6-C7");//,"C3-B2","G2-G3","B2-C1"
        //board.displayBitboard(board.getPossibleMovesSingles(board.redSingles,true));
        System.out.println(board.getAllPossibleMoveStringsDeprecated(true));
        //System.out.println("\u001B[32m" + "This is green text" + "\u001B[0m");
        assertEquals(board.checkWinCondition(), BitBoard.WINNER_ONGOING);
        assertThrows(IllegalMoveException.class, () -> { //Has to cause an exception, illegal move
            doMoves(true, true, "C3-B2");
        });
        doMoves(true, true, "C3-D2", "G2-G3", "D2-C1");

        assertEquals(board.checkWinCondition(), BitBoard.WINNER_RED);
    }


    @Test
    void doubleMoveTest() {
        setupBoardsWithFen("b0b0b0b0b0b0/1b0b0b0b0b0b01/rb7/7br/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0");

        displayBoard();
        //doMoves();
        doMoves(true, true, "H4-G2");

        assertThrows(IllegalMoveException.class, () -> { //Has to cause an exception, illegal move
            doMoves(true, false, "A3-C2");
        });
        System.out.println(board.getAllPossibleMoveStringsDeprecated(false));
        doMoves(true, false, "A3-C4");
        //TODO: FIX KNIGHT MOVE HERE
    }


    @Test
    void doubleMoveTestTwo() {
        setupBoardsWithFen("6/8/rb7/7br/8/8/8/6");
        displayBoard();
        //doMoves();
        //doMoves(true,true,"H4-G2");

        /*assertThrows(IllegalMoveException.class, () -> { //Has to cause an exception, illegal move
            doMoves(true,false,"A3-C2");
        });*/
        System.out.println(board.getAllPossibleMoveStringsDeprecated(true));
        //doMoves(true,false,"A3-C4");
        //TODO: FIX KNIGHT MOVE HERE
    }

    @Test
    void doubleMoveTestThree() {
        setupBoardsWithFen("6/8/4rb3/3br4/8/8/8/6");
        displayBoard();

        System.out.println(board.getAllPossibleMoveStringsDeprecated(true));
        //doMoves(true,false,"A3-C4");
        //TODO: FIX KNIGHT MOVE HERE
    }

    @Test
    void doubleMoveTestEdges() {
        setupBoardsWithFen("br5/8/br7/rb7/7rb/7br/8/rb4rb");
        displayBoard();
        //doMoves();
        //doMoves(true,true,"H4-G2");

        /*assertThrows(IllegalMoveException.class, () -> { //Has to cause an exception, illegal move
            doMoves(true,false,"A3-C2");
        });*/
        List<String> redMoves = board.getAllPossibleMoveStringsDeprecated(true);
        assertArrayEquals(redMoves.toArray(), new String[]{"A3-C2", "H6-F5", "H6-G4"});
        System.out.println(redMoves);

        List<String> blueMoves = board.getAllPossibleMoveStringsDeprecated(false);
        assertArrayEquals(blueMoves.toArray(), new String[]{"A4-B6", "A4-C5", "H5-F6", "H5-G7"});
        System.out.println(blueMoves);
        doMoves(true, false, "H5-G7");

    }

}
