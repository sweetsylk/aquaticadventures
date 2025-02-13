
import java.util.List;
import java.util.Random;

/**
 * A simple model of an Algae
 *
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Algae extends Plant
{
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The cod's age.
    private int age;
    private static int consumed;
    private static int naturalDeath;

    /**
     *
     * @param randomAge If true, the cod will have a random age.
     * @param location The location within the field.
     */
    public Algae(Boolean randomAge, Location location)
    {
        super(randomAge, location, 30, 0.2, 3);
    }




    /**
     * This is what the cod does most of the time - it runs
     * around. Sometimes it will breed or die of old age or sleep even
     * cod are awake from 4am to 11pm and can give birth while sleeping
     * @param currentField The field occupied.
     * @param nextFieldState The updated field.
     */
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


    @Override
    public String toString() {
        return "Cod{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                '}';
    }

    /**
     * Check whether or not this cod is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
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
