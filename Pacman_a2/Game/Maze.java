/**
* Author: Tong Zhu
* Student Id: 1368179 
* Email: tonzhu1@student.unimelb.edu.au
*/

/**
 * This class represents the maze used in the Pacman game. It contains the layout of the maze, 
 * the position of Pacman, monsters, and food items, as well as game-related logic such as 
 * movements, scoring, and the status of the game.
 */

package Game;

import Entities.Food;
import Entities.FoodType;
import Entities.Monster;
import Entities.MonsterType;
import utils.Constants;

public class Maze {

    /* maze details */
    private int mazeType;
    private int mazeLength;
    private int mazeWidth;
    private char[][] maze;

    /*pacman position*/
    private int colPosPacman; 
    private int rowPosPacman;

    /*monster and foods positions*/
    private int rowPos;
    private int colPos;
    
    /*Using enums for monsters and foods*/
    private Monster[] monsters = new Monster[4];
    private Food[] foods = new Food[4];

    /*score related attributes*/
    private int numOfFood;
    private int numOfMonster;
    private int numOfHits;
    private int numOfMoves;
    private int superpower;

    private boolean isAlive;
    private boolean isGameGoing;
    private boolean isGameFinished;
    private boolean areAllMonstersKilled;

    /**
     * Constructor to initialize the maze with specified dimensions, type, and a random seed.
     * 
     * @param mazeType   The type of maze to generate.
     * @param mazeLength The length of the maze.
     * @param mazeWidth  The width of the maze.
     * @param seed       A seed for random number generation to ensure repeatable maze layouts.
     */
    public Maze(int mazeType, int mazeLength, int mazeWidth, long seed) {
        this.mazeType = mazeType;
        this.mazeLength = mazeLength;
        this.mazeWidth = mazeWidth;
        // Pacman starting position
        this.colPosPacman = 1;
        this.rowPosPacman = 1;

        // Initialize monsters with enums
        this.monsters[0] = new Monster(MonsterType.RED);
        this.monsters[1] = new Monster(MonsterType.BLUE);
        this.monsters[2] = new Monster(MonsterType.GREEN);
        this.monsters[3] = new Monster(MonsterType.YELLOW);

        // Initialize special foods with enums
        for (int i = 0; i < 4; i++) {
            this.foods[i] = new Food(FoodType.SPECIAL);
        }

        this.numOfFood = 0;
        this.numOfMonster = 0;
        this.numOfHits = 0;
        this.numOfMoves = 0;
        this.superpower = 0;

        this.isGameGoing = false;
        this.isGameFinished = false;
        this.isAlive = true;
        this.areAllMonstersKilled = false;

        // initialize the maze: the rows and columns of the maze are defined by mazeWidth and mazeLength.
        this.maze = new char[mazeWidth][mazeLength]; 

        // fill the maze 
        fillMaze(maze);

        // add the position of all the entities
        LocationGenerator generator = new LocationGenerator(seed);
        generatePos(generator);
        
    }

    /**
     * Starts the game for the specified player.
     * 
     * @param playerName The name of the player starting the game.
     */
    public void startGame(String playerName) {
        System.out.println("Move the Pacman towards the food pellet and gain super power to kill monsters.");
        System.out.println("  > You gain 20 points if Pacman finishes the game without dying.");
        System.out.println("  > You gain 10 more points for every monster you killed.");
        System.out.println("  > You gain 5 points for every special food that you have eaten.");
        System.out.println("  > You lose 0.5 point when you hit the wall/boundary.");
        System.out.println("  > You lose 0.25 points for every move.");
        System.out.println("  > Score = 5 * foodEaten + 10 * monsterKilled - 0.5 * numOfHits - 0.25 * numOfMoves  +  20 if not dead.");
        System.out.println(playerName + " game begins.");
        
        this.isGameGoing = true;

        moveAction();
    }

    /**
     * Resumes the game for the specified player from the last saved position.
     * 
     * @param playerName The name of the player resuming the game.
     */
    public void resumeGame(String playerName) {
        System.out.println("Restart your game from the last position you saved.");
        System.out.println(playerName + " game begins.");

        moveAction();
    }


