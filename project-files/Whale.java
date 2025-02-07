import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A Whale!!!
 * Whale's age, move, eat cod and Tuna, and die.
 *
 * @author David J. Barnes and Michael Kölling (made the original fox and rabbit)
 * @author Ridwan Adam - made the whale!
 * @version 7.1
 */
public class Whale extends Organism
{
    // Characteristics shared by all tuba (class variables).
    // The age at which a Whale can start to breed.
    private static final int BREEDING_AGE = 24;
    // The age to which a Whale can live.
    private static final int MAX_AGE = 360;
    // The likelihood of a Whale breeding.
    private static final double BREEDING_PROBABILITY = 0.1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value of a single prey. In effect, this is the
    // number of steps a Whale can go before it has to eat again.
    private static final int TUNA_FOOD_VALUE = 120;
    private static final int COD_FOOD_VALUE = 95;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The Whale's age.
    private int age;
    // The Whale's food level, which is increased by eating a Whale.
    private int foodLevel;

    /**
     * Create a Whale. A Whale can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the Whale will have random age and hunger level.
     * @param location The location within the field.
     * @param isMale whether the whale is male or not
     */
    public Whale(boolean randomAge, Location location, Boolean isMale)
    {
        super(location, isMale);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
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
    public void act(int step, Field currentField, Field nextFieldState)
    {
        incrementAge();
        incrementHunger();
        if(isAlive())
        {
            if ((step % 24) > 13 && (step % 24) <= 23)
            {
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
        return "Whale{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }

    /**
     * Increase the age. This could result in the Shark's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this Shark more hungry. This could result in the Shark's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for Organisms adjacent to the current location.
     * Only the first live tuna is eaten.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood(Field field) {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 10);
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while (foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Organism Organism = field.getOrganismAt(loc);
            if (Organism instanceof Tuna tuna) {
                if (tuna.isAlive()) {
                    tuna.setDead();
                    foodLevel += TUNA_FOOD_VALUE;
                    foodLocation = loc;
                }
                else if (Organism instanceof Cod cod) {
                    if (cod.isAlive()) {
                        cod.setDead();
                        foodLevel += COD_FOOD_VALUE;
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
    private void giveBirth(Field nextFieldState, List<Location> freeLocations)
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
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed(Field field)
    {
        int births;
        if(canBreed() && (rand.nextDouble() <= BREEDING_PROBABILITY) && canMate(field)) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }  else {
            births = 0;
        }
        return births;
    }
    /**
     * Checks if there is a mating pair (male and female) of sharks nearby.
     * @param field The field currently occupied.
     * @return true if there is a male and female Whale nearby.
     */
    private boolean canMate(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 20);
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

    /**
     * A Whale can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
