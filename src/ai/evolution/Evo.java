package ai.evolution;

import java.util.*;

public class Evo {

    public static Random rand = new Random();
    public static int populationSize = 10;
    public static int generations = 10;//100 previously

    public static int opponentsPerCandidate = 5; // Less than 20/19 because too much time


    /**
     * pop 10, gen 10, opponents 3 after 300:
     * Generation 9:
     * Weight1 range: 9.746826502223714 to 13.307998966618323
     * Weight2 range: 18.764625372004623 to 18.764625372004623
     * Weight3 range: 5.312987341680916 to 9.93851777583728
     * Weight4 range: 12.552360791240071 to 13.654308665935384
     * <p>
     * <p>
     * Tried again, pop10 gen10 opp3 after 300 normalized change:
     * Generation 9:
     * Weight1 range: 0.06978540619062605 to 0.3529705567216121
     * Weight2 range: -0.023442314766092813 to 0.14194012416251653
     * Weight3 range: 0.3584529611342397 to 0.4638815847194541
     * Weight4 range: 0.2516543343923714 to 0.4581839821098716
     * <p>
     * guessing good values: 1-2, 0.5, 3, 3
     *
     *
     * NEWEST, EVAL CLASS MULTIPLIED BY 100
     * Generation 9:
     * Weight1 range: 0.4089442455567967 to 0.6369416615253265
     * Weight2 range: 0.24914283520083721 to 0.48619588597155455
     * Weight3 range: 0.028100045920363886 to 0.28984455384289753
     * Weight4 range: -0.07434226521603135 to 0.1540912407193666
     *
     *
     **/

    public static void main(String[] args) {


        List<Candidate> population = initializePopulation(populationSize);

        for (int generation = 0; generation < generations; generation++) {

            rankCandidatesByFitness(population);

            // Play matches between candidates
            for (Candidate candidate : population) {
                // Select a subset of opponents based on fitness
                List<Candidate> opponents = selectSmartOpponents(population, candidate, opponentsPerCandidate);
                for (Candidate opponent : opponents) {
                    candidate.playMatch(opponent);
                }
            }

            double minWeight1 = Double.MAX_VALUE;
            double maxWeight1 = Double.MIN_VALUE;
            double minWeight2 = Double.MAX_VALUE;
            double maxWeight2 = Double.MIN_VALUE;
            double minWeight3 = Double.MAX_VALUE;
            double maxWeight3 = Double.MIN_VALUE;
            double minWeight4 = Double.MAX_VALUE;
            double maxWeight4 = Double.MIN_VALUE;

            for (Candidate candidate : population) {
                minWeight1 = Math.min(minWeight1, candidate.pieceWeight);
                maxWeight1 = Math.max(maxWeight1, candidate.pieceWeight);
                minWeight2 = Math.min(minWeight2, candidate.attackedMoreWeight);
                maxWeight2 = Math.max(maxWeight2, candidate.attackedMoreWeight);
                minWeight3 = Math.min(minWeight3, candidate.distanceWeight);
                maxWeight3 = Math.max(maxWeight3, candidate.distanceWeight);
                minWeight4 = Math.min(minWeight4, candidate.thirdLastRowMoreWeight);
                maxWeight4 = Math.max(maxWeight4, candidate.thirdLastRowMoreWeight);
            }
            System.out.println("Generation " + generation + ":");
            System.out.println("Weight1 range: " + minWeight1 + " to " + maxWeight1);
            System.out.println("Weight2 range: " + minWeight2 + " to " + maxWeight2);
            System.out.println("Weight3 range: " + minWeight3 + " to " + maxWeight3);
            System.out.println("Weight4 range: " + minWeight4 + " to " + maxWeight4);


            List<Candidate> matingPool = selectMatingPool(population);

            population = createNewPopulation(matingPool, populationSize);

            // Reset fitness for next generation
            for (Candidate candidate : population) {
                candidate.fitness = 0;
            }
        }
    }

    private static void rankCandidatesByFitness(List<Candidate> population) {
        // Sort by fitness
        population.sort(Comparator.comparingDouble(c -> c.fitness));
    }

    // Select a subset of opponents based on fitness
    private static List<Candidate> selectSmartOpponents(List<Candidate> population, Candidate candidate, int opponentsPerCandidate) {
        List<Candidate> opponents = new ArrayList<>();
        int index = population.indexOf(candidate);

        // Select opponents from different fitness levels
        for (int i = 1; i <= opponentsPerCandidate; i++) {
            int opponentIndex = (index + i) % population.size();
            opponents.add(population.get(opponentIndex));
        }

        return opponents;
    }

    private static List<Candidate> selectRandomOpponents(List<Candidate> population, Candidate candidate, int opponentsPerCandidate) {
        List<Candidate> opponents = new ArrayList<>(population);
        opponents.remove(candidate);
        Collections.shuffle(opponents);
        return opponents.subList(0, Math.min(opponentsPerCandidate, opponents.size()));
    }

