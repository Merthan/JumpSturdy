package model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WikiTests extends BitBoardTest{

    static long totalTime = 0;

/*    @Test
    void simpleMoveTest(){
        displayBoard();
        doMoves(true,true,"B7-B6","B2-B3","B6-B5","B3-B4","B5-C5","B4-B5","C5-C4","B5-B6","C4-C3","B6-C7");//,"C3-B2","G2-G3","B2-C1"
        //board.displayBitboard(board.getPossibleMovesSingles(board.redSingles,true));
        System.out.println(wikiBoard.getAllPossibleMoves(true));
        //System.out.println("\u001B[32m" + "This is green text" + "\u001B[0m");
        assertEquals(wikiBoard.checkWinCondition(),'f');
        assertThrows(IllegalMoveException.class, () -> { //Has to cause an exception, illegal move
            doMoves(true,true,"C3-B2");
        });
        doMoves(true,true,"C3-D2","G2-G3","D2-C1");

        assertEquals(wikiBoard.checkWinCondition(),'r');
    }*/

    @Test
    void startWikiTest(){
        setupWikiTest(
                "6/1b06/1r03bb2/2r02b02/8/5r0r01/2r0r04/6 r",
                "B3-A3, B3-C3, C4-B4, C4-D4, C4-C3, F6-E6, F6-G6, F6-F5, G6-F6, G6-H6, G6-G5, C7-B7, C7-D7, C7-C6, D7-C7, D7-E7, D7-D6"
        );
        setupWikiTest(
                "6/1b0b0b0b0b0b01/1b0b0b0b0b0b01/8/8/1r0r0r0r0r0r01/1r0r0r0r0r0r01/6 b",
                "B2-C2, B2-A2, B2-B3, C2-D2, C2-B2, C2-C3, D2-E2, D2-C2, D2-D3, E2-F2, E2-D2, E2-E3, F2-G2, F2-E2, F2-F3, G2-H2, G2-F2, G2-G3, B3-C3, B3-A3, B3-B4, C3-D3, C3-B3, C3-C4, D3-E3, D3-C3, D3-D4, E3-F3, E3-D3, E3-E4, F3-G3, F3-E3, F3-F4, G3-H3, G3-F3, G3-G4"
        );
        setupWikiTest(
                "b0b01b0b0b0/1b0b02b0b01/3b0b03/2b05/3r04/2r05/1r01rr1r0r01/r0r02r0r0 b",
                "B1-B2, B1-C1, C1-B1, C1-C2, C1-D1, E1-D1, E1-E2, E1-F1, F1-E1, F1-F2, F1-G1, G1-F1, G1-G2, B2-A2, B2-B3, B2-C2, C2-B2, C2-C3, C2-D2, D3-C3, D3-D4, D3-E3, E3-D3, E3-E4, E3-F3, F2-E2, F2-F3, F2-G2, G2-F2, G2-G3, G2-H2, C4-B4, C4-C5, C4-D5, C4-D4"
        );
        setupWikiTest(
                "6/2bb1b03/5b02/3b01r02/2b05/8/1rr1r02r01/6 r",
                "B7-A5, B7-C5, B7-D6, D7-C7, D7-D6, D7-E7, G7-F7, G7-G6, G7-H7, F4-E4, F4-G4"
        );
/*        setupWikiTest( //Hier haben die bestimmt nen Fehler
                "b0b0b02bb/1b01b0bb1b01/2b05/5b02/1r06/8/2r0rrr0rr1r0/rr2r01r0 b",
                "B5-A5, B5-B4, B5-C5, B8-B7, B8-C8, C7-B7, C7-C6, C8-B8, C8-C7, C8-D8, D7-B6, D7-C5, D7-E5, D7-F6, E7-E6, E8-D8, E8-E7, E8-F8, F7-D6, F7-E5, F7-G5, F7-H6, G8-F8, G8-G7, H7-G7, H7-H6"
        );*/
        setupWikiTest(
                "b0b0b02bb/1b01b0bb1b01/2b05/5b02/1r06/8/2r0rrr0rr1r0/rr2r01r0 r",
                "B5-A5, B5-B4, B5-C5, B8-A6, B8-C6, C7-B7, C7-C6, D7-B6, D7-C5, D7-E5, D7-F6, E7-E6, E8-D8, E8-E7, E8-F8, F7-D6, F7-E5, F7-G5, F7-H6, G8-F8, G8-G7, H7-G7, H7-H6"
        );
        setupWikiTest(
                "bb4bb/3b02b01/r07/2r02r02/4b03/2b02r02/2r01r01r0r0/1r01r02 r",
                "A3-A2, A3-B3, C4-B4, C4-C3, C4-D4, C7-B7, C7-D7, C8-B8, C8-C7, C8-D8, E7-D7, E7-E6, E7-F7, E8-D8, E8-E7, E8-F8, F4-E4, F4-F3, F4-G4, F6-E5, F6-E6, F6-F5, F6-G6, G7-F7, G7-G6, G7-H7, H7-G7, H7-H6"
        );
        setupWikiTest(
                "6/1bbbbbbbbbbbb1/8/8/8/1r0r0r0r0r0r01/8/r0r0r0r0r0r0 b",
                "B2-A4, B2-C4, B2-D3, C2-B4, C2-D4, C2-A3, C2-E3, D2-C4, D2-E4, D2-B3, D2-F3, E2-D4, E2-F4, E2-C3, E2-G3, F2-E4, F2-G4, F2-D3, F2-H3, G2-F4, G2-H4, G2-E3"
        );
        setupWikiTest(
                "6/2b02b02/2r02r02/8/8/2b02b02/2r02r02/6 b",
                "C6-B6, C6-D6, F6-E6, F6-G6, C2-B2, C2-D2, F2-E2, F2-G2"
        );

        System.out.println("Total time nanos: "+ totalTime + " s:"+ totalTime/1e9);
    }

    void setupWikiTest(String fen, String moves){
        setupBoardsWithFen(fen.trim().substring(0,fen.length()-2));

        boolean redStarts = fen.charAt(fen.length()-1)!='b';
        System.out.println((redStarts?"Red ":"Blue ")+Arrays.toString(moves.replace(" ", "").split(",")));
        displayBoard();
        //doMoves(true,redStarts,);
        //board.getAllPossibleMoves(redStarts)
        List<String> moveList = Arrays.stream(moves.split(",")).map(Tools::cleanMove).sorted().toList();
        //moves.stream().sorted().collect(Collectors.toList())
        long startTime = System.nanoTime();
        List<String> ownMoveList = board.getAllPossibleMoves(redStarts);
        totalTime+= (System.nanoTime()-startTime);
        System.out.println("OTR:"+moveList);
        System.out.println("OWN:"+ownMoveList);
        assertEquals( moveList.size(), ownMoveList.size(),"Lists differ in size");
        for (int i = 0; i < moveList.size(); i++) {
            assertEquals( moveList.get(i), ownMoveList.get(i),"Difference at index " + i);
        }

        System.out.println("-------------------------------------------------------------------");
    }



}
