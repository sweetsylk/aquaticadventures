import java.util.Iterator;
import java.util.List;

/**
 * A simple model of sharks:
 * Sharks are a subclass of Animals so can do all the things animals can do.
 * Sharks can eat tuna and anglerfish.
 * Sharks can get eaten by orca.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Shark extends Animal
{
    // Characteristics shared by all sharks (class variables).
    // The food values of a single tuna and anglerfish (as food for shark).
    private static final int TUNA_FOOD_VALUE = 60;
    private static final int ANGLERFISH_FOOD_VALUE = 75;
    
    // Characteristics shared by all sharks (class variables).
    private static int starvation;
    private static int consumed;
    private static int naturalDeath;
    

    /**
     * Constructor for objects of class shark: 
     * They are given a random initial food level up to a maximum of biggest food source.
     * 
     * @param randomAge If true, the fish will have random age.
     * @param location The initial location of the anglerFish within the field.
     * @param isMale Whether the anglerFish is male or not (female).
     * @param infectedChance The chance of the anglerFish being infected (from 0 to 1).
     */
    public Shark(boolean randomAge, Location location, boolean isMale, double infectedChance)
    {
        super(randomAge, location, 120, isMale, 12, 0.78, 7, infectedChance);
        // sets a random intial food level for shark up to a maximum of biggest food source
        foodLevel = rand.nextInt(TUNA_FOOD_VALUE); 
    }

    /**
     * This is what the Shark does most of the time: it hunts for
     * Tuna and anglerfish. In the process, it might breed, sleep or die of hunger,
     * or die of old age.
     * Sharks are awake from 9am to 5pm and 10pm to 4am
     * @param step the current step in the simulation
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    @Override
    public void act(int step, Field currentField, Field nextFieldState)
    {
        if(isAlive())
        {
            if (((step % 24) >= 9 && (step % 24) <= 19) && Simulator.getWeather() != WeatherType.FROZEN)

            {
                incrementAge();
                incrementHunger();
                List<Location> freeLocations =
                        nextFieldState.getFreeAdjacentLocations(getLocation());
                if(! freeLocations.isEmpty()) {
                    giveBirth(nextFieldState, freeLocations);
                }
                // Move towards a source of food if found.
                Location nextLocation = findFood(currentField);
                if(nextLocation == null && ! freeLocations.isEmpty()) {
                    // No food found - try to move to a free location.
                    nextLocation = freeLocations.remove(0);
                }
                // See if it was possible to move.
                if(nextLocation != null) {
                    setLocation(nextLocation);
                    nextFieldState.placeOrganism(this, nextLocation);
                }
                else {
                    // Overcrowding.
                    setDead();
                }
            }
            else {
                nextFieldState.placeOrganism(this, getLocation());
            }

        }
    }


    /**
     * Overrides toString method so it returns information about the shark:
     * Including its age, location, whether it is alive and its food level.
     * @return Information about the shark.
     */
    @Override
    public String toString() {
        return "Shark{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }


    /**
     * Look for tuna and anglerfish adjacent (of radius 4) to the current location.
     * Then eat them and increase the food level.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    @Override
    public Location findFood(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 4);
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Organism Organism = field.getOrganismAt(loc);
            if(Organism instanceof Tuna tuna) {
                if(tuna.isAlive()) {
                    // shark has a chance of becoming infected when eating an infected tuna
                    if (tuna.isInfected()) {
                        becomeInfectedChance(EATING_INFECTION_PROBABILITY);
                    }
                    tuna.setDead();
                    Tuna.incrementConsumeDeath();
                    foodLevel += TUNA_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
            else if(Organism instanceof Anglerfish anglerfish) {
                if(anglerfish.isAlive()) {
                    // shark has a chance of becoming infected when eating an infected anglerfish
                    if (anglerfish.isInfected()) {
                        becomeInfectedChance(EATING_INFECTION_PROBABILITY);
                    }
                    anglerfish.setDead();
                    anglerfish.incrementConsumeDeath();
                    foodLevel += ANGLERFISH_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }

    /**
     * Check whether this Shark is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     * @param nextFieldState The updated field.
     */
    @Override
    public void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New animals are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed(nextFieldState);
        if(births > 0) {
            for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                boolean babyGender = rand.nextBoolean();
                Shark young = new Shark(false, loc, babyGender, 0);
                nextFieldState.placeOrganism(young, loc);
            }
        }
    }

    /**
     * Checks if there is a mating pair (male and female) of sharks nearby.
     * @param field The field currently occupied.
     * @return true if there is a male and female shark nearby.
     */
    @Override
    public boolean canMate(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 30);
        boolean foundMale = this.isMale();
        boolean foundFemale = !this.isMale();

        for (Location loc : adjacent) {
            Organism organism = field.getOrganismAt(loc);
            if (organism instanceof Shark shark) {
                if (shark.isMale()) {
                    foundMale = true;
                } else {
                    foundFemale = true;
                }
                if (foundMale && foundFemale) {
                    return true;
                }
            }
        }
        return false; // No valid pair
    }


    public static void incrementStarvationDeath()
    {
        starvation += 1;

    }
    public static void incrementConsumeDeath()
    {
        consumed += 1;

    }
    public static void incrementNaturalDeath()
    {
        naturalDeath += 1;

    }
    public static int getStarvation()
    {
        return starvation;

    }
    public static int getConsumed()
    {
        return consumed;

    }
    public static int getNaturalDeath()
    {
        return naturalDeath;

    }
}
