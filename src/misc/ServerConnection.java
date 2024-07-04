package misc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import ai.MerthanAlphaBetaExperiment;
import model.BitBoard;

public class ServerConnection {

    public static int ZEIT = 2000;

    static MerthanAlphaBetaExperiment merthanAlphaBetaExperiment=null; //Preserved because of zobrist, Transposition table etc

    //TODO: before submitting for 2nd contest, make sure all flags have the correct values (logs=false, saveSequence false, transpo true sort true)
    public static void main(String[] args) {
        merthanAlphaBetaExperiment = new MerthanAlphaBetaExperiment();
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
                        // Inlined now, no need for a method for this little logic/2 theoretical lines
                        BitBoard board = new BitBoard(fen.substring(0, fen.length() - 2));//TODO: Remis handling
                        String move = Tools.parseMoveToString(merthanAlphaBetaExperiment.findBestMove(board, fen.charAt(fen.length()-1) == 'r', ZEIT).get(0));

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

/*    private static boolean decide_player(String fen) { Not necessary, why would we handle 2 exceptions here unless ChatGPT told us to. Server should send proper strings and this is checked anyways
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
    }*/
}