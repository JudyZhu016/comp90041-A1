/**
* Author: Tong Zhu
* Student Id: 1368179 
* Email: tonzhu1@student.unimelb.edu.au
*/
import java.util.Random;

public final class FoodGenerator {

    private Random random;

    /**
     * Constructor to intialise a random number generator with a seed
     * @param seed: seed to feed to random number generator
     */
    FoodGenerator(long seed){
        this.random = new Random(seed);

    }

    /**
     * Returns a pseudorandomly chosen int value between the specified startBound (inclusive) and the specified endBound (exclusive).
     * Use this method to generate random positions for food.
     * @param startBound the least value that can be returned bound
     * @param endBound the upper bound (exclusive) for the returned value
     * @return a pseudorandomly chosen int value between the origin (inclusive) and the bound (exclusive)
     */
    public  int generatePosition(int startBound, int endBound) {
       return this.random.nextInt(startBound,endBound);
    }


}

