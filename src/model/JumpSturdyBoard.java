package model;

import java.util.ArrayList;
import java.util.Arrays;

public class JumpSturdyBoard {
    private static final int EMPTY = 0;
    private static final int CORNER = 9;
    private static final int RED_ON_RED = 1;  // 'A'
    private static final int RED_ON_BLUE = 2; // 'B'
    private static final int BLUE_ON_BLUE = 3; // 'C'
    private static final int BLUE_ON_RED = 4;  // 'D'
    private static final int RED = 5;          // 'X'
    private static final int BLUE = 6;         // 'Y'

    private static final String[][] TEMP_MAPPINGS = {
            {"r0", "X"},
            {"b0", "Y"},
            {"rr", "A"},
            {"rb", "D"},//Blue on red
            {"bb", "C"},
            {"br", "B"}//Red on blue
    };
    private int[][] board= new int[8][8];;



    public JumpSturdyBoard() {
        //Fill completely with empty
        Arrays.fill(board,new int[]{0,0,0,0,0,0,0,0,0,0});
        board[0][0]=-1;
        board[9][9]=-1;
        board[0][9]=-1;
        board[9][0]=-1;
    }

    public JumpSturdyBoard(String fen){
        //fromFen(fen);
        board = fenToBoard2(fen);//fenToBoard(fen);
    }

