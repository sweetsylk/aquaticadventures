import java.util.List;

/**
 * A simple model of Algae:
 * Algae grow, breed, gets eaten (by cod, anglerFish and tuna) and die naturally.
 * 
 * @author Ridwan Adam and Areeb Rafiq
 * @version 1.1
 */
public class Algae extends Plant
{
    // Characteristics shared by all algae (class variables).
    private static int consumed;
    private static int naturalDeath;

    /**
     * Constructor for objects of class Algae
     * @param randomAge If true, the cod will have a random age.
     * @param location The location of the algae within the field.
     */
    public Algae(boolean randomAge, Location location)
    {
        super(randomAge, location, 30, 0.35, 3);
    }

    /**
     * This is what the algae does most of the time - it grows
     * Sometimes it will breed or die of old age
     * @param currentField The field occupied.
     * @param nextFieldState The updated field.
     */
    @Override
    public void act(int step, Field currentField, Field nextFieldState)
    {
        incrementAge();
        if(isAlive()) {
            List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation());

            if (!freeLocations.isEmpty() && rand.nextDouble() <= BREEDING_PROBABILITY) {
                giveBirth(nextFieldState, freeLocations);
            }

            // Ensure it remains at its location
            nextFieldState.placeOrganism(this, getLocation());
        }
    }

    /**
     * Returns information about the algae:
     * Including its age, location and whether it is alive.
     * @return Information about the algae.
     */
    @Override
    public String toString() {
        return "Algae{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                '}';
    }

    /**
     * Check whether or not this algae is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     * @param nextFieldState The updated field.
     */
    public void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        int births = breed(nextFieldState);
        if(births > 0) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Algae young = new Algae(false, loc);
                nextFieldState.placeOrganism(young, loc);
            }
        }
    }

    /**
     * Increment the number of consumed algae
     */
    public static void incrementConsumeDeath()
    {
        consumed += 1;

    }

    /**
     * Increment the number of algae natural deaths
     */
    public static void incrementNaturalDeath()
    {
        naturalDeath += 1;

    }

    /**
     * Accessor method of attribute consumed
     * @return The number of consumed algae
     */
    public static int getConsumed()
    {
        return consumed;

    }

    /**
     * Accessor method of attribute naturalDeath
     * @return The number of algae natural deaths
     */
    public static int getNaturalDeath()
    {
        return naturalDeath;

    }
}
