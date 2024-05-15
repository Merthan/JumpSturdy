package model;

import java.io.IOException;
import java.util.Scanner;

public class Tools {

    public static final String RESET = "\u001B[0m";
    public static final String BLUE = "\u001B[34m";
    public static final String RED = "\u001B[31m";
    public static final String YELLOW = "\u001B[33m";
    public static final String WHITE = "\u001B[37m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String BOLD = "\u001B[1m";
    public static void printInColor(String text, boolean isRed) {
        System.out.println((isRed ? RED:BLUE) + text + RESET);
    }
    public static void printInColor(String text, String color) {
        System.out.println((color) + text + RESET);
    }


    public static String cleanMove(String move) {
        // Remove any whitespace and convert to uppercase
        move = move.trim().toUpperCase();
        if(move.length()==5){
            move = move.substring(0,2)+"-"+move.substring(3,5);
        }
        if (move.length() == 3) {
            // Insert a dash between the first and second characters
            move = move.substring(0,2) +"-"+move.charAt(0)+move.charAt(2) ;
        } else if (move.length() == 4) {
            // Insert a dash between the second and third characters
            move = move.substring(0, 2) + "-" + move.substring(2);
        }
        // No action needed for length == 5

        return move;
    }
    public static String bitboardToString(long bitboard) {
        StringBuilder sb = new StringBuilder();
        for (int i = 63; i >= 0; i--) {
            long mask = 1L << i;
            if ((bitboard & mask) != 0) {
                sb.append('1');
            } else {
                sb.append('0');
            }
        }
        return sb.toString();
    }

/*    public String cleanMove(String move){

    }*/

}

class ProgressBarExample { // Might be needed in the future for very pretty console stuff
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i <= 100; i++) {
            // Print the progress bar with percentage
            System.out.println("\r[" + getProgressBar(i) + "] " + i + "%");
            Thread.sleep(100);
        }
    }

    public static void clrscr(){
        //Clears Screen in java
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException | InterruptedException ex) {}
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    private static String getProgressBar(int percent) {
        int length = 20;
        int progress = (int) ((percent / 100.0) * length);
        StringBuilder progressBar = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            if (i < progress) {
                progressBar.append('=');
            } else {
                progressBar.append(' ');
            }
        }
        return progressBar.toString();
    }
}

class DynamicConsoleOutput {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.println("Hello, " + name + "!");
        System.out.println("Please wait...");

        // Simulate some processing
        try {
            for (int i = 1; i <= 10; i++) {
                Thread.sleep(500);
                // Clear the current line
                System.out.print("\033[2K"); // ANSI escape code to clear the current line
                // Move the cursor to the beginning of the line
                System.out.print("\r");
                System.out.print("Processing: " + i * 10 + "%");
            }
            System.out.println("\nProcessing complete!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Continue with user input
        System.out.print("Enter another input: ");
        String userInput = scanner.nextLine();
        System.out.println("You entered: " + userInput);

        scanner.close();
    }
}
