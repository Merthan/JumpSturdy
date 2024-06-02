package misc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import ai.SearchType;
import ai.SturdyJumpersAI;
import com.google.gson.Gson;
import model.BitBoard;


public class ServerConnection {



    static Gson gson = new Gson();  // Create a Gson object for JSON parsing

    static String processInput(String gameStateJson) {
        GameState gameState = gson.fromJson(gameStateJson, GameState.class);  // Convert JSON string to GameState object
        BitBoard board = new BitBoard(gameState.fen);  // Create a new BitBoard from the FEN string in GameState

        boolean isRedTurn = gameState.currentPlayer.equals("r");

        //best move by AI Bot
        String bestMove = SturdyJumpersAI.findBestMove(SearchType.ALPHABETA, board, isRedTurn);

        return bestMove;  // Return the best move
    }

    // Class to represent the game state received from Python
    static class GameState {
        String fen;
        String currentPlayer;
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("AI Server started. Waiting for connection...");
        Socket clientSocket = serverSocket.accept();
        System.out.println("Connected to Python client!");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // Process the game state and compute the move
                String move = processInput(inputLine);
                out.println(move);
            }
        }
        System.out.println("Connection closed.");
        serverSocket.close();
    }
}
