import model.BitBoard;
import model.JumpSturdyBoard;

public class Main {

    public static void main(String[] args) {
        String[] fens = new String[]{"r0r0r0r0r0r0/1r0r0r0r0r0r0/8/8/8/8/1b0b0b0b0b0b0/b0b0b0b0b0b0","2rr3/5r02/1rr1rr2r0r0/2rb3b01/2r0b04/5b0bb1/2bb2b02/3b02"};
        String fen = fens[1];

        JumpSturdyBoard temp= new JumpSturdyBoard(fen);
        BitBoard b = new BitBoard();

        b.readBoard(temp.board);

        //b.displayBitboard(b.getPossibleMovesSingles(b.redSingles,true));
        //b.displayBitboard(b.getPossibleMovesDoubles(b.blueDoubles,false));
        b.displayBitboard(b.getMovesForTeam(false));

        //System.out.println("DISPLAYall");
        //b.displayBitboard(b.redSingles);
        //b.displayBitboard(b.redSingles | b.blueSingles | b.redDoubles |b.blueDoubles | b.red_on_blue | b.blue_on_red);
        System.out.println(b);
        if(true)return;


        System.out.println("FEN: "+fen);
        JumpSturdyBoard board = new JumpSturdyBoard(fen);
        //JumpSturdyBoard board = new JumpSturdyBoard("r0r0r0r0r0r0/1r0r0r0r0r0r0/8/8/8/8/1b0b0b0b0b0b0/b0b0b0b0b0b0");
        System.out.println(board);
        board.printBoard();
    }

}
