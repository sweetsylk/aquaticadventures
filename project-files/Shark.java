import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A SHARK!!!
 * Shark's age, move, eat cod and salmon, and die.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Shark extends Animal
{
    // Characteristics shared by all tuba (class variables).
    // The age at which a Shark can start to breed.
    // The food value of a single prey. In effect, this is the
    // number of steps a Shark can go before it has to eat again.
    private static final int TUNA_FOOD_VALUE = 60;
    private static final int ANGLERFISH_FOOD_VALUE = 75;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The shark's food level, which is increased by eating a shark.
    private int foodLevel;

    private static int starvation;
    private static int consumed;
    private static int naturalDeath;

    /**
     * Create a shark. A shark can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param location The location within the field.
     */
    public Shark(Boolean randomAge, Location location, Boolean isMale)
    {
        super(randomAge, location, 95, isMale, 15, 0.63, 5);
        foodLevel = rand.nextInt(TUNA_FOOD_VALUE);
    }

    /**
     * This is what the Shark does most of the time: it hunts for
     * Tuna and cod. In the process, it might breed, sleep or die of hunger,
     * or die of old age.
     * Sharks are awake from 9am to 5pm and 10pm to 4am
     * @param step the current step in the simulation
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    public void act(int step, Field currentField, Field nextFieldState)
    {
        if(isAlive())
        {
            if (((step % 24) >= 9 && (step % 24) <= 17))

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
        return "Shark{" +
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
                    tuna.setDead();
                    Tuna.incrementConsumeDeath();
                    foodLevel += TUNA_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
            if(Organism instanceof Anglerfish anglerfish) {
                if(anglerfish.isAlive()) {
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
     */
    public void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New animals are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed(nextFieldState);
        if(births > 0) {
            for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                boolean babyGender = rand.nextBoolean();
                Shark young = new Shark(false, loc, babyGender);
                nextFieldState.placeOrganism(young, loc);
            }
        }
    }

    /**
     * Checks if there is a mating pair (male and female) of sharks nearby.
     * @param field The field currently occupied.
     * @return true if there is a male and female shark nearby.
     */
    public boolean canMate(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 15);
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
