package misc.deprecated;

public class MiscLogs {


    //String fen = "b0b0b0b0b0b0/2b0b0b0b0b01/8/1b06/8/1r0r05/3r0r0r0r01/r0r0r0r0r0r0";//fens[0];
    //String fen = "b0bb2b0b0/3b0r03/6b01/5b0b01/3r04/8/2r01r0r0r01/r01r0r0r0r0"; canWin test middle
    //"b0b03b0/3b04/1b02r01b0b0/3r02b01/4r03/8/2r03r01/r01r0r0r0r0"; //"b0b03bb/3b0r03/1b04b01/3r02b01/4b03/5r02/2r03r01/r01r0r0r0r0"; //"b0b03bb/3b0r03/1b04b01/3r01b0b01/4r03/8/2r02r0r01/r01r0r0r0r0";
    //fens[0];


    //game.playVsBot(board,true);
    //game.playAgainst(board, false);
    //game.botGame(board);
    //game.isRedTurn = false;//Blue starts

    //game.getAllPossibleMovesAndRateThem(board,true);
    //if(true)return;
    //canWinWithMovesFusioned(true,board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);


    //FIXED ALREADY: STATE: TOO POSITIVE/NEGATIVE AT THE START; DONT KNOW WHY. Changed String to byte and unified maximizingplayer. Perhaps isRed/maximizing combo is wrong (typo, perhaps brute force combi). Maybe reference String/byte[] error too


    //game.playVsBot(b("3b01b0/1b02b01b01/1r06/1r01b02b01/6r01/8/2r01r0r02/3r0r0r0"),true);//"3bb1b0/1b02b01b01/1r06/3r02b01/6r01/8/2r01r0r02/3r0r0r0"),true);
    //game.isRedTurn = false;
    //game.advancedBotGame(b(DEFAULT_BOARD),1000,false);
    //game.buildBoardFromEmpty();
    //game.playVsBot(board,true);
    //game.buildBoardFromEmpty();
    //game.analyzeMoveSequence(b("6/8/2b0b04/8/2r01r03/8/8/6"),false,"D3-C3, E5-E4, C3-E4, C5-B5, E4-E5, B5-A5, E5-E6, A5-A4, E6-E7, A4-B4, E7-E8".split(", "));
    //game.isRedTurn = true;//Start blue
    //game.manipulateAndTestBoard(b("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0"),false);

    //game.getAllPossibleMovesAndRateThem(b("b0b0b0b0b0b0/1r0b0b0b0b0b01/8/8/3b04/3r04/1r0r01r0rr2/1r0r0r0r0r0"),false);

    //game.playerVsPlayer(new BitBoard("b0b0b0b0b0b0/2b0b0b01b01/6r01/5b02/1b03r02/8/1r0r0r0r03/r0r0r0r0r0r0"),false);


    //game.getAllPossibleMovesAndRateThem(b("b0b0b0b0b0b0/1r0b0b0b0b0b01/8/8/3b04/3r04/1r0r01r0rr2/1r0r0r0r0r0"), true);

    //game.isRedTurn = false;
    //BitBoard isolated3 = b("b05/r07/2b05/8/8/8/2r05/6");
    //board = isolated3;
/*        board =b("b0b0b0b0b0b0/1r0b0b0b0b0b01/8/8/3b04/3r04/1r0r01r0rr2/1r0r0r0r0r0");
        System.out.println(board.previousMoves());
        board.doMove("B7-B6",true,true);//C7-C6
        System.out.println(board.previousMoves());
        board.doMove("C7-C6",true,true);//C7-C6
        System.out.println(board.previousMoves());
        //if(true) return;

        Tools.printDivider();
        System.out.println("BLUE:");
        */

/*        game.getAllPossibleMovesAndRateThem(board, true);
        game.getAllPossibleMovesAndRateThem(board,false);
        BitBoard isolated = b("b0b0b0b0b0b0/1r0b0b0b0b0b01/8/8/8/8/2r05/6");
        BitBoard isolated2 = b("b0b0b0b0b0b0/1r0b0b0b0b0b01/8/8/8/8/8/6");
        board =b("b0b0b0b0b0b0/1r0b0b0b0b0b01/8/8/3b04/3r04/1r0r01r0rr2/1r0r0r0r0r0");
        System.out.println(board.eval());
        Tools.printDivider();
        System.out.println("Eval start: "+board.eval());
        game.playVsBot(board, true);*/

