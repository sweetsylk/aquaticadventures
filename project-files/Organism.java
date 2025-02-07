import java.util.Random;

/**
 * Common elements of cod, tuna, sharks and orcas.
 *
 * @author David J. Barnes and Michael Kölling and Areeb Rafiq
 * @version 8.0
 */
public abstract class Organism
{
    // Whether the Organism is alive or not.
    private boolean alive;
    // The Organism's position.
    private Location location;
    // Whether the Organism is male or not (female)
    private final Boolean isMale;
    
    /**
     * Constructor for objects of class Organism.
     * @param location The Organism's location.
     * @param isMale Whether the creature is male or not
     */
    public Organism(Location location, Boolean isMale)
    {
        this.alive = true;
        this.location = location;
        this.isMale = isMale;
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
     * Check whether the Organism is male or not (female).
     * @return true if the Organism is male.
     */
    public Boolean isMale()
    {
        return isMale;
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
}
