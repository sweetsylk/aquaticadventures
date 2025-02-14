import java.util.Iterator;
import java.util.List;

/**
 * A simple model of orcas:
 * orcas can move, grow, breed, sleep, eats (shark and whale) and die of starvation.
 * orcas are also the apex predator so don't get eaten by any other organism.
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Orca extends Animal
{
    // Characteristics shared by all tuba (class variables)
    // The food value of a single prey. In effect, this is the
    // number of steps a Orca can go before it has to eat again.
    private static final int SHARK_FOOD_VALUE = 30;
    private static final int WHALE_FOOD_VALUE = 50;

    private static int starvation;
    private static int consumed;
    private static int naturalDeath;

   
    /**
     * Create an orca. an orca can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the orca will have random age and hunger level.
     * @param location The location within the field.
     */
    public Orca(boolean randomAge, Location location, boolean isMale)
    {
        super(randomAge, location, 180, isMale, 20, 0.42, 2);
        foodLevel = rand.nextInt(SHARK_FOOD_VALUE);
    }

    /**
     * This is what the Orca does most of the time: it hunts for
     * Sharks. In the process, it might breed, die of hunger,
     * or die of old age.
     * Orcas are awake from 5am to 6pm
     * @param step the step the simulation is at currently
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    public void act(int step, Field currentField, Field nextFieldState)
    {

        if(isAlive())
        {
            if ((step % 24) >= 9 && (step % 24) <= 17)
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



    @Override
    public String toString() {
        return "Orca{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }



    /**
     * Look for Organisms adjacent to the current location.
     * Only the first live shark is eaten.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    public Location findFood(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 5);
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Organism Organism = field.getOrganismAt(loc);
            if(Organism instanceof Shark shark) {
                if(shark.isAlive()) {
                    shark.setDead();
                    Shark.incrementConsumeDeath();
                    foodLevel += SHARK_FOOD_VALUE;
                    foodLocation = loc;
                }
            }

            else if(Organism instanceof Whale whale) {
                if(whale.isAlive()) {
                    whale.setDead();
                    Whale.incrementConsumeDeath();
                    foodLevel += WHALE_FOOD_VALUE;
                    foodLocation = loc;
                }
            }



        }
        return foodLocation;
    }

    /**
     * Check whether this Orca is to give birth at this step.
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
                Orca young = new Orca(false, loc, babyGender);
                nextFieldState.placeOrganism(young, loc);
            }
        }
    }



    public boolean canMate(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 27);
        boolean foundMale = this.isMale();
        boolean foundFemale = !this.isMale();

        for (Location loc : adjacent) {
            Organism organism = field.getOrganismAt(loc);
            if (organism instanceof Orca orca) {
                if (orca.isMale()) {
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



    private static void incrementStarvationDeath()
    {
        starvation += 1;

    }

    private static void incrementNaturalDeath()
    {
        naturalDeath += 1;

    }
    public static int getStarvation()
    {
        return starvation;

    }

    public static int getNaturalDeath()
    {
        return naturalDeath;

    }
  
}