    /**almost win board:
     * AlphaBeta method was called: 84576067 and end point reached/Evaluated: 79242920 cutoffs: 4262165 misc0
     * when playing E7-E6
     * AFTER Move ordering
     * AlphaBeta method was called: 84576062 and end point reached/Evaluated: 79242915 cutoffs: 4262165
     *
     * DEFAULT: D6
     * AlphaBeta method was called: 12475053 and end point reached/Evaluated: 11245274 cutoffs: 1042258 misc0
     * AlphaBeta method was called: 3728600 and end point reached/Evaluated: 3278190 cutoffs: 361651 misc0
     *
     * NOT CALLING Evaluate within (sorting all by fixed 2 value)
     * AlphaBeta method was called: 12371999 and end point reached/Evaluated: 11170598 cutoffs: 1015274 misc0
     * CAlLING EVAL within
     * AlphaBeta method was called: 3722356 and end point reached/Evaluated: 3273071 cutoffs: 360660 misc0
     *
     * New Move sorting: D6
     * AlphaBeta method was called: 3262393 and end point reached/Evaluated: 2919508 cutoffs: 262841 misc0
     *
     * Old
     * AlphaBeta method was called: 3757505 and end point reached/Evaluated: 3303276 cutoffs: 364744 misc0
     *
     *
     * TODO: NEWEST, sorting test: c5 not sorted:
     * AlphaBeta method was called: 8930918 and end point reached/Evaluated: 7658168 cutoffs: 1103311 misc0
     * AlphaBeta method was called: 5227360 and end point reached/Evaluated: 4207683 cutoffs: 902868 misc0 sorted, again:
     * AlphaBeta method was called: 8949236 and end point reached/Evaluated: 7717693 cutoffs: 1968893 misc0
     * without twice:
     * AlphaBeta method was called: 9100963 and end point reached/Evaluated: 7795321 cutoffs: 1132994 misc0
     * AlphaBeta method was called: 12327250 and end point reached/Evaluated: 10649327 cutoffs: 2644621 misc0
     *
     * unsorted 2x c5
     * AlphaBeta method was called: 11341098 and end point reached/Evaluated: 9811984 cutoffs: 2415897 misc0
     * sorted
     * AlphaBeta method was called: 9268867 and end point reached/Evaluated: 7960722 cutoffs: 2148366 misc0
     *
     * first: c5 sorted
     * AlphaBeta method was called: 5580214 and end point reached/Evaluated: 4489894 cutoffs: 966462 misc0
     * changed:
     * AlphaBeta method was called: 5662183 and end point reached/Evaluated: 4547648 cutoffs: 989185 misc0
     *
     *
     * With bitboard move saving:
     * AlphaBeta called: 9031233 End Evaluated: 7913075 Cuts: 963584 Depth Reached: 6 and last index was 15/34 misc:47 d6
     * AlphaBeta called: 13171061 End Evaluated: 12083378 Cuts: 1898237 Depth Reached: 6 and last index was 7/34 misc:23 d5
     *
     * without Bitboard full move saving (arraycopy):
     * AlphaBeta called: 9521233 End Evaluated: 8323700 Cuts: 1035474 Depth Reached: 6 and last index was 17/34 misc: depth6: 52%
     * AlphaBeta called: 14355913 End Evaluated: 13181843 Cuts: 2045453 Depth Reached: 6 and last index was 8/34 misc: depth6: 26%
     *
     * --
     * AlphaBeta called: 8461647 End Evaluated: 7420542 Cuts: 894307 Depth Reached: 6 and last index was 13/34 misc: depth6: 41%
     * AlphaBeta called: 7931292 End Evaluated: 6987053 Cuts: 805674 Depth Reached: 6 and last index was 11/34 misc: depth6: 35%
     *
     * old:
     * AlphaBeta called: 8414508 End Evaluated: 7397172 Cuts: 871292 Depth Reached: 6 and last index was 13/34 misc: depth6: 41%
     *
     * AlphaBeta called: 7725516 End Evaluated: 6805230 Cuts: 784943 Depth Reached: 6 and last index was 11/34 misc: depth6: 35%
     *
     * newd5 : old cutoffs were better than previous commit?
     * AlphaBeta called: 10570592 End Evaluated: 9717529 Cuts: 1509383 Depth Reached: 6 and last index was 3/34 misc: depth6: 11%
     * old d5
     * AlphaBeta called: 11023004 End Evaluated: 10139485 Cuts: 1572455 Depth Reached: 6 and last index was 4/34 misc: depth6: 14%
     *
     *
     * Now testing without objects:
     * with: d6 d5
     * AlphaBeta called: 8686829 End Evaluated: 7614461 Cuts: 921912 Depth Reached: 6 and last index was 14/34 misc: depth6: 44%
     * AlphaBeta called: 12990214 End Evaluated: 11929278 Cuts: 1831337 Depth Reached: 6 and last index was 7/34 misc: depth6: 23%
     * without objects:
     * AlphaBeta called: 9807400 End Evaluated: 8563381 Cuts: 1077828 Depth Reached: 6 and last index was 18/34 misc: depth6: 55%
     * AlphaBeta called: 16222713 End Evaluated: 14940830 Cuts: 2182827 Depth Reached: 6 and last index was 9/34 misc: depth6: 29%
     *
     * Tournament:
     * Red Won:45
     * Blue Won:55
     *
     *
     *
     * 100millis, 3 reps
     * Red Won:1
     * Blue Won:2
     *
     * trans without d6 d5
     * AlphaBeta called: 4801082 End Evaluated: 4271067 Cuts: 426398 Depth Reached: 6 and last index was 17/34 misc: depth6: 52%
     * AlphaBeta called: 6806577 End Evaluated: 6219198 Cuts: 908568 Depth Reached: 6 and last index was 8/34 misc: depth6: 26%
     * with
     * AlphaBeta called: 8436952 End Evaluated: 7408445 Cuts: 882109 Depth Reached: 6 and last index was 13/34 misc: depth6: 41%
     * AlphaBeta called: 12615938 End Evaluated: 11605934 Cuts: 1745586 Depth Reached: 6 and last index was 6/34 misc: depth6: 20%
     *
     *
     * 20 seconds trans off:
     * AlphaBeta called: 98075421 End Evaluated: 86477499 Cuts: 9393488 Depth Reached: 8 and last index was 0/34 misc: depth8: 2%
     * AlphaBeta called: 98352669 End Evaluated: 87774245 Cuts: 17392235 Depth Reached: 7 and last index was 19/34 misc: depth7: 58%
     * trans on:
     * AlphaBeta called: 56391255 End Evaluated: 51287796 Cuts: 3985696 Depth Reached: 8 and last index was 1/34 misc: depth8: 5%
     * AlphaBeta called: 56477664 End Evaluated: 50410551 Cuts: 9035930 Depth Reached: 8 and last index was 0/34 misc: depth8: 2%
     * */

