import java.util.Iterator;
import java.util.List;

/**
 * A simple model of whales:
 * Whales are a subclass of Animals so can do all the things animals can do.
 * Can eat tuna, cod and algae.
 * Can get eaten by orca.
 *
 * @author David J. Barnes and Michael Kölling and Areeb Rafiq and Ridwan Adam
 * @version 7.1
 */
public class Whale extends Animal
{

    // The food values of a single algae, cod and tuna (as food for whales).
    // Characteristics shared by all Whales (class variables).
    private static final int TUNA_FOOD_VALUE = 120;
    private static final int ALGAE_FOOD_VALUE = 75;
    private static final int COD_FOOD_VALUE = 95;
    

    private static int starvation;
    private static int consumed;
    private static int naturalDeath;
    

  

    /**
     * Constructor for objects of class whale: 
     * They are given a random initial food level up to a maximum of biggest food source.
     *
     * @param randomAge If true, the fish will have random age.
     * @param location The initial location of the anglerFish within the field.
     * @param isMale Whether the anglerFish is male or not (female).
     */
    public Whale(boolean randomAge, Location location, boolean isMale)
    {
        super(randomAge, location, 200, isMale, 12, 0.78, 8);
        // sets a random intial food level for whale up to a maximum of biggest food source
        foodLevel = rand.nextInt(TUNA_FOOD_VALUE);
    }

    /**
     * This is what the Whale does most of the time: it hunts for
     * Tuna and cod. In the process, it might breed, die of hunger or sleep,
     * or die of old age.
     * Whales are awake from 5am to 5pm
     * @param step current step in the simulation
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    @Override
    public void act(int step, Field currentField, Field nextFieldState)
    {
        if(isAlive())
        {
            if ((step % 24) > 5 && ((step % 24) <= 20) && Simulator.getWeather() != WeatherType.FROZEN)
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
     * Overrides toString method so it returns inforrmation about the whale:
     * Including its age, location, whether it is alive and its food level.
     * @return Information about the whale.
     */
    @Override
    public String toString() {
        return "Whale{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }



    /**
     * Look for Organisms adjacent to the current location.
     * Only the first live tuna is eaten.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    public Location findFood(Field field) {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 8);
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while (foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Organism Organism = field.getOrganismAt(loc);
            if (Organism instanceof Tuna tuna) {
                if (tuna.isAlive()) {
                    tuna.setDead();
                    Tuna.incrementConsumeDeath();
                    foodLevel += TUNA_FOOD_VALUE;
                    foodLocation = loc;
                }
                else if (Organism instanceof Cod cod) {
                    if (cod.isAlive()) {
                        cod.setDead();
                        Cod.incrementConsumeDeath();
                        foodLevel += COD_FOOD_VALUE;
                        foodLocation = loc;
                    }
                }
                else if(Organism instanceof Algae algae) {
                    if(algae.isAlive()) {
                        algae.setDead();
                        Algae.incrementConsumeDeath();
                        foodLevel += ALGAE_FOOD_VALUE;
                        foodLocation = loc;
                    }

                }
            }

        }
        return foodLocation;
    }

    /**
     * Check whether this Shark is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    public void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed(nextFieldState);
        if(births > 0) {
            for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                boolean babyGender = rand.nextBoolean();
                Whale young = new Whale(false, loc, babyGender);
                nextFieldState.placeOrganism(young, loc);
            }
        }
    }

    /**
     * Checks if there is a mating pair (male and female) of sharks nearby.
     * @param field The field currently occupied.
     * @return true if there is a male and female Whale nearby.
     */
    public boolean canMate(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 35);
        boolean foundMale = this.isMale();
        boolean foundFemale = !this.isMale();

        for (Location loc : adjacent) {
            Organism organism = field.getOrganismAt(loc);
            if (organism instanceof Whale whale) {
                if (whale.isMale()) {
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
