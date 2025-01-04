/**
* Author: Tong Zhu
* Student Id: 1368179 
* Email: tonzhu1@student.unimelb.edu.au
*/

/**
 * This class represents the scoreboard for the Pacman game. It maintains a list of game scores
 * and provides methods to add new scores, resize the score list when needed, and retrieve 
 * all scores as a formatted string.
 */

package Game;

import utils.Constants;

public class ScoreBoard {
    // Array to store scores
    private String[] scores;
    // Counter to keep track of the number of games played
    private int gameCounter;

    /**
     * Constructor to initialize the scoreboard.
     * Initializes the score array with an initial size of 10 and sets the game counter to 0.
     */
    public ScoreBoard() {
        // Initialize the array with a reasonable size. Resize when needed.
        this.scores = new String[10]; // Initial size of 10
        this.gameCounter = 0;
    }

    /**
     * Adds a game score to the scoreboard.
     * Resizes the score array if it is full before adding the new score.
     * 
     * @param score The score information to be added to the scoreboard.
     */
    public void addGameScore(String score) {
        // Resize array if needed
        if (gameCounter >= scores.length) {
            resizeArray();
        }
        this.scores[gameCounter] = score;
        this.gameCounter++;
    }

    /**
     * Resizes the score array to accommodate more scores.
     * Doubles the size of the current array and copies the existing scores to the new array.
     */
    private void resizeArray() {
        String[] newScores = new String[scores.length * 2]; 
        System.arraycopy(scores, 0, newScores, 0, scores.length);
        this.scores = newScores;
    }

     /**
     * Retrieves all completed game scores as a formatted string.
     * If no games have been completed, a message indicating so is returned.
     * 
     * @return A formatted string representing the scoreboard, including all game scores.
     */
    public String getAllScores() {
        // Create a StringBuilder to build the scoreboard output
        StringBuilder scoreBoardOutput = new StringBuilder();

        if (gameCounter == 0) {
            scoreBoardOutput.append("No completed games found.\n");
        } else {
            // Print the header only once
            scoreBoardOutput.append(String.format(Constants.GAME_SCORE_HEADER_FORMAT, "# Game","Player Name","# Food Eaten","# Monster Killed","# Hits","# Moves", "# Score"));
            scoreBoardOutput.append("|========|===============|===============|================|========|========|========|\n");
            
            // Append each game's score
            for (int i = 0; i < gameCounter; i++) {
                scoreBoardOutput.append(scores[i]);
            }
        }

        // Return the full scoreboard string
        return scoreBoardOutput.toString();

    }
}
