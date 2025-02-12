import java.util.Iterator;
import java.util.List;
import java.util.Random;
public abstract class Plant extends Organism {

    protected double BREEDING_PROBABILITY;
    protected int MAX_LITTER_SIZE;
    private static final Random rand = new Random();

    /**
     * Constructor for objects of class Animal.
     */
    public Plant(boolean randomAge, Location location, int maxAge, double breedingProbability, int maxLitterSize) {
        super(randomAge, location, maxAge);
        this.BREEDING_PROBABILITY = breedingProbability;
        this.MAX_LITTER_SIZE = maxLitterSize;
    }


    public abstract void act(int step, Field currentField, Field nextFieldState);

    public int breed(Field field)
    {
        int births;
        if((rand.nextDouble() <= BREEDING_PROBABILITY)) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        else {
            births = 0;
        }
        return births;
    }


}


