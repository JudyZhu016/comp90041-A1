/**
* Author: Tong Zhu
* Student Id: 1368179 
* Email: tonzhu1@student.unimelb.edu.au
*/

/**
 * This is the main game engine class for the Pacman game.
 * It controls the flow of the game, including player selection,
 * game mode (single or multiplayer), game start, resume, and score viewing.
 * 
 * WARNING: This class shouldn't be moved to any package.
 */

import Entities.Food;
import Entities.FoodType;
import Entities.Monster;
import Entities.MonsterType;
import Game.Maze;
import Game.LocationGenerator;
import Game.ScoreBoard;
import utils.Constants;

import java.util.Scanner;

public class GameEngine {

    // A shared scanner for input, to be used throughout the game.
    public static Scanner keyboard = new Scanner(System.in);
    
    // Tracks if the game is in multiplayer mode
    public static boolean isMultiplayer = false;  
    // Tracks the current player (0 for Player 1, 1 for Player 2)
    public static int currentPlayer = 0; 

    // ScoreBoard instance to keep track of game scores.
    private ScoreBoard scoreBoard = new ScoreBoard();

    // 
    private static int gameID = 1;

    /**
     * The main entry point for the game.
     * 
     * @param args Command line arguments for maze setup.
     */
    public  static void main(String[] args){

        GameEngine engine = new GameEngine();

        if(engine.isInvalidArgs(args)){
            //print invalid args message
            System.out.println("Invalid Inputs to set layout. Exiting the program now.");
        }else {
            engine.displayMessage();
            // write rest of the code here.
            engine.runMenu(args);
            
        }
    }

    /**
     * Displays the main menu for the player to choose game mode.
     * 
     * @param args Command line arguments for maze setup.
     */
    private void runMenu(String[] args) {
        boolean exit = false;

        while(!exit) {
            selectPlayer();
            int option = keyboard.nextInt();

            switch (option) {
                case Constants.SINGLE_PLAYER:
                    chooseMenu(args, false);
                    break;
                case Constants.MULTIPLE_PLAYER:
                    chooseMenu(args, true);
                    break;
                case Constants.QUIT_PLAYER:
                    System.out.println("Pacman says - Bye Bye Player.");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid Input.");
            }

        }
    }