    // Initialize population with random candidates
    private static List<Candidate> initializePopulation(int size) {
        List<Candidate> population = new ArrayList<>();
        for (int i = 0; i < size; i++) {//Was without 20 before for 0..1
            //population.add(new Candidate(rand.nextDouble()*20, rand.nextDouble()*20, rand.nextDouble()*20,rand.nextDouble()*20));
            population.add(new Candidate(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), rand.nextDouble()));
            //Should be normalized?
        }
        return population;
    }

    // Select the best candidates for the mating pool
    private static List<Candidate> selectMatingPool(List<Candidate> population) {
        // Sort by fitness
        population.sort((c1, c2) -> Double.compare(c2.fitness, c1.fitness));
        // Top 50%
        return population.subList(0, population.size() / 2);
    }

    // Create new population through crossover and mutation
    private static List<Candidate> createNewPopulation(List<Candidate> matingPool, int size) {
        List<Candidate> newPopulation = new ArrayList<>();
        while (newPopulation.size() < size) {
            Candidate parent1 = matingPool.get(rand.nextInt(matingPool.size()));
            Candidate parent2 = matingPool.get(rand.nextInt(matingPool.size()));
            Candidate offspring = crossover(parent1, parent2);
            mutate(offspring);
            newPopulation.add(offspring);
        }
        return newPopulation;
    }


    /*    private static Candidate crossover(Candidate parent1, Candidate parent2) {
            double weight1 = rand.nextBoolean() ? parent1.pieceWeight : parent2.pieceWeight;
            double weight2 = rand.nextBoolean() ? parent1.attackedMoreWeight : parent2.attackedMoreWeight;
            double weight3 = rand.nextBoolean() ? parent1.distanceWeight : parent2.distanceWeight;
            double weight4 = rand.nextBoolean() ? parent1.thirdLastRowMoreWeight : parent2.thirdLastRowMoreWeight;

            return new Candidate(weight1, weight2, weight3,weight4);
        }*/
    private static Candidate crossover(Candidate parent1, Candidate parent2) {
        double alpha = 0.5;
        double weight1 = blxAlphaCrossover(parent1.pieceWeight, parent2.pieceWeight, alpha);
        double weight2 = blxAlphaCrossover(parent1.attackedMoreWeight, parent2.attackedMoreWeight, alpha);
        double weight3 = blxAlphaCrossover(parent1.distanceWeight, parent2.distanceWeight, alpha);
        double weight4 = blxAlphaCrossover(parent1.thirdLastRowMoreWeight, parent2.thirdLastRowMoreWeight, alpha);

        return new Candidate(weight1, weight2, weight3, weight4);
    }

    private static double blxAlphaCrossover(double w1, double w2, double alpha) {
        double min = Math.min(w1, w2);
        double max = Math.max(w1, w2);
        double range = max - min;
        return min - range * alpha + rand.nextDouble() * range * (1 + 2 * alpha);
    }

    // Mutate the offspring parameters
    private static void mutate(Candidate offspring) {
        double range = 0.1;
        double rate = 0.1;
        if (rand.nextDouble() < rate) { // 10% mutation rate PREVIOUS 0.1, NOW 2
            offspring.pieceWeight += rand.nextGaussian() * range;
            //offspring.pieceWeight = Math.max(0, Math.min(20, offspring.pieceWeight));
        }
        if (rand.nextDouble() < rate) {
            offspring.attackedMoreWeight += rand.nextGaussian() * range;
            //offspring.attackedMoreWeight= Math.max(0, Math.min(20, offspring.attackedMoreWeight));
        }
        if (rand.nextDouble() < rate) {
            offspring.distanceWeight += rand.nextGaussian() * range;
            //offspring.distanceWeight = Math.max(0, Math.min(20, offspring.distanceWeight));
        }
        if (rand.nextDouble() < rate) {
            offspring.thirdLastRowMoreWeight += rand.nextGaussian() * range;
            //offspring.thirdLastRowMoreWeight= Math.max(0, Math.min(20, offspring.thirdLastRowMoreWeight));
        }
        normalizeWeights(offspring);

    }

    private static void normalizeWeights(Candidate candidate) {
        double totalWeight = candidate.pieceWeight + candidate.attackedMoreWeight + candidate.distanceWeight + candidate.thirdLastRowMoreWeight;
        if (totalWeight != 0) { // Avoid division by zero
            candidate.pieceWeight /= totalWeight;
            candidate.attackedMoreWeight /= totalWeight;
            candidate.distanceWeight /= totalWeight;
            candidate.thirdLastRowMoreWeight /= totalWeight;
        }
    }
}
