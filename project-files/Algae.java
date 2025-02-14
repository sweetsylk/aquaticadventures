
import java.util.List;

/**
 * A simple model of Algae:
 * Algae grow, breed, gets eaten (by cod, anglerFish and tuna) and die naturally.
 * 
 * @author Ridwan Adam and Areeb Rafiq
 * @version 1.0
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
        super(randomAge, location, 36, 0.45, 5);
    }

    /**
     * This is what the algae does most of the time - it grows
     * Sometimes it will breed or die of old age
     * @param currentField The field occupied.
     * @param nextFieldState The updated field.
     */
    public void act(int step, Field currentField, Field nextFieldState)
    {
        if(isAlive()) {
            incrementAge();

            List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation());

            if (!freeLocations.isEmpty() && (rand.nextDouble() <= BREEDING_PROBABILITY) && ((step % 24) >= 9 && (step % 24) <= 17) && Simulator.getWeather() != WeatherType.ACIDIFIED) {
                giveBirth(nextFieldState, freeLocations);
            }


            nextFieldState.placeOrganism(this, getLocation());
        }
    }

    /**
     * Returns inforrmation about the algae
     * Including its age, location and whether it is alive.
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
