import java.util.Random;

/**
 * Common elements of cod, tuna, sharks and orcas.
 *
 * @author David J. Barnes and Michael Kölling and Areeb Rafiq
 * @version 8.0
 */
public abstract class Animal
{
    // Whether the animal is alive or not.git
    private boolean alive;
    // The animal's position.
    private Location location;
    // Whether the animal is male or not (female)
    private Boolean isMale;
    
    /**
     * Constructor for objects of class Animal.
     * @param location The animal's location.
     */
    public Animal(Location location)
    {
        this.alive = true;
        this.location = location;
        this.isMale = new Random().nextBoolean();
    }
    
    /**
     * Act.
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     */
    abstract public void act(Field currentField, Field nextFieldState);
    
    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Check whether the animal is male or not (female).
     * @return true if the animal is male.
     */
    public Boolean isMale()
    {
        return isMale;
    }

    /**
     * Indicate that the animal is no longer alive.
     */
    protected void setDead()
    {
        alive = false;
        location = null;
    }
    
    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * Set the animal's location.
     * @param location The new location.
     */
    protected void setLocation(Location location)
    {
        this.location = location;
    }
}
