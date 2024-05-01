import model.BitBoard;
import model.JumpSturdyBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static model.BitBoard.indexToPosition;
import static model.BitBoard.positionToIndex;

public class Main {

    private static Random random = new Random();
    public static void main(String[] args) {
        String[] fens = new String[]{"r0r0r0r0r0r0/1r0r0r0r0r0r0/8/8/8/8/1b0b0b0b0b0b0/b0b0b0b0b0b0","2rr3/5r02/1rr1rr2r0r0/2rb3b01/2r0b04/5b0bb1/2bb2b02/3b02"};
        String fen = fens[0];

        JumpSturdyBoard temp= new JumpSturdyBoard(fen);
        BitBoard b = new BitBoard();

        b.readBoard(temp.board);

        //b.displayBitboard(b.getPossibleMovesSingles(b.redSingles,true));
        //b.displayBitboard(b.getPossibleMovesDoubles(b.blueDoubles,false));
        b.displayBitboard(b.getMovesForTeam(true));

        //System.out.println("DISPLAYall");
        //b.displayBitboard(b.redSingles);
        //b.displayBitboard(b.redSingles | b.blueSingles | b.redDoubles |b.blueDoubles | b.red_on_blue | b.blue_on_red);
        System.out.println(b);
        long[] move = BitBoard.parseMove("F7-F6");
        b.moveSinglePiece(move[0],move[1],true);
        System.out.println(b);
        if(true)return;

        System.out.println(b);
        ///long[] move = BitBoard.parseMove("C5-E6");
        //b.moveDoublePiece(move[0],move[1],false);
        //System.out.println(b);

        System.out.println("-------------\n\n\n");


        if(true)return;


        System.out.println("FEN: "+fen);
        JumpSturdyBoard board = new JumpSturdyBoard(fen);
        //JumpSturdyBoard board = new JumpSturdyBoard("r0r0r0r0r0r0/1r0r0r0r0r0r0/8/8/8/8/1b0b0b0b0b0b0/b0b0b0b0b0b0");
        System.out.println(board);
        board.printBoard();



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
