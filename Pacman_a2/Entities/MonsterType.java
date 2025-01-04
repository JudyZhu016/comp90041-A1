/**
* Author: Tong Zhu
* Student Id: 1368179 
* Email: tonzhu1@student.unimelb.edu.au
*/

package Entities;

public enum MonsterType {
    RED('R'),
    BLUE('B'),
    GREEN('G'),
    YELLOW('Y');

    private final char symbol;

    MonsterType(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }
}

