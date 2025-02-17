import java.util.List;

/**
 * A simple model of Algae:
 * Algae are a subclass of Plant so can do all the things plants can do.
 * Get eaten by cod, tuna, anglerfish and whale.
 * Eats no one (as they are a producer)
 * 
 * @author Ridwan Adam and Areeb Rafiq
 * @version 1.1
 */
public class Algae extends Plant
{
   
    private static int consumed;
    private static int naturalDeath;

    /**
     * Constructor for objects of class Algae
     * @param randomAge If true, the cod will have a random age.
     * @param location The location of the algae within the field.
     */
    public Algae(boolean randomAge, Location location)
    {
        super(randomAge, location, 36, 0.475, 4);
    }

    /**
     * This is what the algae does most of the time - it grows
     * Sometimes it will breed or die of old age
     * algae are awake from 9am to 5pm
     * algae can't give birth while in acidic weather. 
     * @param step The current step in the simulation.
     * @param currentField The field occupied.
     * @param nextFieldState The updated field.
     */
    @Override
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
