import java.util.Random;

/**
 * Common elements of plants (algae) and animals (orca, shark, whale, tuna, cod, anglefish).
 *
 * @author David J. Barnes and Michael Kölling and Areeb Rafiq and Ridwan Adam
 * @version 8.0
 */
public abstract class Organism
{
    // The Organism's probability of breeding
    protected final double BREEDING_PROBABILITY;
    // The maximum number of offspring.
    protected final int MAX_LITTER_SIZE;
    // Random number generator object to be used for random operations
    protected static final Random rand = new Random();
    // Whether the Organism is alive or not.
    private boolean alive;
    // The Organism's position.
    private Location location;
    // The maximum age the Organism can reach.
    protected int MAX_AGE;
    // The Organism's age.
    protected int age;

    
    /**
     * Constructor for objects of class Organism.
     * @param randomAge If true, the Organism will have a random age.
     * @param location The Organism's location.
     * @param MAX_AGE The maximum age the Organism can reach.
     * @param BREEDING_PROBABILITY The Organism's probability of breeding.
     * @param MAX_LITTER_SIZE The maximum number of offspring.
     */
    public Organism(boolean randomAge, Location location, int MAX_AGE, double BREEDING_PROBABILITY, int MAX_LITTER_SIZE) {
        this.alive = true;
        this.location = location;
        this.MAX_AGE = MAX_AGE;
        this.BREEDING_PROBABILITY = BREEDING_PROBABILITY;
        this.MAX_LITTER_SIZE = MAX_LITTER_SIZE;

        if (randomAge) {
            if (MAX_AGE > 0) {
                age = rand.nextInt(MAX_AGE);
            }
            else {
                age = 0;
            }
        }
        else {
            age = 0;
        }
    }

    
    /**
     * Act.
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     * @param step The current step (hour) the simulation is at
     */
    abstract public void act(int step, Field currentField, Field nextFieldState);
    
    /**
     * Check whether the Organism is alive or not.
     * @return true if the Organism is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the Organism is no longer alive.
     */
    protected void setDead()
    {
        alive = false;
        location = null;
    }
    
    /**
     * Return the Organism's location.
     * @return The Organism's location.
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * Set the Organism's location.
     * @param location The new location.
     */
    protected void setLocation(Location location)
    {
        this.location = location;
    }

    /**
     * Increment the age.
     */
    public void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
}
