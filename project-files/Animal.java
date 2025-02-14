import java.util.List;

public abstract class Animal extends Organism {


    // Whether the Animal is male or not (female)
    private final boolean isMale;
    // The food level of the animal
    protected int foodLevel;
    // The age at which the animal can start to breed
    protected final int BREEDING_AGE;
    /**
     * Constructor for objects of class Animal.
     * @param randomAge If true, the Organism will have a random age.
     * @param location The Organism's location.
     * @param MAX_AGE The maximum age the Organism can reach.
     * @param BREEDING_PROBABILITY The Organism's probability of breeding.
     * @param MAX_LITTER_SIZE The maximum number of offspring.
     * @param isMale Whether the Animal is male or not
     * @param breedingAge The age at which the animal can start to breed
     */
    public Animal(boolean randomAge, Location location, int maxAge, boolean isMale,
                  int breedingAge, double breedingProbability, int maxLitterSize) {

        super(randomAge, location, maxAge, breedingProbability, maxLitterSize);
        this.isMale = isMale;
        this.BREEDING_AGE = breedingAge;
        this.foodLevel = rand.nextInt(10) + 5; // Initialize food level randomly
    }

    /**
     * Act.
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     * @param step The current step (hour) the simulation is at
     */
    public abstract void act(int step, Field currentField, Field nextFieldState);

    /**
     * return if the animal is male or not (female)
     * @return true if the Organism is male
     */
    public boolean isMale() 
    { 
        return isMale; 
    }

    /**
     * increment the foodlevel of the animal
     */
    public void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Find food in the field.
     * @param field The field to look for food in.
     */
    public abstract Location findFood(Field field);

    /**
     * check if the animal can breed
     * @param true if the organism can breed
     */
    public boolean canBreed() {
        return age >= BREEDING_AGE;
    }

    /**
     * @param field The field to look for a mate in.
     * Force each animal species to implement their own mating behavior.
     */
    public abstract boolean canMate(Field field);

    /**
     * @param nextFieldState The updated field.
     * @param freeLocations The locations that are free in the current field.
     * give birth to a new animal
     */
    public abstract void giveBirth(Field nextFieldState, List<Location> freeLocations);

    /**
     * randomise how many births will happen 
     * @param field The current field to breed in.
     * @return the number of births to happen 
     */
    public int breed(Field field)
    {
        int births;
        if(canBreed() && (rand.nextDouble() <= BREEDING_PROBABILITY) && canMate(field))
        {
            if (Simulator.getWeather() == WeatherType.WARM)
            {
                births = rand.nextInt(MAX_LITTER_SIZE) + 5;
            }
            else if (Simulator.getWeather() == WeatherType.NORMAL)
            {
                births = rand.nextInt(MAX_LITTER_SIZE) + 1;
            }
            else if (Simulator.getWeather() == WeatherType.FROZEN)
            {
                births = 0;
            }
            else
            {
                births = 1;
            }
        }

        else {
            births = 0;
        }
        return births;
    }

    
    
}


