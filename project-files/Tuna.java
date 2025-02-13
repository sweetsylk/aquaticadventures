import java.util.Iterator;
import java.util.List;

/**
 * A simple model of a tuna.
 * Tunas age, move, eat cod and salmon, bread and die.
 * 
 * @author David J. Barnes and Michael Kölling and Areeb Rafiq and Ridwan Adam
 * @version 7.1
 */
public class Tuna extends Animal
{
    // Characteristics shared by all tuna (class variables).

    // The food value of a single cod (as food for tuna).
    private static final int COD_FOOD_VALUE = 50;
    // The food value of a single algae (as food for tuna).
    private static final int ALGAE_FOOD_VALUE = 20;

    private static int starvation;
    private static int consumed;
    private static int naturalDeath;
   
    
    
    

    /**
     * Create a tuna. A tuna can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the tuna will have random age and hunger level.
     * @param location The location within the field.
     */
    public Tuna(boolean randomAge, Location location, boolean isMale)
    {
        super(randomAge, location, 120, isMale, 10, 0.7, 10);
        foodLevel = rand.nextInt(COD_FOOD_VALUE);
    }
    
    /**
     * This is what the tuna does most of the time: it hunts for cod.
     * In the process, it might breed, die of hunger or sleep,
     * or die of old age.
     * Tunas are awake from 5am to 10pm
     * @param step the current step in the simulation
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    public void act(int step, Field currentField, Field nextFieldState)
    {
        if(isAlive())
        {
            incrementAge();
            incrementHunger();
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            if ((step % 24) >= 9 && (step % 24) <= 17)
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
        return "Tuna{" +
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
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 1);
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
     * Check whether this tuna is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    public void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New Tunas are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed(nextFieldState);
        if (Field.tunaCount() <= 100)
        {
            births += 5;
        }
        if(births > 0) {
            for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                boolean babyGender = rand.nextBoolean();
                Tuna young = new Tuna(false, loc, babyGender);
                nextFieldState.placeOrganism(young, loc);
            }
        }
    }
        

    public boolean canMate(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 15);
        boolean foundMale = this.isMale();
        boolean foundFemale = !this.isMale();

        for (Location loc : adjacent) {
            Organism organism = field.getOrganismAt(loc);
            if (organism instanceof Tuna tuna) {
                if (tuna.isMale()) {
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