    /**
     * Handles the game logic for single and multiplayer modes.
     * 
     * @param args Command line arguments for maze setup.
     * @param isMultiplayer Indicates if the game is in multiplayer mode.
     */
    private void chooseMenu(String[] args, boolean isMultiplayer) {
        boolean backToPlayer = false;
        int mazeLength = Integer.parseInt(args[0]);
        int mazeWidth = Integer.parseInt(args[1]);
        long seed = Long.parseLong(args[2]);

        int mazeType = 0; // initialize mazeType
        Maze[] mazeArray = new Maze[2]; // Array to store mazes for Player 1 and Player 2
        Maze mazeSinglePlayer = null; // Maze for single-player mode
        int currentPlayer = 0; // 0 for Player 1, 1 for Player 2

        // To track if the game is in multiplayer mode
        boolean multiPlayerMode = isMultiplayer;
        // differentiate between selecting the maze type and a new game after discarding the old one
        boolean mazeCreatedThroughDiscard = false; 

        // Initialize scoreboard
        ScoreBoard scoreBoard = new ScoreBoard();

        while (!backToPlayer) {
            printMenu();
            int menuChoice = keyboard.nextInt();
            
            switch (menuChoice) {
                case Constants.SELECT_MAZE:
                    mazeType = selectMazeType();
                    if (multiPlayerMode) {
                        mazeArray[0] = new Maze(mazeType, mazeLength, mazeWidth, seed); // Maze for Player 1
                        mazeArray[1] = new Maze(mazeType, mazeLength, mazeWidth, seed); // Maze for Player 2
                        currentPlayer = 0; // Reset currentPlayer to 0 for a new game session
                        // Increment gameID only once for the multiplayer game
                        if (currentPlayer == 0) {
                            gameID++;
                        }
                    } else {
                        mazeSinglePlayer = new Maze(mazeType, mazeLength, mazeWidth, seed);
                        // Increment gameID for single-player
                        gameID++;
                    }
                    System.out.println("Maze created. Proceed to play the game.");
                    mazeCreatedThroughDiscard = false;
                    break;

                case Constants.START_GAME:
                    if (multiPlayerMode){
                        // Multiplayer Mode
                        if ((mazeArray[0] == null || mazeArray[0].isGameFinished()) && 
                            (mazeArray[1] == null || mazeArray[1].isGameFinished())) {
                            System.out.println("Maze not created. Select option 1 from main menu.");
                        
                        // Scenario 3: Previous game in progress
                        } else if (mazeArray[0].isGameGoing() || mazeArray[1].isGameGoing()) {
                            System.out.println("Previous game hasn't ended yet. Do you want to discard previous game?");
                            System.out.println("Press N to go back to main menu to resume the game or else press any key to discard.");
                            System.out.print("> ");
                            String userInput = keyboard.next();

                            if (userInput.equalsIgnoreCase("N")) {
                                break;
                            } else {
                                // Discard the previous game and start a new game
                                mazeType = selectMazeType();
                                mazeArray[0] = new Maze(mazeType, mazeLength, mazeWidth, seed);
                                mazeArray[1] = new Maze(mazeType, mazeLength, mazeWidth, seed);
                                mazeCreatedThroughDiscard = true;
                                System.out.println("New maze created. Proceed to play the game.");
                                // Start the game immediately for Player 1
                                mazeArray[0].startGame("Player 1");
                                break;
                            }

                        // Scenario 2: Maze type selected new game started
                        } else {
                            // Normal flow for multiplayer game
                            mazeArray[currentPlayer].startGame("Player " + (currentPlayer + 1));
                            if (!mazeArray[currentPlayer].isGameGoing()) {
                                currentPlayer++; // Move to next player
                                // Check if there are more players to play
                                if (currentPlayer < 2) {
                                    // Start the next player's game (Player 2)
                                    mazeArray[currentPlayer].startGame("Player " + (currentPlayer + 1));
                                } else {
                                    // Both players have finished, determine the winner
                                    if (mazeArray[0].calculateScore() >= mazeArray[1].calculateScore()) {
                                        scoreBoard.addGameScore(mazeArray[0].getScoreInfo(gameID, "Player 1"));
                                        System.out.println("Player 1 wins. Returning to main menu.");
                                    } else {
                                        scoreBoard.addGameScore(mazeArray[1].getScoreInfo(gameID, "Player 2"));
                                        System.out.println("Player 2 wins. Returning to main menu.");
                                    }
                                    // Both players have finished, determine the winner
                                    mazeArray[currentPlayer] = null;
                                    currentPlayer = 0;
                                }
                            }
                        }

                    } else {
                        // Single Player Mode
                        if (mazeSinglePlayer == null || mazeSinglePlayer.isGameFinished()) {
                            // Scenario 1: Maze type not selected
                            System.out.println("Maze not created. Select option 1 from main menu."); 
                        } else if (mazeSinglePlayer != null && mazeSinglePlayer.isGameGoing()) {
                            // Scenario 3: Previous game in progress
                            System.out.println("Previous game hasn't ended yet. Do you want to discard previous game?");
                            System.out.println("Press N to go back to main menu to resume the game or else press any key to discard.");
                            System.out.print("> ");
                            String userInput = keyboard.next();

                            if (userInput.equalsIgnoreCase("N")) {
                                break;
                            } else {
                                // Discard the previous game and start a new game
                                mazeType = selectMazeType();
                                //Create new maze
                                mazeSinglePlayer = new Maze(mazeType, mazeLength, mazeWidth, seed); 
                                mazeCreatedThroughDiscard = true;
                                System.out.println("New maze created. Proceed to play the game."); // Print only if created through discard 
                                mazeSinglePlayer.startGame("Player 1"); // Start a new game
                                break;
                            }
                        } else {
                            // Scenario 2: Maze type selected new game started
                            mazeSinglePlayer.startGame("Player 1");

                            // Add game completion handling
                            if (!mazeSinglePlayer.isGameGoing()) {
                                // Game ended naturally
                                String scoreInfo = mazeSinglePlayer.getScoreInfo(gameID, "Player 1"); // Calculate the score
                                scoreBoard.addGameScore(scoreInfo);
                                
                            }
                        }
                    }
                    break;

                case Constants.RESUME_GAME:
                    if (multiPlayerMode) {
                        // Multiplayer Mode: Resume for the current player
                        if ((mazeArray[0] == null || mazeArray[0].isGameFinished()) && 
                            (mazeArray[1] == null || mazeArray[1].isGameFinished())) {
                            System.out.println("Maze not created. Select option 1 from main menu.");
                        }
                        else if (mazeArray[currentPlayer] != null && mazeArray[currentPlayer].isGameGoing()) {
                            mazeArray[currentPlayer].resumeGame("Player " + (currentPlayer + 1));

                            if (!mazeArray[currentPlayer].isGameGoing()) {

                                currentPlayer++; // Move to next player
                                // Check if there are more players to play
                                if (currentPlayer < 2) {
                                    // Start the next player's game (Player 2)
                                    mazeArray[currentPlayer].startGame("Player " + (currentPlayer + 1));
                                } else {
                                    // Both players have finished, determine the winner
                                    if (mazeArray[0].calculateScore() >= mazeArray[1].calculateScore()) {
                                        scoreBoard.addGameScore(mazeArray[0].getScoreInfo(gameID, "Player 1"));
                                        System.out.println("Player 1 wins. Returning to main menu.");
                                    } else {
                                        scoreBoard.addGameScore(mazeArray[1].getScoreInfo(gameID, "Player 2"));
                                        System.out.println("Player 2 wins. Returning to main menu.");
                                    }
                                    // Both players have finished, determine the winner
                                    mazeArray[currentPlayer] = null;
                                    currentPlayer = 0;
                                }
                            }
                        
                        } else {
                            System.out.println("No paused game found. Select option 2 from main menu to start a new game.");
                        }

                    } else {
                        // Single Player Mode
                        // check if mazeType is selected, else show error messages.
                        // check if game is ongoing
                        if (mazeSinglePlayer == null) {
                            // Scenario 1: Maze type not selected
                            System.out.println("Maze not created. Select option 1 from main menu.");
                        } else if (mazeSinglePlayer != null && mazeSinglePlayer.isGameGoing()) {
                            // Scenario 2: Resuming a paused game
                            mazeSinglePlayer.resumeGame("Player 1");

                            if (!mazeSinglePlayer.isGameGoing()) {
                                // Game ended naturally
                                String scoreInfo = mazeSinglePlayer.getScoreInfo(gameID, "Player 1"); // Calculate the score
                                scoreBoard.addGameScore(scoreInfo);
                            }

                        } else {
                            // Scenario 3: No paused game found
                            System.out.println("No paused game found. Select option 2 from main menu to start a new game.");
                        }
                    }

                    break;

                case Constants.VIEW_SCORES:
                    System.out.print(scoreBoard.getAllScores());
                    break;

                case Constants.QUIT_MAIN:
                    System.out.println("Exiting main menu, return to Player Selection.");
                    backToPlayer = true;
                    break;
                default:
                    System.out.println("Invalid Input.");
            }
        }
    }

