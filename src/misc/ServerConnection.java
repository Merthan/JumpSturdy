package misc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import ai.MerthanAlphaBetaExperiment;
import ai.SearchType;
import ai.SturdyJumpersAI;
import misc.Tools;
import model.BitBoard;

public class ServerConnection {

    public static int ZEIT = 2000;


    public static void main(String[] args) {
        System.out.println("-------Starting Server Connection------\n\n\n");
        String serverAddress = "localhost";
        int port = 5065; // The port on which the Python server is listening

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();  // Correct method call
                System.out.println("New client connected.");

                try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

                    System.out.println("Waiting to receive FEN...");
                    String fen = input.readLine(); // Correctly reads from the client
                    System.out.println("Received FEN: " + fen);

                    if (fen != null && !fen.isEmpty()) {
                        // Process the FEN and decide on the move
                        String move = processFEN(fen);

                        // Send the move back to the client
                        output.println(move);
                        System.out.println("Sent move: " + move);
                    }
                } catch (Exception e) {
                    System.out.println("Error during communication: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    socket.close();  // Ensure socket is closed after handling
                }
            }
        } catch (Exception e) {
            System.out.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String processFEN(String fen) {

        boolean isRed = decide_player(fen);
        String cleanFEN = fen.substring(0, fen.length() - 2);

        BitBoard board = new BitBoard(cleanFEN);
        String bestMove = Tools.parseMoveToString(new MerthanAlphaBetaExperiment().findBestMove(board, isRed, ZEIT).get(0));
        return bestMove;
    }

    private static boolean decide_player(String fen) {
        if (fen == null || fen.isEmpty()) {
            throw new IllegalArgumentException("Invalid FEN string");
        }

        char lastChar = fen.charAt(fen.length() - 1);

        if (lastChar == 'r') {
            return true;
        } else if (lastChar == 'b') {
            return false;
        } else {
            throw new IllegalArgumentException("FEN string ungültig");
        }
    }
}