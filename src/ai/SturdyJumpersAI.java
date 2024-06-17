package ai;

import misc.Tools;
import model.*;

import java.util.List;
import java.util.Comparator;

public class SturdyJumpersAI {

    private static int cutOffs = 0;
    private static int untersuchteZustaende = 0;
    private static boolean timeUp;
    private static long TIME_LIMIT = 20000000000L; // 2 seconds in nanoseconds
    private static final long BASE_TIME_LIMIT = 20000000000L; // Basis-Zeitlimit in Nanosekunden
    private static final long MAX_TIME_LIMIT = 200000000000L; // Maximales Zeitlimit in Nanosekunden
    private static long remainingTime = 60000000000L; // Verbleibende Gesamtzeit in Nanosekunden (z.B. 60 Sekunden)

    public static String findBestMove(SearchType searchType, BitBoard board, boolean isRed) {
        return findBestMoveTestWrapper(searchType, board, isRed).bestMove;
    }


    public static TestWrapper findBestMoveTestWrapper(SearchType searchType, BitBoard board, boolean isRed) {
        switch (searchType) {
            case MINIMAX -> {
                return alphaBetaWrapper(board, isRed, false);
            }
            case ALPHABETA -> {
                return alphaBetaWrapper(board, isRed, true);
            }
        }
        return alphaBetaWrapper(board, isRed, true);
    }

    public static TestWrapper alphaBetaWrapper(BitBoard board, boolean isRed, boolean withCutoffs) {
        int bestValue = Integer.MIN_VALUE;
        String bestMove = null;
        List<String> legalMoves = board.getAllPossibleMoves(isRed);
        int depth = 1;
        sortMoves(legalMoves, board, isRed);

        if(BitBoardManipulation.doesNextMoveWin(isRed, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                board.red_on_blue, board.blue_on_red))
            return new TestWrapper(untersuchteZustaende, bestValue, Tools.lastRowMove(legalMoves, isRed), isRed);


        untersuchteZustaende = 0;
        cutOffs = 0;
        long startTime = System.nanoTime();
        timeUp = false;

        while (!timeUp) {
            adjustTimeLimit(depth, legalMoves.size(), board); // Adjust time limit based on depth and number of legal moves
            for (String move : legalMoves) {
                untersuchteZustaende++;
                BitBoard newBoard = BitBoard.fromLongArray(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                        Tools.parseMove(move)[0], Tools.parseMove(move)[1], isRed,
                        board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                        board.red_on_blue, board.blue_on_red));

                int moveValue = withCutoffs ?
                        alphaBetaSearch(newBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, !isRed, startTime) :
                        alphaBetaSearchWithoutCutoffs(newBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, !isRed, startTime);
                if (moveValue > bestValue) {
                    bestValue = moveValue;
                    bestMove = move;
                }
                if (timeUp) break;
            }
            depth++;

        }
        System.out.println("Laufzeit: " + ((System.nanoTime() - startTime) / 1e6) + " ms\n" +
                "Erreichte Tiefe: " + (depth - 1) + "\n" +
                "Bester Zug: " + bestMove + ". Value: " + bestValue + "\n" +
                "Untersuchte Zustände: " + untersuchteZustaende + "\n" +
                "Cutoffs: " + cutOffs
        );

