import java.util.Iterator;
import java.util.List;

/**
 * A simple model of Anglerfishes:
 * Anglerfishes are a subclass of Animals so can do all the things animals can do.
 * Can eat algae.
 * Can get eaten by shark.
 * Are also nocturnal.
 * @author David J. Barnes and Michael Kölling and Areeb Rafiq and Ridwan Adam
 * @version 7.1
 */
public class Anglerfish extends Animal
{
    // characteristics shared by all anglerfish (class variables).
    // The food values of a single algae (as food for anglerfish).
    private static final int ALGAE_FOOD_VALUE = 36;

    private static int starvation;
    private static int consumed;
    private static int naturalDeath;


    /**
     * Constructor for objects of class Anglerfish: 
     * They are given a random initial food level based on their biggest food source.
     * @param randomAge If true, the anglerfish will have random age.
     * @param location The initial location of the anglerFish within the field.
     * @param isMale Whether the anglerFish is male or not (female).
     */
    public Anglerfish(boolean randomAge, Location location, boolean isMale)
    {
        super(randomAge, location, 96, isMale, 12, 0.65, 7);
        // They are given a random initial food level based on their biggest food source.
        foodLevel = rand.nextInt(ALGAE_FOOD_VALUE) + 24;
    }

    /**
     * This is what the Anglerfish does most of the time: it hunts for algae.
     * In the process, it might breed, die of hunger or sleep,
     * or die of old age.
     * Anglerfish are awake from 6pm to 6am
     * @param step The current step in the simulation.
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    @Override
    public void act(int step, Field currentField, Field nextFieldState)
    {
        if(isAlive())
        {
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            // Anglerfish are awake from 6pm to 6am so only do actions in that time frame
            if (((step % 24) >= 18) || ((step % 24) <= 6))
            {
                
                incrementAge();
                incrementHunger();
                // deals with necessary infection operations that occur every step
                infectionCheck();
                if(! freeLocations.isEmpty()) {
                    giveBirth(nextFieldState, freeLocations);
                }
                // Move towards a source of food if found.
                Location nextLocation = findFood(currentField);
                if(nextLocation == null && ! freeLocations.isEmpty()) {
                    // No food found - try to move to a free location.
                    nextLocation = freeLocations.remove(0);
                }
                // if the anglerfish is near an infected anglerfish it will become infected
                if (isCompromised(currentField, 3)) {
                    setInfected();
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

    /**
     * Overrides toString method so it returns inforrmation about the anglerFish:
     * Including its age, location, whether it is alive and its food level.
     * @return Information about the anglerFish.
     */
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
     * Look for algae near (of radius 3) to the current location.
     * Then eat them and increase the food level.
     * @param field The field currently occupied.
     * @return Last loction of food that was eaten, or null if none were eaten.
     */
    @Override
    public Location findFood(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 3);
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Organism Organism = field.getOrganismAt(loc);
            if(Organism instanceof Algae algae) {
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
     * Check whether this anglerfish should give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations a list of free adjacent locations.
     * @param newFieldState The updated field.
     */
    @Override
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
                // if parent is infected the child becomes infected upon birth
                if (infected == true){
                    young.setInfected();
                }
                
            }
        }
    }


    /**
     * Check whether or not the anglerfish can mate in this step.
     * @param field The field currently occupied.
     * @return True if the anglerfish can mate in this step.
     */
    @Override
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

