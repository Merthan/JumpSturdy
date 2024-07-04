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
        long start = System.currentTimeMillis();
        while(true){
            Evaluate.pieceWeight = c1Starts?c1.pieceWeight:c2.pieceWeight;
            Evaluate.attackedMoreWeight = c1Starts?c1.attackedMoreWeight:c2.attackedMoreWeight;
            Evaluate.distanceWeight= c1Starts?c1.distanceWeight:c2.distanceWeight;
            Evaluate.thirdLastMoreWeight= c1Starts?c1.thirdLastRowMoreWeight:c2.thirdLastRowMoreWeight;


            byte[] move = alpha.findBestMove(board,redStarts,20).get(0);
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

            if(System.currentTimeMillis()-start > 10000){//If this takes longer than 10 seconds, draw
                System.out.println("Draw, history too long:"+board.previousMoves());
                return 0;
            }
        }
    }

    private static int playGameVsDefault(double w1,double w2,double w3,double w4,int millis) {
        boolean c1Starts = Evo.rand.nextBoolean();//Not just start but also keeps track of current c1c2
        boolean redStarts = Evo.rand.nextBoolean();

        long start = System.currentTimeMillis();

        MerthanAlphaBetaExperiment alpha = new MerthanAlphaBetaExperiment();
        BitBoard board = new BitBoard(Game.DEFAULT_BOARD);
        byte winningState = BitBoard.WINNER_ONGOING;
        System.out.println();
        while(true){
            Evaluate.pieceWeight = c1Starts?w1:1;
            Evaluate.attackedMoreWeight = c1Starts?w2:1;
            Evaluate.distanceWeight= c1Starts?w3:1;
            Evaluate.thirdLastMoreWeight= c1Starts?w4:1;


            byte[] move = alpha.findBestMove(board,redStarts,millis).get(0);

            if(board.previousMove.length>80 && board.previousMove.length % 20 == 0){
                move = board.getAllPossibleMovesByteSorted(redStarts)[0];//If lots of previous moves, every 20.th turn do mostly random move
                Tools.printRed("Too many moves, doing mostly random, length:"+board.previousMove.length);
            }
            //System.out.println(((Evaluate.pieceWeight==1)?"DEFAULT: ":"Cand: ")+ (Tools.parseMoveToString(move))+" red:"+redStarts+ " c1:"+c1Starts);
            //System.out.println("winningstate:"+board.currentWinningState()+" before move: "+Tools.parseMoveToString(move)+" for:red="+redStarts+ " fen: "+board.toFEN());
            board.doMoveNoParse(move,redStarts,true);
            System.out.print("["+Tools.parseMoveToString(move)+"] ");
            winningState = board.currentWinningState();
            if(winningState!=BitBoard.WINNER_ONGOING){
                System.out.println("\nc1Starts: "+c1Starts+ " won");
                //TODO: assuming c1Starts has correct state here as the move that c1Starts did lead to win
                board.printCommented("Game:"+matchesPlayedCounter++ +"/"+(Evo.populationSize*Evo.opponentsPerCandidate)+" c1: "+c1Starts+ " red: "+redStarts +" time taken:"+((System.currentTimeMillis()-start)/1000)+ "\n|"+
                        (c1Starts?"NEW":"DEFAULT")+"| won against |\n|"+ (!c1Starts?"NEW":"DEFAULT"));
                return c1Starts? 1: -1; //-1= opponent won
            }
            //10 minutes currently upper limit per game
            if(System.currentTimeMillis()-start > 600000){//If this takes longer than 10 seconds, draw
                System.out.println("Draw, history too long:"+board.previousMoves());
                return 0;
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
            //
            // playGameVsDefault(2,1,3,3);
            playGameVsDefault(0.5,0.34,0.10,0.05,50);
            //TODO: NEW mostly wins here against default, however 2000ms again causes the results to not be that pretty
            //TODO: 2000 needs to be tested over a long amount of time to get representative results for that
        }

        /**
         * 10/10 won with 0.5,0.34,0.10,0.05 at 100ms
         * 9/10 won with 0.5,0.34,0.10,0.05 at 200ms
         * 4/10 won with 0.5,0.34,0.10,0.05 at 2000ms - each game took 2 minutes, around 20 total
         * **/
    }

}

