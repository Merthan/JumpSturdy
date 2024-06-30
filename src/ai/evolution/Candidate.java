package ai.evolution;

import ai.Evaluate;
import ai.Game;
import ai.MerthanAlphaBetaExperiment;
import misc.Tools;
import model.BitBoard;

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

    private int playGame(Candidate c1, Candidate c2) {
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
                board.printCommented("Game:"+matchesPlayedCounter++ +"/"+(Evo.populationSize*Evo.opponentsPerCandidate)+" c1: "+c1Starts+ " red: "+redStarts );
                return c1Starts? 1: -1; //-1= opponent won
            }
            redStarts = !redStarts;
            c1Starts = !c1Starts;//Flip, now other c2
        }

    }
}

