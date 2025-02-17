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

    // Characteristics shared by all Whales (class variables).
    // The food values of a single algae, cod and tuna (as food for whales).
    private static final int TUNA_FOOD_VALUE = 120;
    private static final int ALGAE_FOOD_VALUE = 72;
    private static final int COD_FOOD_VALUE = 96;

    private static int starvation;
    private static int consumed;
    private static int naturalDeath;
    

  

    /**
     * Constructor for objects of class whale: 
     * They are given a random initial food level based on their biggest food source.
     *
     * @param randomAge If true, the fish will have random age.
     * @param location The initial location of the anglerFish within the field.
     * @param isMale Whether the anglerFish is male or not (female)
     */
    public Whale(boolean randomAge, Location location, boolean isMale)
    {
        super(randomAge, location, 200, isMale, 12, 0.5, 2);
         // They are given a random initial food level based on their biggest food source.
        foodLevel = rand.nextInt(TUNA_FOOD_VALUE) + 72;
    }

    /**
     * This is what the Whale does most of the time: it hunts for
     * Tuna, cod and algae. In the process, it might breed, die of hunger or sleep,
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
            // Whales are awake from 5am to 5pm so only do actions in that time frame
            // and only if the weather is not frozen
            if ((step % 24) > 5 && ((step % 24) <= 20) && Simulator.getWeather() != WeatherType.FROZEN)
            {
                incrementAge();
                incrementHunger();
                // deals with necessary infection operations that occur every step
                infectionCheck();
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
                 // if the whale is near an infected whale it will become infected
                if (isCompromised(currentField, 7)) {
                    setInfected();
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
     * Look for algae and cod adjacent (of radius 8) to the current location.
     * Then eat them and increase the food level..
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    @Override
    public Location findFood(Field field) {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 8);
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while (foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Organism Organism = field.getOrganismAt(loc);
            if (Organism instanceof Tuna tuna) {
                if (tuna.isAlive()) {
                    // if the whale eats an infected tuna, it becomes infected
                    if (tuna.isInfected())
                    {
                        setInfected();
                    }
                    tuna.setDead();
                    Tuna.incrementConsumeDeath();
                    foodLevel += TUNA_FOOD_VALUE;
                    foodLocation = loc;
                }
                else if (Organism instanceof Cod cod) {
                    if (cod.isAlive()) {
                        // if the whale eats an infected cod, it becomes infected
                        if (cod.isInfected())
                        {
                            setInfected();
                        }
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
     * @param nextFieldState The updated field.
     */
    @Override
    public void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New whales are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed(nextFieldState);
        if(births > 0) {
            for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                boolean babyGender = rand.nextBoolean();
                Whale young = new Whale(false, loc, babyGender);
                nextFieldState.placeOrganism(young, loc);
                // if parent is infected the child becomes infected upon birth
                if (infected == true){
                    young.setInfected();
                }
            }
        }
    }

    /**
     * Checks if there is a mating pair (male and female) of whales nearby.
     * @param field The field currently occupied.
     * @return true if there is a male and female Whale nearby.
     */
    @Override
    public boolean canMate(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 30);
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
