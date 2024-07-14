package model;

import ai.Game;
import ai.MerthanAlphaBetaExperiment;
import ai.transpotest.FastTranspo;
import misc.deprecated.TemporaryTranspositionDisabledAlphaBeta;
import ai.transpotest.Zobrist;
import misc.Tools;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TranspositionTableTest {


    @Test
    void complexTest(){

        MerthanAlphaBetaExperiment alpha = new MerthanAlphaBetaExperiment();
        Zobrist zobrist = alpha.zobrist;
        FastTranspo transpo = alpha.fastTranspo;
        BitBoard board = new BitBoard(Game.DEFAULT_BOARD);
        zobrist.initializeCorrectBoardKey(board);
        MerthanAlphaBetaExperiment.DELETE_UPPER_DEPTH_LIMIT = 8;
        int timeLimitMillis = 1400;
        List<byte[]> list = alpha.findBestMove(board,true, timeLimitMillis);
        List<byte[]> list2 = new TemporaryTranspositionDisabledAlphaBeta().findBestMove(board,true, timeLimitMillis);
        // 1800ms, upper depth 8
        // Best Move Sequence: G7-G6,G1-F1,E7-D7,F1-G3,D7-C5,D1-D2,C5-D5
        // Best Move Sequence: G7-G6,G1-F1,G6-G5,F1-G3,G5-H5,G2-G3 (no transpo)
        String s =Tools.byteListToMoveSequence(list);
        String s2 =Tools.byteListToMoveSequence(list2);

        System.out.println(s+"\n---\n"+s2);
        System.out.println(s+"$ size:"+transpo.transpositionTable.size());

        //
        // System.out.println(alpha.fastTranspo.transpositionTableToString(true));
    }

    @Test
    void specificBoardTest(){
        BitBoard b = new BitBoard("b0b0b0b0b0b0/1b0b01b0b0b01/3b04/8/8/3r0r03/1r0r02r0r01/r0r0r0r0r0r0");
        Zobrist zobrist = new Zobrist();
        b.printCommented("r:"+b.redSingles+" b:"+b.blueSingles);
        zobrist.initializeCorrectBoardKey(b);
        long current = zobrist.currentBoardKey;
        System.out.println(current);
    }

    @Test
    void packingTest(){
        MerthanAlphaBetaExperiment alpha = new MerthanAlphaBetaExperiment();
        Zobrist zobrist = alpha.zobrist;
        FastTranspo transpo = alpha.fastTranspo;

        long packed =transpo.packEntry(3,(byte)4,(byte)5,(byte)6,(byte)2);

        System.out.println(FastTranspo.entryToString(packed));

    }

    @Test
    void preserveMovesTest(){
        BitBoard board = new BitBoard(Game.DEFAULT_BOARD);
        board.doMove("C7-C6",true,true);
        board.doMove("C2-C3",false,true);

        System.out.println(board.previousMoves());
    }

    @Test
    void zobristKeyTest(){
        MerthanAlphaBetaExperiment alpha = new MerthanAlphaBetaExperiment();
        Zobrist zobrist = alpha.zobrist;
        BitBoard board = new BitBoard(Game.DEFAULT_BOARD);
        BitBoard board2 = new BitBoard(Game.DEFAULT_BOARD);
        //alpha.zobrist.initializeCorrectBoardKey(board);
        //System.out.println(alpha.zobrist.calculateInitialZobristKeyBeforeBoard());
        zobrist.initializeCorrectBoardKey(board);
        System.out.println(zobrist.currentBoardKey + " "+Long.toHexString(zobrist.currentBoardKey));
        board.print();
        byte[] move1 = Tools.parseMove("C7-C6");
        byte[] move2 = Tools.parseMove("C2-C3");
        long test1= applyMoveTest("C7-C6", zobrist.currentBoardKey, board);
        board.doMove("C7-C6",true,true);
        long test2= applyMoveTest("C2-C3", test1, board);
        //zobrist.applyMove(zobrist.currentBoardKey,)
        board.doMove("C2-C3",false,true);
        board.print();

        System.out.println(Long.toHexString(test1)+"  "+Long.toHexString(test2));

        long test3= applyMoveTest("C2-C3", zobrist.currentBoardKey, board2);
        board2.doMove("C2-C3",false,true);
        long test4= applyMoveTest("C7-C6", test3, board2);
        //zobrist.applyMove(zobrist.currentBoardKey,)
        board2.doMove("C7-C6",true,true);

        System.out.println(Long.toHexString(test3)+"  "+Long.toHexString(test4));

        assertEquals(test4,test2);//If the same moves are done in a different order, they should still result in the same long/zobrist

    }

    @Test
    void zobristKeyTestMoveBackForth(){
        MerthanAlphaBetaExperiment alpha = new MerthanAlphaBetaExperiment();
        Zobrist zobrist = alpha.zobrist;
        BitBoard board = new BitBoard(Game.DEFAULT_BOARD);
        BitBoard board2 = new BitBoard(Game.DEFAULT_BOARD);
        //alpha.zobrist.initializeCorrectBoardKey(board);
        //System.out.println(alpha.zobrist.calculateInitialZobristKeyBeforeBoard());
        zobrist.initializeCorrectBoardKey(board);
        System.out.println(zobrist.currentBoardKey + " "+Long.toHexString(zobrist.currentBoardKey));
        board.print();
        byte[] move1 = Tools.parseMove("C7-C6");
        byte[] move2 = Tools.parseMove("C2-C3");
        long test1= applyMoveTest("C7-C6", zobrist.currentBoardKey, board);
        board.doMove("C7-C6",true,true);
        long test2= applyMoveTest("C2-C3", test1, board);
        //zobrist.applyMove(zobrist.currentBoardKey,)
        board.doMove("C2-C3",false,true);
        board.print();

        System.out.println(Long.toHexString(test1)+"  "+Long.toHexString(test2));

        long test3= applyMoveTest("C2-C3", zobrist.currentBoardKey, board2);
        board2.doMove("C2-C3",false,true);
        long test4= applyMoveTest("C7-C6",test3, board2);
        //zobrist.applyMove(zobrist.currentBoardKey,)
        board2.doMove("C7-C6",true,true);

        long test5 = applyMoveTest("C3-D3",test4,board2);
        board2.doMove("C3-D3",false,false);
        board2.print();

        long test6 = applyMoveTest("D3-C3",test5,board2);
        board2.doMove("D3-C3",false,false);
        board2.print();

        System.out.println(Long.toHexString(test3)+"  "+Long.toHexString(test4));
        System.out.println(Long.toHexString(test5)+"  "+Long.toHexString(test6));
        assertEquals(test4,test2);//If the same moves are done in a different order, they should still result in the same long/zobrist

    }

    long applyMoveTest(String move,long zobristCurrentKey,BitBoard board){
        byte[] moveArray = Tools.parseMove(move);
        return Zobrist.applyMove(zobristCurrentKey,moveArray[0],moveArray[1], board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
    }

}
