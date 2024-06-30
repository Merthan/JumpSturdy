package ai.evolution;

import java.util.*;

public class Evo {

    public static Random rand = new Random();
    public static int populationSize = 10;
    public static int generations = 10;//100 previously

    public static int opponentsPerCandidate = 3; // Less than 20/19 because too much time


    /**
     * pop 10, gen 10, opponents 3 after 300:
     * Generation 9:
     * Weight1 range: 9.746826502223714 to 13.307998966618323
     * Weight2 range: 18.764625372004623 to 18.764625372004623
     * Weight3 range: 5.312987341680916 to 9.93851777583728
     * Weight4 range: 12.552360791240071 to 13.654308665935384
     *
     *
     *
     *
     * **/

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
            population.add(new Candidate(rand.nextDouble()*20, rand.nextDouble()*20, rand.nextDouble()*20,rand.nextDouble()*20));

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

    // Crossover two parents to create child
    private static Candidate crossover(Candidate parent1, Candidate parent2) {
        double weight1 = rand.nextBoolean() ? parent1.pieceWeight : parent2.pieceWeight;
        double weight2 = rand.nextBoolean() ? parent1.attackedMoreWeight : parent2.attackedMoreWeight;
        double weight3 = rand.nextBoolean() ? parent1.distanceWeight : parent2.distanceWeight;
        double weight4 = rand.nextBoolean() ? parent1.thirdLastRowMoreWeight : parent2.thirdLastRowMoreWeight;

        return new Candidate(weight1, weight2, weight3,weight4);
    }

    // Mutate the offspring parameters
    private static void mutate(Candidate offspring) {
        if (rand.nextDouble() < 0.1) { // 10% mutation rate PREVIOUS 0.1, NOW 2
            offspring.pieceWeight += rand.nextGaussian() * 2;
            offspring.pieceWeight = Math.max(0, Math.min(20, offspring.pieceWeight));
        }
        if (rand.nextDouble() < 0.1) {
            offspring.attackedMoreWeight += rand.nextGaussian() * 2;
            offspring.attackedMoreWeight= Math.max(0, Math.min(20, offspring.attackedMoreWeight));
        }
        if (rand.nextDouble() < 0.1) {
            offspring.distanceWeight += rand.nextGaussian() * 2;
            offspring.distanceWeight = Math.max(0, Math.min(20, offspring.distanceWeight));
        }
        if (rand.nextDouble() < 0.1) {
            offspring.thirdLastRowMoreWeight += rand.nextGaussian() * 2;
            offspring.thirdLastRowMoreWeight= Math.max(0, Math.min(20, offspring.thirdLastRowMoreWeight));
        }
    }
}