    //game.playVsBot(b(DEFAULT_BOARD), true);
    //game.playVsBot(b("2b0b0b0b0/1b04b01/2b04r0/5b02/3r01rr2/8/b02r01r0r01/1r02r01"),true);
    //game.playVsBot(b("6/3b0b03/8/3r04/8/6b01/1r04r01/6"),true);
    //game.playVsBot("6/3b04/8/3r04/8/6b01/6r01/6");

    //game.botWorldChampionship(b("1b03b0/r0b02bb1b01/3b02r01/8/8/3r0b03/1r06/r0r02r01"),100,10,false);
    //game.botWorldChampionship(b("1b03b0/r0b02bb1b01/3b02r01/8/8/3r0b03/1r06/r0r02r01"),60,100,false,true);

    //BitBoard b= b(DEFAULT_BOARD);
    //b.deleteRandomFigure(false);
    //b.print();
    //game.playVsBot("b0b0b0b0b0b0/2b0b0b0b0b01/8/1b06/4r03/1r0r05/3r01r0r01/r0r0r0r0r0r0");
    //game.playVsBot();




    //random.deleteRandomFigure(new Random().nextBoolean());
    //game.botWorldChampionship(random,200,3,false,true);

    //game.buildBoardFromDefault();

    //game.playVsBot();
    /**
     * 100ms r 2 b 8
     * 50ms r 27 b 23
     * 500ms r 2 b 3
     * 1000ms r 1 b 2
     * 2000ms
     * */

    //game.playVsBot();
    //game.playVsBot();
    //game.botWorldChampionship(b(DEFAULT_BOARD),200,1,true,true);
    //game.manipulateAndTestBoard(b("br4b0/5b01b0/5bb2/3b01r02/6b01/8/1r0r05/r0r0r01r0r0"),false);

    // error: b0b03b0/2r02b01b0/5bb2/3b01r02/6b01/8/1r0r05/r0r0r01r0r0

