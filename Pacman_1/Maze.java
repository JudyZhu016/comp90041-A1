/**
* Author: Tong Zhu
* Student Id: 1368179 
* Email: tonzhu1@student.unimelb.edu.au
*/

public class Maze {
    /* maze details */
    /* Represent the maze using a grid of Cell objects */
    // The starting cell of the maze (top-left corner)
    private Cell startCell;
    // The cell where Pacman is currently located
    private Cell pacmanCell;
    // The cell where the food is located
    private Cell foodCell;


    /*food position*/
    private int colPosFood;
    private int rowPosFood;

    /*score/game related attributes*/


    //write a constructor for Maze that invokes the generateFood method with appropriate params if 0 < mazeType < 4.
    // this generate position of the special food  

    /**
     * Constructor to initialize the maze and generate food based on maze type.
     *
     * @param seed       the seed value for the food generator
     * @param mazeLength the length of the maze
     * @param mazeWidth  the width of the maze
     * @param mazeType   the type of the maze, determines food placement rules
     */
    public Maze(long seed, int mazeLength, int mazeWidth, int mazeType) {
        generateFood(seed, mazeLength, mazeWidth, mazeType);
        createMaze(mazeLength, mazeWidth, mazeType);
    }


    //DO NOT MODIFY THIS CODE
    private void generateFood(long seed, int mazeLength, int mazeWidth, int mazeType) {
        FoodGenerator generator = new FoodGenerator(seed);
        while(true) {
            int xFood = generator.generatePosition(1, mazeLength-2);
            int yFood = generator.generatePosition(2, mazeWidth-2);
            if ((mazeType == 1 && xFood <= yFood) || (mazeType ==2 && xFood >=yFood) || (mazeType ==3 && !(yFood%2 == 0 && xFood!= 1 && xFood!= mazeLength-2))){
                this.colPosFood = xFood;
                this.rowPosFood = yFood;
                break;
            }

        }
    }

    /**
     * Creates the maze structure based on the given dimensions and maze type.
     * Initializes cells, sets boundaries, and places Pacman and food.
     *
     * @param mazeLength the length of the maze
     * @param mazeWidth  the width of the maze
     * @param mazeType   the type of the maze which affects cell values
     */
    private void createMaze(int mazeLength, int mazeWidth, int mazeType) {
        // Create the start cell (top-left corner of the maze)
        startCell = new Cell(0, 0, '#');
        Cell currentRowStart = startCell;

        // Build the maze structure row by row
        for (int i = 0; i < mazeWidth; i++) {
            Cell currentCell = currentRowStart;

            for (int j = 0; j < mazeLength; j++) {
                if (i == 0 || i == mazeWidth - 1 || j == 0 || j == mazeLength - 1) {
                    // Set boundary cells
                    currentCell.setValue('#');
                } else {
                    // Set inner cells based on maze type
                    if ((mazeType == 1 && j > i) || (mazeType == 2 && j < i) || (mazeType == 3 && i % 2 == 0 && j != 1 && j != mazeLength - 2)) {
                        currentCell.setValue('-');
                    } else {
                        currentCell.setValue('.');
                    }
                }

                if (j < mazeLength - 1) {
                    // Create the next cell in the row and link it
                    currentCell.right = new Cell(i, j + 1, '.');
                    currentCell.right.left = currentCell;
                    currentCell = currentCell.right;
                }
            }

            // Move to the next row if not the last row
            if (i < mazeWidth - 1) {
                currentRowStart.down = new Cell(i + 1, 0, '#');
                currentRowStart.down.up = currentRowStart;
                currentRowStart = currentRowStart.down;
            }
        }

        // Place Pacman at the starting position (1,1)
        pacmanCell = getCell(1, 1);
        if (pacmanCell != null) {
            pacmanCell.setValue('P');
        }

        // Place food
        foodCell=getCell(rowPosFood, colPosFood);
        foodCell.setValue( '*');
    }

    /**
     * Retrieves the cell at the specified row and column.
     *
     * @param row the row index of the cell
     * @param col the column index of the cell
     * @return the Cell object at the specified position, or null if out of bounds
     */
    public Cell getCell(int row, int col) {
        Cell currentRow = startCell;
        for (int i = 0; i < row; i++) {
            if (currentRow != null) {
                currentRow = currentRow.down;
            }
        }
        Cell currentCell = currentRow;
        for (int j = 0; j < col; j++) {
            if (currentCell != null) {
                currentCell = currentCell.right;
            }
        }

        return currentCell;
    }

    /**
     * Prints the maze to the console.
     */
    public void printMaze() {
        Cell currentRow = startCell;
        while (currentRow != null) {
            Cell currentCell = currentRow;
            while (currentCell != null) {
                System.out.print(currentCell.getValue());
                currentCell = currentCell.right;
            }
            System.out.println();
            currentRow = currentRow.down;
        }
    }

    /**
     * Gets the cell where Pacman is currently located.
     *
     * @return the Cell object where Pacman is located
     */
    public Cell getPacmanCell() {
        return pacmanCell;
    }

    /**
     * Sets the cell where Pacman is currently located.
     *
     * @param pacmanCell the Cell object to set as Pacman's location
     */
    public void setPacmanCell(Cell pacmanCell) {
        this.pacmanCell = pacmanCell;
    }

    /**
     * Gets the cell where the food is located.
     *
     * @return the Cell object where the food is located
     */
    public Cell getFoodCell() {
        return foodCell;
    }

    /**
     * Sets the cell where the food is located.
     *
     * @param foodCell the Cell object to set as the food's location
     */
    public void setFoodCell(Cell foodCell) {
        this.foodCell = foodCell;
    }

    /**
     * The Cell class represents a single cell in the maze.
     * It holds information about its position, value, and neighboring cells.
     */
    class Cell {
        // Row index of the cell
        private int row;
        // Column index of the cell
        private int col;
        // Value representing the cell's content (e.g., '#', '.', '-')
        private char value;
        // Link to the cell above
        private Cell up;
        // Link to the cell below
        private Cell down;
        // Link to the cell to the left
        private Cell left;
        // Link to the cell to the right
        private Cell right;

        /**
         * Constructs a new Cell with specified position and value.
         *
         * @param row   the row index of the cell
         * @param col   the column index of the cell
         * @param value the value representing the cell's content
         */
        public Cell(int row, int col, char value) {
            this.row = row;
            this.col = col;
            this.value = value;
        }

        /**
         * Gets the value of the cell.
         *
         * @return the value of the cell
         */
        public char getValue() {
            return value;
        }

        /**
         * Sets the value of the cell.
         *
         * @param value the new value of the cell
         */
        public void setValue(char value) {
            this.value = value;
        }

        /**
         * Gets the row index of the cell.
         *
         * @return the row index of the cell
         */
        public int getRow() {
            return row;
        }

        /**
         * Gets the column index of the cell.
         *
         * @return the column index of the cell
         */
        public int getCol() {
            return col;
        }
    }

}