    /**
     * Prompts the player to select a maze type.
     * 
     * @return The selected maze type.
     */
    private int selectMazeType() {
        int mazeType = 0;
        boolean validInput = false;
        
        while (!validInput) {
            System.out.println("Please select a maze type.");
            System.out.println("Press 1 to select lower triangle maze.");
            System.out.println("Press 2 to select upper triangle maze.");
            System.out.println("Press 3 to select horizontal maze.");
            System.out.print("> ");

            mazeType = keyboard.nextInt();
            
            if(mazeType > 0 && mazeType <= Constants.HORIZONTAL_TRIANGLE_MAZE) {
                validInput = true;
            } else {
                System.out.println("Invalid Input.");
            }
        }
        
        return mazeType;

    }

    private void playGame(Maze maze, String playerName) {
        boolean gameIsRunning = true;
        while (gameIsRunning) {
            maze.printMaze();
            movePacmanMenu();

            String input = keyboard.next();
            if (input.equalsIgnoreCase(Constants.QUIT_MOVING)) {
                System.out.println("Your game is paused and saved.");
                gameIsRunning = false; // Quit the game
            } else {
                boolean validMove = maze.movePacman(input);
                if (!validMove) {
                    System.out.println("Invalid Input.");
                }

                if (maze.isGameOver()) {
                    String scoreInfo = maze.getScoreInfo(playerName, gameID);
                    System.out.println(scoreInfo);
                    scoreBoard.addGameScore(scoreInfo);
                    gameIsRunning = false; // Game over
                }
            }
        }
    }

    /**
     * Prompts the player to select the game mode.
     */
    private void selectPlayer() {
        System.out.println("Make player selection.");
        System.out.println("Press 1 for Single Player.");
        System.out.println("Press 2 for Multi Player.");
        System.out.println("Press 3 to exit.");
        System.out.print("> ");
    }

    /**
     * Displays the game menu.
     */
    private void printMenu() {
        System.out.println("Select an option to get started.");
        System.out.println("Press 1 to select a pacman maze type.");
        System.out.println("Press 2 to play the game.");
        System.out.println("Press 3 to resume the game.");
        System.out.println("Press 4 to view the scores.");
        System.out.println("Press 5 to exit.");
        System.out.print("> ");
    }

    /**
     * Displays the movement options for Pacman.
     */
    private void movePacmanMenu(){
        System.out.println("Press W to move up.");
        System.out.println("Press A to move left.");
        System.out.println("Press S to move down.");
        System.out.println("Press D to move right.");
        System.out.println("Press Q to exit.");
        System.out.print("> ");
    }  

    /**
     * Checks if the provided command line arguments are valid.
     * 
     * @param args Command line arguments for maze setup.
     * @return True if the arguments are invalid, otherwise false.
     */
    private boolean isInvalidArgs(String[] args) {
        boolean invalidInput = false;
        if(args.length != Constants.CMD_ARGS){
            invalidInput = true;
        }
        for(int i = 0; i< args.length; i++){
            if(Integer.parseInt(args[i]) <=0){
                invalidInput = true;
            }
        }
        return  invalidInput;
    }

    /**
     * Displays a welcome message at the start of the game.
     */
    private void displayMessage() {
        System.out.println(" ____         __          ___        _  _         __         __ _ \n" +
                "(  _ \\       / _\\        / __)      ( \\/ )       / _\\       (  ( \\\n" +
                " ) __/      /    \\      ( (__       / \\/ \\      /    \\      /    /\n" +
                "(__)        \\_/\\_/       \\___)      \\_)(_/      \\_/\\_/      \\_)__)");
        System.out.println("");
        System.out.println("Let the fun begin");
        System.out.println("(`<    ...   ...  ...  ..........  ...");
        System.out.println("");
    }


}