    /**
     * Calculates the score for the current game.
     * 
     * @return The calculated score.
     */
    public double calculateScore() {
        // Calculate base score
        double score = Constants.FOOD_MULTIPLIER * numOfFood + 
                    Constants.MONSTER_MULTIPLIER * numOfMonster - 
                    Constants.HIT_MULTIPLIER * numOfHits - 
                    Constants.MOVE_MULTIPLIER * numOfMoves;

        // Add bonus if Pacman is alive and all monsters are killed
        if (!this.isGameGoing && isAlive && areAllMonstersKilled()) {
            score += Constants.TOTAL_SCORE; // Add bonus 20 points
        }

        return score;
    }

    /**
     * Generates a formatted string containing the score information for the current game.
     * 
     * @param playerName The name of the player.
     * @return A formatted string with game and score information.
     */
    public String getScoreInfo(int gameID, String playerName) {
        double score = calculateScore();

        // Return the formatted score information
        return String.format(Constants.GAME_SCORE_FORMAT, gameID, playerName, numOfFood, numOfMonster, numOfHits, numOfMoves, score);
    }

    /**
     * Checks if the game is currently in progress.
     * 
     * @return True if the game is in progress, false otherwise.
     */
    public boolean isGameGoing() {
        return isGameGoing;
    }

    /**
     * Checks if the game has finished.
     * 
     * @return True if the game has finished, false otherwise.
     */
    public boolean isGameFinished() {
        return isGameFinished;
    }

