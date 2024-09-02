/**
* Author: Tong Zhu
* Student Id: 1368179 
* Email: tonzhu1@student.unimelb.edu.au
*/

import java.util.Scanner;

/**
 * The GameEngine class manages the main game logic for a Pacman-style game.
 * It handles maze selection, game play, game resumption, score viewing, and game completion.
 */
public class GameEngine {

    // The current maze instance
    private Maze maze;
    // Type of the maze
    private int mazeType;
    // Track if a game is currently in progress
    private boolean isGameInProgress = false;
    // Track if the game is paused
    private boolean isGamePaused = false;
    // Counter to track the number of games played
    private int gameCounter = 0;
    // List to keep track of completed games
    private GamerList gamers = new GamerList();
    // Current game instance
    private Gamer currentGame = null;
    // Single Scanner instance used throughout the class
    public static Scanner keyboard = new Scanner(System.in);

    public static void main(String[] args) {
        GameEngine engine = new GameEngine();
        engine.execute(args);
    }

    /**
     * Executes the game setup and menu operations based on command-line arguments.
     * @param args Command-line arguments: maze length, maze width, and seed value
     */
    private void execute(String[] args) {

        // Check if the correct number of command-line arguments is provided
        if (args.length != 3) {
            System.out.println("Invalid Inputs to set layout. Exiting the program now.");
            return;
        }

        try {
            int mazeLength = Integer.parseInt(args[0]);
            int mazeWidth = Integer.parseInt(args[1]);
            long seed = Long.parseLong(args[2]);

            // Validate inputs: maze length, maze width, and seed must be positive integers
            if (mazeLength <= 0 || mazeWidth <= 0 || seed <= 0) {
                System.out.println("Invalid Inputs to set layout. Exiting the program now.");
                return;
            }

            // Display the welcome message
            displayMessage();

            while (true) {
                displayMenu();
                System.out.print("> ");
                int option = keyboard.nextInt();

                switch (option) {
                    case 1:
                        selectMazeType(keyboard, mazeLength, mazeWidth, seed);
                        break;
                    case 2:
                        playGame();
                        break;
                    case 3:
                        resumeGame();
                        break;
                    case 4:
                        viewScores();
                        break;
                    case 5:
                        exitGame();
                        return;
                    default:
                        System.out.println("Invalid Input.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid input format. Maze length, width, and seed must be integers.");
        }
    }

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

    private void displayMenu() {
        System.out.println("Select an option to get started.");
        System.out.println("Press 1 to select a pacman maze type.");
        System.out.println("Press 2 to play the game.");
        System.out.println("Press 3 to resume the game.");
        System.out.println("Press 4 to view the scores.");
        System.out.println("Press 5 to exit.");
    }

     /**
     * Allows the user to select the type of maze and creates the maze.
     * @param scanner keyboard object for user input
     * @param mazeLength Length of the maze
     * @param mazeWidth Width of the maze
     * @param seed Seed value for the maze generation
     */
    private void selectMazeType(Scanner keyboard, int mazeLength, int mazeWidth, long seed) {
        while (true) {
            System.out.println("Please select a maze type.");
            System.out.println("Press 1 to select lower triangle maze.");
            System.out.println("Press 2 to select upper triangle maze.");
            System.out.println("Press 3 to select horizontal maze.");
            System.out.print("> ");
            mazeType = keyboard.nextInt();

            if (mazeType >= 1 && mazeType <= 3) {
                maze = new Maze(seed, mazeLength, mazeWidth, mazeType);
                System.out.println("Maze created. Proceed to play the game.");
                break;
            } else {
                System.out.println("Invalid Input.");
            }
        }
    }

    /**
     * Starts a new game if no game is in progress or if the previous game was discarded.
     */
    private void playGame() {
        if (maze == null) {
            // Scenario 1: Maze type not selected
            System.out.println("Maze not created. Select option 1 from main menu.");
        } else if (isGameInProgress || isGamePaused) {
            // Scenario 3: Previous game in progress
            System.out.println("Previous game hasn't ended yet. Do you want to discard previous game?");
            System.out.println("Press N to go back to main menu to resume the game or else press any key to discard.");

            System.out.print("> ");
            String userInput = keyboard.next();
            if (userInput.equalsIgnoreCase("N")) {
                return;
            } else {
                // Discard the previous game and start a new game
                isGameInProgress = false; // Reset the game in progress flag
                startNewGame(); // Start a new game
            }
        } else {
            // Scenario 2: Maze type selected and no game in progress
            startNewGame(); // Start a new game
        }
    }

    /**
     * Initializes a new game, increments the game counter, and starts game play.
     */
    private void startNewGame() {
        gameCounter++;
        currentGame = new Gamer(gameCounter);
        gamers.add(currentGame);

        // Set game in progress flag to true
        isGameInProgress = true;


        // Display game instructions and move menu
        System.out.println("Move the Pacman towards the food pellet.");
        System.out.println("  > You gain 20 points when Pacman get the food.");
        System.out.println("  > You lose 0.5 point when you hit the wall/boundary.");
        System.out.println("  > Score = 20 * Food - 0.5 * hits - 0.25 * moves.");

        // Print the maze to the user
        maze.printMaze();
        move();
    }

    /**
     * Resumes the game if it was paused. If no game is paused, notifies the user.
     */
    private void resumeGame() {
        if (maze == null) {
            // Scenario 1: Maze type not selected
            System.out.println("Maze not created. Select option 1 from main menu.");
        } else if (isGamePaused) {
            // Scenario 2: Resuming a paused game
            // Set game in progress flag to true
            isGameInProgress = true;
            // Reset the paused state
            isGamePaused = false;

            // Resume the game from the last saved state
            System.out.println("Restart your game from the last position you saved.");
            // Print the maze to show the current state of the game
            maze.printMaze();

            move();
        } else {
            // Scenario 3: No paused game found
            System.out.println("No paused game found. Select option 2 from the main menu to start a new game.");
        }
    }

    /**
     * Displays the scores for completed games.
     */
    private void viewScores() {
        if (gamers.size()==0) {
            System.out.println("No completed games found.");
            return;
        }

        System.out.printf("|%8s|%8s|%8s|%8s|%n", "# Game", "# Hits", "# Moves", "# Score");
        System.out.println("|========|========|========|========|");

        for (int i = 0; i < gamers.size(); i++) {
            Gamer game = gamers.get(i);
            System.out.printf("|%8d|%8d|%8d|%8.2f|%n", game.getId(), game.getHits(), game.getMoves(), game.getScore());
        }
    }

    /**
     * Completes the current game, calculates the score, and resets the game state.
     */
    public void completeCurrentGame() {
        if (currentGame != null) {
            this.isGameInProgress = false;
            this.isGamePaused = false;
            double score = 20 * currentGame.getFoods() - 0.5 * currentGame.getHits() - 0.25 * currentGame.getMoves();
            currentGame.setScore(score);
        }
    }

    /**
     * Exits the game with a goodbye message.
     */
    private void exitGame() {
        System.out.println("Pacman says - Bye Bye Player.");
    }

    /**
     * Handles user movement in the maze and updates the game state accordingly.
     */
    private void move() {
        while (isGameInProgress) {
            Maze.Cell pacmanCell = maze.getPacmanCell();
            int currentRow = pacmanCell.getRow();
            int currentCol = pacmanCell.getCol();
            int newRow = currentRow;
            int newCol = currentCol;

            System.out.println("Press W to move up.");
            System.out.println("Press A to move left.");
            System.out.println("Press S to move down.");
            System.out.println("Press D to move right.");
            System.out.println("Press Q to exit.");

            System.out.print("> ");
            String move = keyboard.next();
            switch (move.toUpperCase()) {
                case "W":
                    newRow = currentRow - 1;
                    break;
                case "A":
                    newCol = currentCol - 1;
                    break;
                case "S":
                    newRow = currentRow + 1;
                    break;
                case "D":
                    newCol = currentCol + 1;
                    break;
                case "Q":
                    // End the game
                    isGameInProgress = false;
                    isGamePaused = true;
                    System.out.println("Your game is paused and saved.");
                    break;
                default:
                    System.out.println("Invalid Input.");
                    // Print the maze to the user
                    maze.printMaze();
                    move();
                    return;
            }
            if(!isGamePaused) {
                moveAction(newRow, newCol);
            }
        }
    }

    /**
     * Executes the movement action based on the new position and updates the game state.
     * @param newRowPosMaze New row position in the maze
     * @param newColPosMaze New column position in the maze
     */
    private void moveAction(int newRowPosMaze, int newColPosMaze) {
        Maze.Cell currentCell = maze.getPacmanCell();
        Maze.Cell newCell = maze.getCell(newRowPosMaze, newColPosMaze);
        if (newCell.getValue() == '#') {
            currentGame.setHits(currentGame.getHits() + 1);
            System.out.println("You have hit the boundary.");
        } else if (newCell.getValue() == '-') {
            currentGame.setHits(currentGame.getHits() + 1);
            System.out.println("You have hit a wall.");
        } else {
            currentGame.setMoves(currentGame.getMoves() + 1);
            boolean isComplete=false;
            if (newCell.getValue() == '*') {
                currentGame.setFoods(currentGame.getFoods() + 1);
                this.completeCurrentGame();
                isComplete=true;
            }
            currentCell.setValue('.');
            newCell.setValue('P');
            if(maze!=null) {
                maze.setPacmanCell(newCell);
            }
            if(isComplete){
                maze.printMaze();
                this.maze=null;
                System.out.println("Game has ended! Your score for this game is " + currentGame.getScore());
            }
        }
        if(maze!=null) {
            maze.printMaze();
        }
    }

    /**
     * Node class used in the linked list to store Gamer objects.
     */
    class Node {
        // Gamer object stored in this node
        Gamer gamer;
        // Pointer to the next node in the list
        Node next;

        Node(Gamer gamer) {
            this.gamer = gamer;
            this.next = null;
        }
    }

    /**
     * GamerList class manages a linked list of Gamer objects.
     */
    class GamerList {
        // Head node of the linked list
        private Node head;
        // Tail node of the linked list
        private Node tail;

        public GamerList() {
            head = null;
            tail = null;
        }

        /**
         * Adds a Gamer to the list.
         * @param gamer Gamer object to be added
         */
        public void add(Gamer gamer) {
            Node newNode = new Node(gamer);
            if (head == null) {
                head = newNode;
                tail = newNode;
            } else {
                tail.next = newNode;
                tail = newNode;
            }
        }

        /**
         * Retrieves a Gamer from the list by index.
         * @param index Index of the Gamer to retrieve
         * @return Gamer object at the specified index, or null if out of bounds
         */
        public Gamer get(int index) {
            Node current = head;
            int count = 0;
            while (current != null) {
                if (count == index) {
                    return current.gamer;
                }
                count++;
                current = current.next;
            }
            return null; // Index out of bounds
        }

        /**
         * Gets the number of Gamers in the list.
         * @return Size of the list
         */
        public int size() {
            int size = 0;
            Node current = head;
            while (current != null) {
                size++;
                current = current.next;
            }
            return size;
        }
    }

    /**
     * Gamer class stores information about a single game, including ID, hits, moves, foods, and score.
     */
    class Gamer {
        // Unique ID for the Gamer
        private int id;
        // Number of hits
        private int hits;
        // Number of moves
        private int moves;
        // Number of foods collected
        private int foods;
        // Score of the game
        private double score;

        public Gamer(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public int getHits() {
            return hits;
        }

        public void setHits(int hits) {
            this.hits = hits;
        }

        public int getMoves() {
            return moves;
        }

        public void setMoves(int moves) {
            this.moves = moves;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public int getFoods() {
            return foods;
        }

        public void setFoods(int foods) {
            this.foods = foods;
        }
    }
}