        return new TestWrapper(untersuchteZustaende, bestValue, bestMove, isRed);
    }

    // AlphaBeta with time management
    private static int alphaBetaSearch(BitBoard board, int depth, int alpha, int beta, boolean isRed, long startTime) {
        if (System.nanoTime() - startTime > TIME_LIMIT) {
            timeUp = true;
            return isRed ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }

        if (depth <= 0 || board.checkWinCondition() != BitBoard.WINNER_ONGOING) {
            int ruhesuche = BitBoardManipulation.ruhesuche(board,isRed);

            int eval =  Evaluate.evaluateSimple(isRed, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red) -
                    Evaluate.evaluateSimple(!isRed, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
            return eval;
        }

        List<String> legalMoves = board.getAllPossibleMoves(isRed);

        if (!isRed) {
            int maxEval = Integer.MIN_VALUE;
            for (String move : legalMoves) {
                untersuchteZustaende++;
                BitBoard newBoard = BitBoard.fromLongArray(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                        Tools.parseMove(move)[0], Tools.parseMove(move)[1], isRed,
                        board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                        board.red_on_blue, board.blue_on_red));
                int eval = alphaBetaSearch(newBoard, depth - 1, alpha, beta, !isRed, startTime);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, maxEval);
                if (beta <= alpha) {
                    cutOffs++;
                    break; // Beta cut-off
                }
                if (timeUp) break;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (String move : legalMoves) {
                untersuchteZustaende++;
                BitBoard newBoard = BitBoard.fromLongArray(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                        Tools.parseMove(move)[0], Tools.parseMove(move)[1], isRed,
                        board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                        board.red_on_blue, board.blue_on_red));
                int eval = alphaBetaSearch(newBoard, depth - 1, alpha, beta, !isRed, startTime);

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, minEval);
                if (beta <= alpha) {
                    cutOffs++;
                    break; // Alpha cut-off
                }
                if (timeUp) break;
            }
            return minEval;
        }
    }

    private static void adjustTimeLimit(int depth, int numMoves, BitBoard board) {
        // Einfache Heuristik zur Berechnung des Zeitlimits basierend auf Tiefe und der Anzahl der legalen Züge
        long timeLimit = BASE_TIME_LIMIT / ((long) (depth + 1) * (numMoves + 1) / 2);

        // Spielphasenanalyse: Anpassung des Zeitlimits basierend auf der Spielphase
        int gamePhase = evaluateGamePhase(board);
        switch (gamePhase) {
            case 1: // Eröffnung
                timeLimit *= 2; // Erhöhtes Zeitlimit in der Eröffnung
                break;
            case 2: // Mittelspiel
                timeLimit *= 1.5; // Mittel hohes Zeitlimit im Mittelspiel
                break;
            case 3: // Endspiel
                timeLimit *= 1.3; // Erhöhtes Zeitlimit im Endspiel
                break;
        }

        // Komplexität des aktuellen Zustands: Anpassung des Zeitlimits basierend auf der Anzahl der legalen Züge
        if (numMoves > 20) {
            timeLimit *= 0.8; // Reduziertes Zeitlimit bei vielen möglichen Zügen
        } else if (numMoves < 10) {
            timeLimit *= 1.5; // Erhöhtes Zeitlimit bei wenigen möglichen Zügen
        }

        // Verbleibende Zeit analysieren und anpassen
        long estimatedMovesLeft = estimateRemainingMoves(board);
        long timePerMove = remainingTime / estimatedMovesLeft;
        timeLimit = Math.min(timeLimit, timePerMove);

        // Sicherstellen, dass das Zeitlimit nicht das maximale Zeitlimit überschreitet
        TIME_LIMIT = Math.min(timeLimit, MAX_TIME_LIMIT);
    }

    private static void sortMoves(List<String> moves, BitBoard board, boolean isRed) {
        // Bewertung der Züge und Sortierung
        moves.sort((move1, move2) -> {
            BitBoard newBoard1 = BitBoard.fromLongArray(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                    Tools.parseMove(move1)[0], Tools.parseMove(move1)[1], isRed,
                    board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                    board.red_on_blue, board.blue_on_red));
            BitBoard newBoard2 = BitBoard.fromLongArray(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                    Tools.parseMove(move2)[0], Tools.parseMove(move2)[1], isRed,
                    board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                    board.red_on_blue, board.blue_on_red));

            int eval1 = Evaluate.evaluateSimple(isRed, newBoard1.redSingles, newBoard1.blueSingles, newBoard1.redDoubles, newBoard1.blueDoubles, newBoard1.red_on_blue, newBoard1.blue_on_red);
            int eval2 = Evaluate.evaluateSimple(isRed, newBoard2.redSingles, newBoard2.blueSingles, newBoard2.redDoubles, newBoard2.blueDoubles, newBoard2.red_on_blue, newBoard2.blue_on_red);

            return Integer.compare(eval2, eval1); // Sort
        });
    }

    private static int evaluateGamePhase(BitBoard board) {
        // Heuristik zur Bestimmung der Spielphase (1: Eröffnung, 2: Mittelspiel, 3: Endspiel)
        int totalPieces = board.countTotalPieces();
        if (totalPieces > 20) {
            return 1; // Eröffnung
        } else if (totalPieces > 10) {
            return 2; // Mittelspiel
        } else {
            return 3; // Endspiel
        }
    }

    private static long estimateRemainingMoves(BitBoard board) {
        //Heuristik zur Schätzung der verbleibenden Züge bis zum Spielende
        int totalPieces = board.countTotalPieces();
        if (totalPieces > 20) {
            return 40; // Schätzung für Eröffnung
        } else if (totalPieces > 10) {
            return 20; // Schätzung für Mittelspiel
        } else {
            return 10; // Schätzung für Endspiel
        }
    }


    // Minimax without cut-offs (not modified, can be used as is)
    private static int alphaBetaSearchWithoutCutoffs(BitBoard board, int depth, int alpha, int beta, boolean isRed, long startTime) {
       /* if (System.nanoTime() - startTime > TIME_LIMIT) {
            timeUp = true;
            return isRed ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }*/
        if (depth <= 0 || board.checkWinCondition() != BitBoard.WINNER_ONGOING) {
            return Evaluate.evaluateSimple(isRed, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red) -
                    Evaluate.evaluateSimple(!isRed, board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles, board.red_on_blue, board.blue_on_red);
        }

        List<String> legalMoves = board.getAllPossibleMoves(isRed);
        if (!isRed) {
            int maxEval = Integer.MIN_VALUE;
            for (String move : legalMoves) {
                //System.out.println("Move: " + move + ". Depth: " + depth + ". Bereits untersucht: " + untersuchteZustaende + ". Move für Red: " + isRed);
                untersuchteZustaende++;
                BitBoard newBoard = BitBoard.fromLongArray(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                        Tools.parseMove(move)[0], Tools.parseMove(move)[1], false,
                        board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                        board.red_on_blue, board.blue_on_red));
                int eval = alphaBetaSearchWithoutCutoffs(newBoard, depth - 1, alpha, beta, true, startTime);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                System.out.println("Move: " + move + ". Value: " + eval + ". Move für Red: " + isRed);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (String move : legalMoves) {
                //System.out.println("Move: " + move + ". Depth: " + depth + ". Bereits untersucht: " + untersuchteZustaende + ". Move für Red: " + isRed);
                untersuchteZustaende++;
                BitBoard newBoard = BitBoard.fromLongArray(BitBoardManipulation.doMoveAndReturnModifiedBitBoards(
                        Tools.parseMove(move)[0], Tools.parseMove(move)[1], true,
                        board.redSingles, board.blueSingles, board.redDoubles, board.blueDoubles,
                        board.red_on_blue, board.blue_on_red));
                int eval = alphaBetaSearchWithoutCutoffs(newBoard, depth - 1, alpha, beta, false, startTime);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                System.out.println("Move: " + move + ". Value: " + eval + ". Move für Red: " + isRed);
            }
            return minEval;
        }
    }
}
