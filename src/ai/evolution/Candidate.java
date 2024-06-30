package ai.evolution;

import ai.Evaluate;
import ai.Game;
import ai.MerthanAlphaBetaExperiment;
import misc.Tools;
import model.BitBoard;

import static misc.Tools.doubleFormatted;

class Candidate {
    double pieceWeight;
    double attackedMoreWeight;
    double distanceWeight;
    double thirdLastRowMoreWeight;
    double fitness;

    public static int matchesPlayedCounter = 0;

    public Candidate(double w1, double w2, double w3,double w4) {
        pieceWeight = w1;
        attackedMoreWeight = w2;
        distanceWeight = w3;
        thirdLastRowMoreWeight=w4;
        fitness = 0;
        normalizeWeights(this);
    }

    @Override
    public String toString() {
        return "[Piece: " + doubleFormatted(pieceWeight) +
                " Attack: " + doubleFormatted(attackedMoreWeight) +
                " Dist: " + doubleFormatted(distanceWeight) +
                " Third: " + doubleFormatted(thirdLastRowMoreWeight) +
                " Fitness: " + doubleFormatted(fitness)+"]";
    }

    // play, update fitness
    public void playMatch(Candidate opponent) {
        int result = playGame(this, opponent);
        if (result == 1) { // this candidate wins
            this.fitness += 1;
        } else if (result == 0) { // draw
            this.fitness += 0.5;
            opponent.fitness += 0.5;
        } else { // opponent wins
            opponent.fitness += 1;
        }
    }

    static void normalizeWeights(Candidate candidate) {
        double totalWeight = candidate.pieceWeight + candidate.attackedMoreWeight + candidate.distanceWeight + candidate.thirdLastRowMoreWeight;
        if (totalWeight != 0) { // Avoid division by zero
            candidate.pieceWeight /= totalWeight;
            candidate.attackedMoreWeight /= totalWeight;
            candidate.distanceWeight /= totalWeight;
            candidate.thirdLastRowMoreWeight /= totalWeight;
        }
    }

    private static int playGame(Candidate c1, Candidate c2) {
        boolean c1Starts = Evo.rand.nextBoolean();//Not just start but also keeps track of current c1c2
        boolean redStarts = Evo.rand.nextBoolean();

        MerthanAlphaBetaExperiment alpha = new MerthanAlphaBetaExperiment();
        BitBoard board = new BitBoard(Game.DEFAULT_BOARD);
        byte winningState = BitBoard.WINNER_ONGOING;
        while(true){
            Evaluate.pieceWeight = c1Starts?c1.pieceWeight:c2.pieceWeight;
            Evaluate.attackedMoreWeight = c1Starts?c1.attackedMoreWeight:c2.attackedMoreWeight;
            Evaluate.distanceWeight= c1Starts?c1.distanceWeight:c2.distanceWeight;
            Evaluate.thirdLastMoreWeight= c1Starts?c1.thirdLastRowMoreWeight:c2.thirdLastRowMoreWeight;


            byte[] move = alpha.findBestMove(board,redStarts,50).get(0);
            board.doMoveNoParse(move,redStarts,false);

            winningState = board.currentWinningState();
            if(winningState!=BitBoard.WINNER_ONGOING){
                System.out.println("c1Starts: "+c1Starts+ " won");
                //TODO: assuming c1Starts has correct state here as the move that c1Starts did lead to win
                board.printCommented("Game:"+matchesPlayedCounter++ +"/"+(Evo.populationSize*Evo.opponentsPerCandidate)+" c1: "+c1Starts+ " red: "+redStarts +"\n|"+
                        (c1Starts?c1.toString():c2.toString())+"| won against |\n|"+ (!c1Starts?c1.toString():c2.toString()));
                return c1Starts? 1: -1; //-1= opponent won
            }
            redStarts = !redStarts;
            c1Starts = !c1Starts;//Flip, now other c2
        }
    }

    private static int playGameVsDefault(double w1,double w2,double w3,double w4) {
        boolean c1Starts = Evo.rand.nextBoolean();//Not just start but also keeps track of current c1c2
        boolean redStarts = Evo.rand.nextBoolean();

        MerthanAlphaBetaExperiment alpha = new MerthanAlphaBetaExperiment();
        BitBoard board = new BitBoard(Game.DEFAULT_BOARD);
        byte winningState = BitBoard.WINNER_ONGOING;
        while(true){
            Evaluate.pieceWeight = c1Starts?w1:1;
            Evaluate.attackedMoreWeight = c1Starts?w2:1;
            Evaluate.distanceWeight= c1Starts?w3:1;
            Evaluate.thirdLastMoreWeight= c1Starts?w4:1;


            byte[] move = alpha.findBestMove(board,redStarts,100).get(0);
            //System.out.println(((Evaluate.pieceWeight==1)?"DEFAULT: ":"Cand: ")+ (Tools.parseMoveToString(move))+" red:"+redStarts+ " c1:"+c1Starts);
            board.doMoveNoParse(move,redStarts,true);

            winningState = board.currentWinningState();
            if(winningState!=BitBoard.WINNER_ONGOING){
                System.out.println("c1Starts: "+c1Starts+ " won");
                //TODO: assuming c1Starts has correct state here as the move that c1Starts did lead to win
                board.printCommented("Game:"+matchesPlayedCounter++ +"/"+(Evo.populationSize*Evo.opponentsPerCandidate)+" c1: "+c1Starts+ " red: "+redStarts +"\n|"+
                        (c1Starts?"NEW":"DEFAULT")+"| won against |\n|"+ (!c1Starts?"NEW":"DEFAULT"));
                return c1Starts? 1: -1; //-1= opponent won
            }
            redStarts = !redStarts;
            c1Starts = !c1Starts;//Flip, now other c2
        }
    }


    public static void main(String[] args) {
        //Play game against default

        //
        //playGame(new Candidate(1,1,1,1),new Candidate(2,1,3,3));
        for (int i = 0; i < 10; i++) {
            //playGameVsDefault(2,1,3,3);
            playGameVsDefault(2,1,3,3);
        }
    }

}

