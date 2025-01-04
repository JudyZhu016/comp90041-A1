/**
* Author: Tong Zhu
* Student Id: 1368179 
* Email: tonzhu1@student.unimelb.edu.au
*/

package Entities;

public class Monster {
    private MonsterType type;;
    private int rowPos;
    private int colPos;

    public Monster(MonsterType type) {
        this.type = type;
    }

    public MonsterType getType() {
        return type;
    }

    public char getSymbol() {
        // Use the symbol from the enum directly
        return type.getSymbol();
    }

    public void setPos(int rowPos, int colPos) {
        this.rowPos = rowPos;
        this.colPos = colPos;
    }

    public int getRowPos() {
        return rowPos;
    }

    public int getColPos() {
        return colPos;
    }

}

