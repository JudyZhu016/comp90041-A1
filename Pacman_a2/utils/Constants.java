/**
* Author: Tong Zhu
* Student Id: 1368179 
* Email: tonzhu1@student.unimelb.edu.au
*/
package utils;

public class Constants {

    public static final String GAME_SCORE_HEADER_FORMAT = "|%8s|%15s|%15s|%16s|%8s|%8s|%8s|%n";
    public static final String GAME_SCORE_FORMAT = "|%8d|%15s|%15d|%16d|%8d|%8d|%8.2f|%n";

    public static final int SINGLE_PLAYER = 1;
    public static final int MULTIPLE_PLAYER = 2;
    public static final int QUIT_PLAYER = 3;

    public static final int SELECT_MAZE = 1;
    public static final int START_GAME = 2;
    public static final int RESUME_GAME = 3;
    public static final int VIEW_SCORES = 4;
    public static final int QUIT_MAIN = 5;

    public static final String MOVE_UP = "W";
    public static final String MOVE_DOWN ="S";
    public static final String MOVE_RIGHT = "D";
    public static final String MOVE_LEFT = "A";
    public static final String QUIT_MOVING = "Q";

    public static final int LOWER_TRIANGLE_MAZE = 1;
    public static final int UPPER_TRIANGLE_MAZE = 2;
    public static final int HORIZONTAL_TRIANGLE_MAZE = 3;

    public static final char MAZE_PACMAN = 'P';
    public static final char MAZE_BOUNDARY = '#';
    public static final char MAZE_WALL = '-';
    public static final char MAZE_PATH = '.';

    public static final double TOTAL_SCORE = 20.0;
    public static final double FOOD_MULTIPLIER = 5;
    public static final double MONSTER_MULTIPLIER = 10;
    public static final double HIT_MULTIPLIER = 0.5;
    public static final double MOVE_MULTIPLIER = 0.25;

    public static final int CMD_ARGS = 3;
    
}
