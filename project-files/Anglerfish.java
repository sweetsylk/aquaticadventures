import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * This is an anglerfish - a nocturnal creature.
 * they can age, move, eat cod and salmon, and die.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Anglerfish extends Animal
{
    // Characteristics shared by all tuba (class variables).
    // The age at which they can start to breed.
    // The food value of a single prey. In effect, this is the
    // number of steps the fish can go before it has to eat again.
    private static final int COD_FOOD_VALUE = 40;
    private static final int ALGAE_FOOD_VALUE = 20;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();


    // Individual characteristics (instance fields).

    // The fish's age.
    private int age;
    // The fish's food level, which is increased by eating cod or salmon.
    private int foodLevel;

    private static int starvation;
    private static int consumed;
    private static int naturalDeath;

    /**
     * Create an Anglerfish. they can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the fish will have random age and hunger level.
     * @param location The location within the field.
     */
    public Anglerfish(Boolean randomAge, Location location, Boolean isMale)
    {
        super(randomAge, location, 60, isMale, 12, 0.65, 7);

        foodLevel = rand.nextInt(COD_FOOD_VALUE);


    }

    /**
     * This is what the Anglerfish does most of the time: it hunts for cod.
     * In the process, it might breed, die of hunger or sleep,
     * or die of old age.
     * Tunas are awake from 5am to 10pm
     * @param step the current step in the simulation
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    public void act(int step, Field currentField, Field nextFieldState)
    {
        incrementAge();
        incrementHunger();
        if(isAlive())
        {
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            if (((step % 24) >= 20) || ((step % 24) <= 5))
            {
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
        return "Anglerfish{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }




    /**
     * Look for rabbits adjacent to the current location.
     * Only the first live cod is eaten.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    public Location findFood(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 3);
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Organism Organism = field.getOrganismAt(loc);
            if(Organism instanceof Cod cod) {
                if(cod.isAlive()) {
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
        return foodLocation;
    }

    /**
     * Check whether this fish should give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    public void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New Anglerfish are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed(nextFieldState);
        if(births > 0) {
            for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                boolean babyGender = rand.nextBoolean();
                Anglerfish young = new Anglerfish(false, loc, babyGender);
                nextFieldState.placeOrganism(young, loc);
            }
        }
    }



    public boolean canMate(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 7);
        boolean foundMale = this.isMale();
        boolean foundFemale = !this.isMale();

        for (Location loc : adjacent) {
            Organism organism = field.getOrganismAt(loc);
            if (organism instanceof Anglerfish anglerfish) {
                if (anglerfish.isMale()) {
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
    public static void incrementConsumeDeath()
    {
        consumed += 1;

    }

    private static void incrementNaturalDeath()
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