    /**
     * Fills the maze with boundaries, walls, and paths based on the maze type.
     * 
     * @param maze The maze to be filled.
     */
    private void fillMaze(char[][] maze) {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                if (i == 0 || i == maze.length - 1 || j == 0 || j == maze[i].length - 1) {
                    maze[i][j] = Constants.MAZE_BOUNDARY; // Set boundary
                } else if (i == rowPosPacman && j == colPosPacman) {
                    maze[i][j] = Constants.MAZE_PACMAN; // Pacman starting position
                } else if ((mazeType == Constants.LOWER_TRIANGLE_MAZE && j > i) || 
                           (mazeType == Constants.UPPER_TRIANGLE_MAZE && j < i) || 
                           (mazeType == Constants.HORIZONTAL_TRIANGLE_MAZE && (i%2 == 0 && j!= 1 && j!= maze[i].length - 2))) {
                    maze[i][j] = Constants.MAZE_WALL; // Set walls based on maze type
                } else {
                    maze[i][j] = Constants.MAZE_PATH; // Path
                }
            }
        }
    }

    /**
     * Generates a position for an entity (monster or food) in the maze.
     * 
     * @param generator The location generator used to determine random positions.
     * @param symbol The symbol representing the entity in the maze.
     */
    private void generatePosition(LocationGenerator generator, char symbol) {
        while (true) {
            int colPos = generator.generatePosition(1, this.mazeLength-2);
            int rowPos = generator.generatePosition(2, this.mazeWidth-2);

            if (this.maze[rowPos][colPos] == '.') {
                // Set the Monster or foods location
                this.colPos = colPos;
                this.rowPos = rowPos;
                // set the position of monster or food in maze, set it with correct symbol
                this.maze[rowPos][colPos] = symbol;
                break;
            }
        }
    }

    /**
     * Generates positions for all monsters and foods in the maze.
     * 
     * @param generator The location generator used to determine random positions.
     */
    private void generatePos(LocationGenerator generator) {
        // Generate positions for monsters
        for (int i = 0; i < this.monsters.length; i++) {
            Monster currentMonster = this.monsters[i];
            generatePosition(generator, currentMonster.getSymbol());
            // Set monster's position
            currentMonster.setPos(this.rowPos, this.colPos);
        }

        // Generate positions for special foods
        for (int i = 0; i < this.foods.length; i++) {
            Food currentFood = this.foods[i];
            generatePosition(generator, currentFood.getSymbol());
            // Set food's position 
            currentFood.setPos(this.rowPos, this.colPos);
        }
        
    }

    /**
     * Prints the current state of the maze to the console.
     * 
     * @param maze The maze to be printed.
     */
    private void printMaze(char[][] maze) {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                System.out.print(maze[i][j]);
            }
            System.out.println();
        }
    }

    /**
     * Handles the game actions, such as moving Pacman and checking game status.
     */
    private void moveAction() {
        boolean exit = false;
        while(!exit) {
            printMaze(this.maze);
            movePacmanMenu();
            String input = GameEngine.keyboard.next();

            switch (input.toUpperCase()){
                case Constants.MOVE_UP:
                case Constants.MOVE_LEFT:
                case Constants.MOVE_DOWN:
                case Constants.MOVE_RIGHT:

                    move(input.toUpperCase());
                    // Check if the game has ended 
                    if(!isAlive || areAllMonstersKilled()){
                        isGameGoing = false;
                        isGameFinished = true;
                        this.maze = null;
                        System.out.println("Game has ended! Your score for this game is " + calculateScore());

                        
                        exit = true;
                        
                    }
                    break;
                // Exit the movement menu
                case Constants.QUIT_MOVING:
                    System.out.println("Your game is paused and saved.");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid Input.");
            }
        }
    }

    /**
     * Moves Pacman in the specified direction and updates the game state accordingly.
     * 
     * @param direction The direction in which to move Pacman.
     */
    private void move(String direction) {
        int newRow = this.rowPosPacman;
        int newCol = this.colPosPacman;

        switch(direction){
            case Constants.MOVE_UP:
                newRow--;
                break;
            case Constants.MOVE_DOWN:
                newRow++;
                break;
            case Constants.MOVE_LEFT:
                newCol--;
                break;
            case Constants.MOVE_RIGHT:
                newCol++;
                break;
            default:
                break;
        }

        // check for invalid moves
        if(this.maze[newRow][newCol] == Constants.MAZE_WALL) {
            System.out.println("You have hit a wall.");
            numOfHits++;
            return;
        }

        if(this.maze[newRow][newCol] == Constants.MAZE_BOUNDARY) {
            System.out.println("You have hit the boundary.");
            numOfHits++;
            return;
        }

        // Handle special moves and check if Pacman is alive after the move
        specialMove(newRow, newCol);

        // If Pacman is still alive after the move
        if (isAlive) {
            maze[rowPosPacman][colPosPacman] = Constants.MAZE_PATH; // Set the current position to empty
            rowPosPacman = newRow;
            colPosPacman = newCol;
            maze[newRow][newCol] = Constants.MAZE_PACMAN; // Set Pacman's new position
            numOfMoves++; // Increment the number of moves
        }
    }

    /**
     * Handles special actions such as eating food and fighting monsters when Pacman moves to a new cell.
     * 
     * @param newRow The row position Pacman moves to.
     * @param newCol The column position Pacman moves to.
     */
    private void specialMove(int newRow, int newCol) {
        char newMaze = maze[newRow][newCol];

        // Eating a special food
        if (newMaze == FoodType.SPECIAL.getSymbol()) {
            System.out.println("Power up!");
            numOfFood++;
            superpower++;
        }
        // Killing a monster
        else if (isMonsterSymbol(newMaze)) {
            if (superpower > 0) {
                System.out.println("Hurray! A monster is killed.");
                numOfMonster++;
                superpower--; // Decrease superpower
            } else {
                System.out.println("Boo! Monster killed Pacman.");
                isAlive = false; // Pacman dies
            }
        }
    }

    /**
     * Check if a character corresponds to any monster type.
     * 
     * @param symbol The symbol to check.
     * @return True if the symbol corresponds to a monster, false otherwise.
     */
    private boolean isMonsterSymbol(char symbol) {
        for (MonsterType type : MonsterType.values()) {
            if (type.getSymbol() == symbol) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all monsters in the maze have been killed.
     * 
     * @return True if all monsters are killed, false otherwise.
     */
    private boolean areAllMonstersKilled() {
        return numOfMonster == monsters.length;
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
}

