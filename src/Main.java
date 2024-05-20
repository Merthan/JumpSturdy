import model.*;
import deprecated.JumpSturdyBoard;

import java.util.List;
import java.util.Random;

public class Main {

    private static Random random = new Random();
    public static void main(String[] args) {
        Game.main(args);//Only call Game Main, then skip rest of this method

        if(true)return;

        String[] fens = new String[]{"b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0","2bb3/5b02/1bb1bb2b0b0/2br3r01/2b0r04/5r0rr1/2rr2r02/3r02", "b05/8/2bb5/8/8/8/8/r05"};

        String fen = fens[0];
        System.out.println(fen);
        JumpSturdyBoard temp = new JumpSturdyBoard(fen);
        BitBoard b = new BitBoard();

        b.readBoard(temp.board);
        System.out.println(b);
        //Benchmark.benchmark(b);


        byte[] moves = Tools.parseMove("B8-B7");
        b.moveSinglePiece(moves[0],moves[1],true);
        System.out.println(b);

        System.out.println("-------------------------------------------");
        System.out.println(SturdyJumpersAI.findBestMove(b,false));
        System.out.println("-------------------------------------------");
        //b.displayBitboard(b.getPossibleMovesSingles(b.redSingles,false));
        //b.displayBitboard(b.getPossibleMovesDoubles(b.redDoubles,true));
        //System.out.println(b.checkWinCondition(b.redSingles, b.blueSingles));
        //System.out.println(b.getMovesForTeam(true));
        //System.out.println(b);
        //b.displayBitboard(b.getMovesForTeam(true));
        //System.out.println(BitBoard.indexToPosition(49));
        //System.out.println(BitBoard.positionToIndex("B2"));
        //System.out.println("DISPLAYall");
        //b.displayBitboard(b.redSingles);
       
        //System.out.println("DISPLAYall");
        //System.out.println(b.getPossibleMovesSinglesString(b.redSingles,true)) ;
    
      /*  System.out.println(b.getAllPossibleMoves(false));
        long[] move = BitBoard.parseMove(b.getAllPossibleMoves(false).get(4));

       

        b.moveDoublePiece(move[0],move[1],false);
        System.out.println(b);
        
        if(true)return;*/

       /* System.out.println(b);

        System.out.println("DISPLAYall");
        System.out.println("DISPLAYall");
        System.out.println("DISPLAYall");
        long[] moves = BitBoard.parseMove("C5-E6");
        b.moveDoublePiece(moves[0],moves[1],false);
        System.out.println(b);*/

        System.out.println("-------------\n\n\n");





        System.out.println("FEN: "+fen);
        JumpSturdyBoard board = new JumpSturdyBoard(fen);
        //JumpSturdyBoard board = new JumpSturdyBoard("r0r0r0r0r0r0/1r0r0r0r0r0r0/8/8/8/8/1b0b0b0b0b0b0/b0b0b0b0b0b0");
        System.out.println(board);
        board.printBoard();



    }

    public static List<String> mergeLists(List<String> a, List<String> b){
        a.addAll(b);
        return a;
    }
/*

    private static void tryRandom(BitBoard b) {
        for (int i = 0; i < 10; i++) {

            String move = getPossibleMoveForRandomSingle(b.redsTurn?b.redSingles:b.blueSingles,b);
            System.out.println("Move: "+move);
            long[] moveArray = BitBoard.parseMove(move);
            b.moveSinglePiece(moveArray[0],moveArray[1],b.redsTurn);
            b.redsTurn = !b.redsTurn;
            System.out.println(b);
        }
    }


    // Method to extract a single bit (piece position) randomly from a bitboard
    private static long selectRandomPiece(long pieces) {
        List<Long> positions = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            if ((pieces & (1L << i)) != 0) {
                positions.add(1L << i);
            }
        }
        if (positions.isEmpty()) {
            return 0L;
        }
        return positions.get(random.nextInt(positions.size()));
    }

    // Get random move for a single piece
    public static String getPossibleMoveForRandomSingle(long allSingles,BitBoard board) {
        long singlePiece = selectRandomPiece(allSingles);
        if (singlePiece == 0L) {
            return "No pieces available to move.";
        }
        long possibleMoves = board.getPossibleMovesSingles(singlePiece,board.redsTurn);
        long move = selectRandomMove(possibleMoves);
        if (move == 0L) {
            return "No moves available for selected piece.";
        }
        // Assuming you have a method that converts a single bit to its board position
        String fromPosition = indexToPosition(Long.numberOfTrailingZeros(singlePiece));
        String toPosition = indexToPosition(Long.numberOfTrailingZeros(move));
        return fromPosition + "-" + toPosition;
    }


    public static long selectRandomMove(long possibleMoves) {
        List<Long> moveList = new ArrayList<>();
        for (long i = 0; i < 64; i++) {
            if ((possibleMoves & (1L << i)) != 0) {
                moveList.add(1L << i);
            }
        }
        if (moveList.isEmpty()) {
            return 0L; // No possible moves
        }
        // Select a random move
        return moveList.get(new Random().nextInt(moveList.size()));
    }
*/


}
