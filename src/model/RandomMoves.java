package model;



import java.util.List;
        import java.util.Random;

public class RandomMoves {


    public static String generateRandomMove(List<String> possibleMoves) {

        //no possible moves
        if (possibleMoves.isEmpty()) {
            return null;
        }

        Random random = new Random();
        return possibleMoves.get(random.nextInt(possibleMoves.size())) ;
    }


}
