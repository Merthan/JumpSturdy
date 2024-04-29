import model.JumpSturdyBoard;

public class Main {

    public static void main(String[] args) {
        String fen = "2rr3/5r02/1rr1rr2r0r0/2rb3b01/2r0b04/5b0bb1/2bb2b02/3b02";
        System.out.println("FEN: "+fen);
        JumpSturdyBoard board = new JumpSturdyBoard(fen);
        //JumpSturdyBoard board = new JumpSturdyBoard("r0r0r0r0r0r0/1r0r0r0r0r0r0/8/8/8/8/1b0b0b0b0b0b0/b0b0b0b0b0b0");
        System.out.println(board);
        board.printBoard();
    }

}
