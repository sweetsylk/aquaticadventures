import java.util.List;

public abstract class Animal extends Organism {
    // Whether the Animal is male or not (female)
    private final Boolean isMale;
    protected int foodLevel;
    protected final int BREEDING_AGE;
    /**
     * Constructor for objects of class Animal.
     *
     * @param isMale Whether the Animal is male or not
     */
    public Animal(boolean randomAge, Location location, int maxAge, boolean isMale,
                  int breedingAge, double breedingProbability, int maxLitterSize) {
        super(randomAge, location, maxAge, breedingProbability, maxLitterSize);
        this.isMale = isMale;
        this.BREEDING_AGE = breedingAge;
        this.foodLevel = rand.nextInt(10) + 5; // Initialize food level randomly
}

    public abstract void act(int step, Field currentField, Field nextFieldState);

    public Boolean isMale() { return isMale; }


    public void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }
    public abstract Location findFood(Field field);

    public boolean canBreed() {
        return age >= BREEDING_AGE;
    }

    /**
     * Force each animal species to implement their own mating behavior.
     */
    public abstract boolean canMate(Field field);

    public abstract void giveBirth(Field nextFieldState, List<Location> freeLocations);


    public int breed(Field field)
    {
        int births;
        if(canBreed() && (rand.nextDouble() <= BREEDING_PROBABILITY) && canMate(field)) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        else {
            births = 0;
        }
        return births;
    }



}


