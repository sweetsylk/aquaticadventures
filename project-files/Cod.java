import java.util.Iterator;
import java.util.List;

/**
 * A simple model of cod:
 * cod move, grow, breed, sleep, eats (algae), gets eaten (by anglerfish, tuna and whale) and die of starvation.
 * 
 * @author David J. Barnes and Michael Kölling and Areeb Rafiq and Ridwan Adam
 * @version 7.1
 */
public class Cod extends Animal
{
    
    // The food value of a single algae (as food for cod).
    private static final int ALGAE_FOOD_VALUE = 30;

    // Characteristics shared by all cod (class variables).
    private static int starvation;
    private static int consumed;
    private static int naturalDeath;
    
    /**
     * Constructor for objects of class cod: 
     * They can be created as a new born (age zero) or with a random age
     * and given random initial food level.
     * 
     * @param randomAge If true, the cod will have a random age.
     * @param location The location within the field.
     * @param isMale whether the cod is male or not (female)
     */
    public Cod(boolean randomAge, Location location, boolean isMale)
    {
        super(randomAge, location, 48, isMale, 3, 0.375, 7);
        foodLevel = rand.nextInt(ALGAE_FOOD_VALUE) + 12; // sets intial random food level for anglerfish

    }

    /**
     * This is what the cod does most of the time - it runs
     * around. Sometimes it will breed or die of old age or sleep even
     * cod are awake from 4am to 11pm and can give birth while sleeping
     * @param currentField The field occupied.
     * @param nextFieldState The updated field.
     */
    @Override
    public void act(int step, Field currentField, Field nextFieldState)
    {
        if(isAlive()) {
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            if ((step % 24) > 9 && (step % 24) <= 17 && Simulator.getWeather() != WeatherType.FROZEN) {
                incrementAge();
                incrementHunger();
                infectionCheck();
                if (!freeLocations.isEmpty()) {
                    giveBirth(nextFieldState, freeLocations);
                }
                // Try to move into a free location.
                if (!freeLocations.isEmpty()) {
                    Location nextLocation = freeLocations.get(0);
                    setLocation(nextLocation);
                    nextFieldState.placeOrganism(this, nextLocation);
                }
                else {
                    // Overcrowding.
                    setDead();
                }
                if (isCompromised(currentField, 3)) {
                    setInfected();
                }
            }
            else {
                nextFieldState.placeOrganism(this, getLocation());

            }
        }
    }

    /**
     * Overrides toString method so it returns inforrmation about the cod:
     * Including its age, location, whether it is alive and its food level.
     * @return Information about the cod.
     */
    @Override
    public String toString() {
        return "Cod{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                '}';
    }

    /**
     * find location of food source from adjacent locations
     * @param field The field currently occupied.
     * @return the location of next food to be eaten
     */
    @Override
    public Location findFood(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 1);
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Organism Organism = field.getOrganismAt(loc);
            if(Organism instanceof Algae algae) {
                if(algae.isAlive()) {
                    algae.setDead();
                    Algae.incrementConsumeDeath();
                    foodLevel += ALGAE_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }

    /**
     * Check whether or not this cod is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     * @param nextFieldState The updated field.
     */
    @Override
    public void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New rabbits are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed(nextFieldState);
        if(births > 0) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                boolean babyGender = rand.nextBoolean();
                Cod young = new Cod(false, loc, babyGender);
                nextFieldState.placeOrganism(young, loc);
            }
        }
    }
        

    /**
     * Checks if there is a mating pair (male and female) of cods nearby.
     * @param field The field currently occupied.
     * @return true if there is a male and female cod nearby.
     */
    @Override
    public boolean canMate(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 1);
        boolean foundMale = this.isMale();
        boolean foundFemale = !this.isMale();

        for (Location loc : adjacent) {
            Organism organism = field.getOrganismAt(loc);
            if (organism instanceof Cod cod) {
                if (cod.isMale()) {
                    foundMale = true;
                } else {
                    foundFemale = true;
                }
                if (foundMale && foundFemale) {
                    return true;
                }
            }
        }
        return false;
    }


    public static void incrementConsumeDeath()
    {
        consumed += 1;

    }

    public static void incrementNaturalDeath()
    {
        naturalDeath += 1;

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
