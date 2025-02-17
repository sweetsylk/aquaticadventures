import java.util.List;

/**
 * A simple model of an Animal of abstract type so can't be instantiated:
 * Animals are a subclass of Organisms so can do all the things organisms can do.
 * Animals can also all move, eat and or get eaten, die of overcrowing and starvation.
 * Animals include cod, anglefish, tuna, sharks, whales and orca.
 * 
 * @author Ridwan Adam and Areeb Rafiq
 * @version 1.0
 */

public abstract class Animal extends Organism {


    // Whether the Animal is male or not (female)
    private final boolean isMale;
    // The food level of the animal
    protected int foodLevel;
    // The age at which the animal can start to breed
    private final int BREEDING_AGE;




    // whether the animal is infected with a disease or not
    protected double infectionRate = 0.01;
    protected boolean infected = false;

    /**
     * Constructor for objects of class Animal:
     * They assigned to be infected or not depedning on the infected chance paramater
     * 
     * @param randomAge If true, the Organism will have a random age.
     * @param location The Organism's location.
     * @param maxAge The maximum age the Organism can reach.
     * @param breedingProbability The Organism's probability of breeding.
     * @param maxLitterSize The maximum number of offspring.
     * @param isMale Whether the Animal is male or not
     * @param breedingAge The age at which the animal can start to breed
     */
    public Animal(boolean randomAge, Location location, int maxAge, boolean isMale, int breedingAge, double breedingProbability, int maxLitterSize)
    {
        super(randomAge, location, maxAge, breedingProbability, maxLitterSize);
        this.isMale = isMale;
        this.BREEDING_AGE = breedingAge;
        
    }

    /**
     * abtract method to be overriden by subclasses.
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     * @param step The current step (hour) the simulation is at
     */
    @Override
    public abstract void act(int step, Field currentField, Field nextFieldState);

    /**
     * return if the animal is male or not (female)
     * @return true if the Organism is male
     */
    public boolean isMale() 
    { 
        return isMale; 
    }

    public boolean isInfected() {
        return infected;
    }

    public void setInfected() {
        this.infected = true;
    }
    public void infectionCheck()
    {
        if (rand.nextDouble() < infectionRate) {
            setInfected();
        }
    }

    public boolean isCompromised(Field field, int radius) {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), radius);
        for (Location loc : adjacent) {
            Organism organism = field.getOrganismAt(loc);
            // Check that the organism is of the same class and is an Animal
            if (organism != null && organism.getClass().equals(this.getClass()) && organism instanceof Animal) {
                Animal other = (Animal) organism;
                if (other.isInfected())
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * increment the foodlevel of the animal
     */
    public void incrementHunger()
    {
        foodLevel--;
        // animals with disease end up starving earlier
        if (foodLevel <= 0 || (isInfected() && foodLevel <= 10)) {
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
    public boolean canBreed() 
    {
        return age >= BREEDING_AGE;
    }

    /**
     * abstract method to be overriden by subclasses.
     * @param field The field to look for a mate in.
     * Force each animal species to implement their own mating behavior.
     */
    public abstract boolean canMate(Field field);

    /**
     * abstract method to be overriden by subclasses.
     * @param nextFieldState The updated field.
     * @param freeLocations The locations that are free in the current field.
     * give birth to a new animal
     */
    public abstract void giveBirth(Field nextFieldState, List<Location> freeLocations);

    /**
     * Randomise how many births will happen for an animal.
     * Also based on the weather, the animal will give birth to a different number of offspring.
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
                births = rand.nextInt(MAX_LITTER_SIZE) + 4;
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