    //game.playVsBot("b0b03b0/2r02b01b0/5bb2/3b01r02/6b01/8/1r0r05/r0r0r01r0r0");

    /***
     * Mert Transpo test: previous, without code changes:
     * AlphaBeta called: 8546159 End Evaluated: 7491398 Cuts: 906706 Depth Reached: 6 and last index was 14/34 misc: depth6: 44%
     * with code changes, BUT TRANSPO OFF:
     * AlphaBeta called: 8266168 End Evaluated: 7262478 Cuts: 860719 Depth Reached: 6 and last index was 12/34 misc: depth6: 38%
     * with transpo on:
     * AlphaBeta called: 6243803 End Evaluated: 5578152 Cuts: 566320 Depth Reached: 8 and last index was 0/34 misc: depth8: 2%
     *
     * middle of testing, transpo off but alphabeta changed
     * AlphaBeta called: 8573367 End Evaluated: 7516582 Cuts: 907967 Depth Reached: 6 and last index was 14/34 misc: depth6: 44%
     *
     *
     * */
    //game.playVsBot();
    //game.botWorldChampionship(b(DEFAULT_BOARD),200,5,true,true);
    //game.advancedBotGame(b(DEFAULT_BOARD),300,true,false,true);

/*        BitBoard b2 = b("5b0/1b0b0b01r02/6rr1/8/8/8/2r05/r01rr3");
        b2.print();

        BitBoard b3=b("4b01/1b0b0b01r02/6rr1/8/8/8/2r05/r01rr3");
        b3.print();
        System.out.println(b3.getAllPossibleMoveStrings(true));
        List<byte[]> arr =new MerthanAlphaBetaExperiment().findBestMove(b3,true,2000);
        System.out.println(arr);
        Fixed a new bug here, related to possiblefrom

        */
    /**AlphaBetaStart: move: C1-B2 has value:5
     AlphaBetaStart: move: C1-C2 has value:4
     AlphaBetaStart: move: C1-B1 has value:4
     AlphaBetaStart: move: C1-D1 has value:3*/

    //game.analyzeMoveSequence(b(DEFAULT_BOARD),true,"D7-D6, G2-G3, D6-D5, E1-E2, D5-E5, E2-D4, E5-D4".split(", "));
    //game.analyzeMoveSequence(b(DEFAULT_BOARD),true,"D7-D6, G2-G3, D6-D5, E1-E2, D5-D4, E2-D4".split(", "));
    //game.playVsBot();

    //game.botWorldChampionship(b(DEFAULT_BOARD),200,5,true,true);

    //BitBoard.fromFen("b0b01b01b0/1b0b01b03/3b01b02/7b0/3r02b0r0/8/1r0r0r04/r0r0r0r0r0r0").print();

    //BitBoard.fromFen("b0b01b01b0/1b0b01b03/3b01b02/7r0/3r02b01/8/1r0r0r04/r0r0r0r0r0r0").print();

    //game.analyzeMoveSequence(b(DEFAULT_BOARD),true,"G7-G6, G2-G3, F7-E7, G3-G4, G6-G5, F1-G1, E7-D5, G1-F3, E7-E6, F2-F3, E6-F6, D2-D3, G5-H5, G4-H5, F6-G6, F3-G5, G6-H5, H5-H4".split(", "));
    //game.botWorldChampionship(b(DEFAULT_BOARD),200,5,true,true);

    //game.advancedBotGame(random,200,true,true,true);


    /**
     * transpo disabled: 6 11/34
     * enabled: 6 21/34
     * enabled again: 7 1/34
     *
     * WITH LIBRARY-----
     * disabled:6 13/34
     * enabled: 28/34
     * enabled again: 7 2/34
     * */

/*        new TemporaryTranspositionDisabledAlphaBeta().findBestMove(random,true,2000);

        MerthanAlphaBetaExperiment preserved = new MerthanAlphaBetaExperiment(); // With transpo
        preserved.findBestMove(random,true,2000);
        //calling again will lead to lots of transpo table use, resulting in depth 7
        preserved.findBestMove(random,true,2000);*/

    //game.advancedBotGame(random,1000,true,true,true);
    //b("1b04/1b06/2b02bb2/2rr5/1r02r0b02/3r02b01/6r01/1r02r01").print();
    //b("2b03/1b06/2b01r0bb2/2r01r03/1r04b01/3r02b01/6r01/1r02r01").print();
    //b("2b03/1b06/2b01r0br2/2r05/1r06/3r02bb1/6r01/1r02r01").print();

}
