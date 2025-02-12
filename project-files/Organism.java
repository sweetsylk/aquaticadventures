import java.util.Iterator;
import java.util.List;
import java.util.Random;
/**
 * Common elements of cod, tuna, sharks and orcas.
 *
 * @author David J. Barnes and Michael Kölling and Areeb Rafiq
 * @version 8.0
 */
public abstract class Organism
{
    private static final Random rand = new Random();
    // Whether the Organism is alive or not.
    private boolean alive;
    // The Organism's position.
    private Location location;

    protected int MAX_AGE;
    protected int age;
    
    /**
     * Constructor for objects of class Organism.
     * @int randomAge is just random
     * @param location The Organism's location.
     */
    public Organism(boolean randomAge, Location location, int MAX_AGE) {
        this.alive = true;
        this.location = location;
        this.MAX_AGE = MAX_AGE;

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

    public void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
}
