import java.util.Iterator;
import java.util.List;

/**
 * A simple model of orcas:
 * Orcas are a subclass of Animals so can do all the things animals can do.
 * Orcas can eat sharks and whales.
 * Orcas can get eaten by no other organism (apex predator).
 * 
 * @author David J. Barnes and Michael Kölling and Areeb Rafiq and Ridwan Adam
 * @version 7.1
 */
public class Orca extends Animal
{
    // characteristics shared by all orcas (class variables).
    // The food values of a single shark and whale (as food for orca).
    private static final int SHARK_FOOD_VALUE = 30;
    private static final int WHALE_FOOD_VALUE = 50;

    private static int starvation;
    private static int naturalDeath;

   
    /**
     * Constructor for objects of class orca: 
     * They are given a random initial food level up to a maximum of biggest food source.
     * 
     * @param randomAge If true, the fish will have random age.
     * @param location The initial location of the anglerFish within the field.
     * @param isMale Whether the anglerFish is male or not (female).
     */
    public Orca(boolean randomAge, Location location, boolean isMale)
    {
        super(randomAge, location, 180, isMale, 20, 0.35, 2);
         // They are given a random initial food level up to a maximum of biggest food source.
        foodLevel = rand.nextInt(SHARK_FOOD_VALUE);
    }
    /**
     * This is what the Orca does most of the time: it hunts for
     * Sharks. In the process, it might breed, die of hunger,
     * or die of old age.
     * Orcas are awake from 5am to 6pm
     * @param step the step the simulation is at currently
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    @Override
    public void act(int step, Field currentField, Field nextFieldState)
    {
        if(isAlive())
        {
            // Orcas are awake from 5am to 6pm so only do actions in that time frame
            if ((step % 24) >= 9 && (step % 24) <= 17)
            {
                incrementAge();
                incrementHunger();
                 // makes orca sometimes become infected with disease
                infectionCheck();
                List<Location> freeLocations =
                        nextFieldState.getFreeAdjacentLocations(getLocation());
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
                 // if the orca is near an infected orca it will become infected
                if (isCompromised(currentField, 6)) {
                    setInfected();
                }
            }
            else {
                nextFieldState.placeOrganism(this, getLocation());
            }

        }
    }


    /**
     * Overrides toString method so it returns inforrmation about the orca:
     * Including its age, location, whether it is alive and its food level.
     * @return Information about the orca.
     */
    @Override
    public String toString() {
        return "Orca{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }



    /**
     * Look for sharks and whales adjacent (of radius 5) to the current location.
     * Then eat them and increase the food level.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    @Override
    public Location findFood(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 4);
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Organism Organism = field.getOrganismAt(loc);
            if(Organism instanceof Shark shark) {
                if(shark.isAlive()) {
                    // if the orca eats an infected shark, it becomes infected
                    if (shark.isInfected())
                    {
                        setInfected();
                    }
                    shark.setDead();
                    Shark.incrementConsumeDeath();
                    foodLevel += SHARK_FOOD_VALUE;
                    foodLocation = loc;
                }
            }

            else if(Organism instanceof Whale whale) {
                if(whale.isAlive()) {
                    // if the orca eats an infected whale, it becomes infected
                    if (whale.isInfected())
                    {
                        setInfected();
                    }
                    whale.setDead();
                    Whale.incrementConsumeDeath();
                    foodLevel += WHALE_FOOD_VALUE;
                    foodLocation = loc;
                }
            }



        }
        return foodLocation;
    }

    /**
     * Check whether this Orca is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     * @param nextFieldState The updated field.
     */
    @Override
    public void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New orcas are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed(nextFieldState);
        if(births > 0) {
            for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                boolean babyGender = rand.nextBoolean();
                Orca young = new Orca(false, loc, babyGender);
                nextFieldState.placeOrganism(young, loc);
            }
        }
    }


    /**
     * Checks if there is a mating pair (male and female) of orcas nearby.
     * @param field The field currently occupied.
     * @return true if there is a male and female orca nearby.
     */
    @Override
    public boolean canMate(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 27);
        boolean foundMale = this.isMale();
        boolean foundFemale = !this.isMale();

        for (Location loc : adjacent) {
            Organism organism = field.getOrganismAt(loc);
            if (organism instanceof Orca orca) {
                if (orca.isMale()) {
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

    private static void incrementNaturalDeath()
    {
        naturalDeath += 1;

    }
    public static int getStarvation()
    {
        return starvation;

    }

    public static int getNaturalDeath()
    {
        return naturalDeath;

    }
  
}