    // Convert board state to FEN-like notation
    public String toFen() {
        StringBuilder fen = new StringBuilder();
        for (int i = 1; i < 9; i++) {
            int emptyCount = 0;
            for (int j = 1; j < 9; j++) {
                if (board[i][j] == 0) {
                    emptyCount++;
                } else {
                    if (emptyCount != 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(getFenChar(board[i][j]));
                }
            }
            if (emptyCount != 0) {
                fen.append(emptyCount);
            }
            if (i < 8) fen.append('/');
        }
        return fen.toString();
    }

    // Convert FEN-like notation to board state
    public void fromFen(String fen) {


        String[] rows = fen.split("/");
        for (int i = 0; i < rows.length; i++) {
            board[i] = readFenRow(rows[i],i==0||i==7);
        }



/*        String[] rows = fen.split("/");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if((i==0 && j==0)||(i==7 && j==7)||(i==7 && j==0)||(i==0 && j==7)){

                }
            }
        }

        for (int i = 1; i <= 8; i++) {
            int col = 1;
            String row = rows[i - 1];
            System.out.println("Row:"+row);
            for (int j = 0; j < row.length(); j++) {
                char c = row.charAt(j);
                if (Character.isDigit(c)) {
                    int num = Character.getNumericValue(c);
                    for (int k = 0; k < num; k++) {
                        this.board[i][col++] = 0;
                    }
                } else {
                    this.board[i][col++] = getBoardValue(c);
                }
            }
        }*/
    }

    public int[][] fenToBoard(String fen) {
        String[] rows = fen.split("/");
        int[][] board = new int[rows.length][];

        for (int i = 0; i < rows.length; i++) {
            ArrayList<Integer> rowList = new ArrayList<>();
            for (int j = 0; j < rows[i].length(); j++) {
                char c = rows[i].charAt(j);
                if (Character.isDigit(c)) {
                    int emptySpaces = c - '0';
                    for (int k = 0; k < emptySpaces; k++) {
                        rowList.add(EMPTY);
                    }
                } else {
                    char nextChar = j + 1 < rows[i].length() ? rows[i].charAt(j + 1) : '_';
                    if (Character.isDigit(nextChar)) {
                        rowList.add(getFenCharValue(c, '_'));
                    } else {
                        rowList.add(getFenCharValue(c, nextChar));
                        j++; // Skip next character as it's part of the current piece
                    }
                }
            }
            board[i] = rowList.stream().mapToInt(Integer::intValue).toArray();
        }

        return board;
    }
/*    public String boardToFen() {
        StringBuilder fen = new StringBuilder();

        for (int[] row : board) {
            int emptyCount = 0;
            for (int cell : row) {
                if (cell == EMPTY) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(getFenRepresentation(cell));
                }
            }
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            fen.append('/');
        }

        // Remove the last '/'
        fen.deleteCharAt(fen.length() - 1);

        return fen.toString();
    }*/
public String boardToFen() {


    StringBuilder fenBuilder = new StringBuilder();



    for (int row = 0; row < board.length; row++) {
        if (row > 0) {
            fenBuilder.append('/');
        }
        int emptyCount = 0;
        for (int col = 0; col < board[row].length; col++) {
            int piece = board[row][col];
            if (piece == EMPTY) {
                emptyCount++;
            } else {
                if (emptyCount > 0) {
                    fenBuilder.append(emptyCount);
                    emptyCount = 0;
                }
                fenBuilder.append(getFenCharacter(piece));
            }
        }
        if (emptyCount > 0) {
            fenBuilder.append(emptyCount);
        }
    }
    String fen = fenBuilder.toString();
    for (String[] mapping : TEMP_MAPPINGS) {
        fen = fen.replace(mapping[1], mapping[0]);
    }

    return fen.replace(".","");//Finally, remove all .dots
}

    private char getFenCharacter(int piece) {
        switch (piece) {
            case RED_ON_RED: return 'A';
            case RED_ON_BLUE: return 'B';
            case BLUE_ON_BLUE: return 'C';
            case BLUE_ON_RED: return 'D';
            case RED: return 'X';
            case BLUE: return 'Y';
            default: return '.'; // Handle empty spaces as 0 which is an empty char (as int)
        }
    }

    private String getFenRepresentation(int value) {
        switch(value) {
            case 1: return "r0";
            case 11: return "b0";
            case 2: return "rr";
            case 12: return "rb";
            case 13: return "bb";
            case 3: return "br";
            default: throw new IllegalArgumentException("Invalid board value: " + value);
        }
    }

    private String cleanFen(String fen){//

        for (String[] mapping : TEMP_MAPPINGS) {
            fen = fen.replace(mapping[0], mapping[1]);
        }
        return fen;
    }
    public int[][] fenToBoard2(String fen) {
        int[][] board = new int[8][8];
        board[0] = new int[6];
        board[7] = new int[6];

        int row = 0, col = 0;
        fen = cleanFen(fen);

        for (int i = 0; i < fen.length(); i++) {
            char c = fen.charAt(i);
            if (c == '/') {
                row++;
                col = 0;
            } else if (Character.isDigit(c)) {
                int count = c - '0';
                for (int j = 0; j < count; j++) {
                    board[row][col++] = EMPTY;
                }
            } else {
                board[row][col++] = getPieceValue(c);
            }
        }
        board[0] = surroundArray(board[0]);
        board[7] = surroundArray(board[7]);
        return board;
    }

    public static int[] surroundArray(int[] original) {
        // Create a new array that is two elements larger than the original
        int[] newArray = new int[original.length + 2];

        // Set the first and last elements to -1
        newArray[0] = CORNER;
        newArray[newArray.length - 1] = CORNER;

        // Copy the original array into the new array starting at index 1
        System.arraycopy(original, 0, newArray, 1, original.length);

        return newArray;
    }
    private int getPieceValue(char c) {
        switch (c) {
            case 'A': return RED_ON_RED;
            case 'B': return RED_ON_BLUE;
            case 'C': return BLUE_ON_BLUE;
            case 'D': return BLUE_ON_RED;
            case 'X': return RED;
            case 'Y': return BLUE;
            default: return EMPTY; // Default case handles unexpected characters
        }
    }

    public int[] readFenRow(String row,boolean isFirstOrLast){
        int[] rowArray = new int[]{CORNER,0,0,0,0,0,0,CORNER};
        int rowArrayIndex = isFirstOrLast? 1: 0;//If, start at 1 higher
        char[] rowChars=row.toCharArray();
        int length = isFirstOrLast? rowChars.length-1 : rowChars.length;
        for (int i = 0; i < rowChars.length && rowArrayIndex<length;) {

            char current = rowChars[i];
            //Is the next including current a doublechar, includes bounds check

            if(current=='0'){
                i++;
                rowArrayIndex++;
                continue;
            }

            char nextDoubleChar = (Character.isDigit(current))? '_' : ((i+1<length)? rowChars[i+1] : '_');
            if(Character.isDigit(nextDoubleChar)){
                nextDoubleChar='_';
            }

            if (nextDoubleChar!='_'){
                rowArray[rowArrayIndex++] = getFenCharValue(current,nextDoubleChar);
                i++;
                continue;
            }
            if(Character.isDigit(current)){
                int initialIndex = rowArrayIndex;
                for (int j = rowArrayIndex; j < initialIndex+Character.getNumericValue(current); j++) {
                    rowArray[j] = 0;//Set to not occupied
                    System.out.println("RowAI raising:"+rowArrayIndex);
                    rowArrayIndex++;//Advance
                }
            }else{
                rowArray[rowArrayIndex++] = getFenCharValue(current);
                i++;
            }

        }
        System.out.println("Returned: "+Arrays.toString(rowArray));
        return rowArray;
    }

    private int getFenCharValue(char current){
        return getFenCharValue(current,'_');
    }
    private int getFenCharValue(char current, char nextDoubleChar) {
        if(nextDoubleChar == '_'){
           if(current == 'r') return 1;
           if(current == 'b') return 11;
           throw new RuntimeException("unknown char");
        }
        if(current == 'r'&& nextDoubleChar == 'r') return 2;//Double
        if(current == 'r'&& nextDoubleChar == 'b') return 12;//B
        if(current == 'b'&& nextDoubleChar == 'b') return 13;//Double
        if(current == 'b'&& nextDoubleChar == 'r') return 3;//R
        throw new RuntimeException("unknown char"+current+" "+nextDoubleChar);
    }


    private char getFenChar(int value) {
        switch (value) {
            case 1: return 'r';
            case 2: return 'b';
            case 3: return 'R';
            case 4: return 'B';
            default: return '0';
        }
    }

    private int getBoardValue(char fenChar) {
        switch (fenChar) {
            case 'r': return 1;
            case 'b': return 2;
            case 'R': return 3;
            case 'B': return 4;
            default: return 0;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
/*        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if (i == 1 || i == 8) {
                    if (j == 1 || j == 8) {
                        sb.append("- ");
                    } else {
                        sb.append(this.board[i][j]).append(" ");
                    }
                } else {
                    sb.append(this.board[i][j]).append(" ");
                }
            }
            sb.append("\n");
        }*/
        sb.append(Arrays.deepToString(board));
        sb.append("\nFEN: ").append(boardToFen()).append("\n");
        return sb.toString();
    }

    public void printBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(this.board[i][j] + " ");
            }
            System.out.println();
        }
    }
}