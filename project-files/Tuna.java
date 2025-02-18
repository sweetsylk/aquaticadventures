import java.util.Iterator;
import java.util.List;

/**
 * A simple model of tuna:
 * Tunas are a subclass of Animals so can do all the things animals can do.
 * Can eat cod and algae.
 * Can get eaten by shark and whale.
 * @author David J. Barnes and Michael Kölling and Areeb Rafiq and Ridwan Adam
 * @version 7.1
 */
public class Tuna extends Animal
{

    // Characteristics shared by all tunas (class variables).
    // The food values of a single algae and cod (as food for tuna).
    private static final int COD_FOOD_VALUE = 72;
    private static final int ALGAE_FOOD_VALUE = 50;
   
    /**
     * Constructor for objects of class tuna: 
     * They are given a random initial food level based on their biggest food source.
     * @param randomAge If true, the tuna will have random age.
     * @param location The initial location of the tuna within the field.
     * @param isMale Whether the tuna is male or not (female).
     */
    public Tuna(boolean randomAge, Location location, boolean isMale)
    {
        super(randomAge, location, 96, isMale, 12, 0.4, 6);
         // They are given a random initial food level based on their biggest food source.
        foodLevel = rand.nextInt(COD_FOOD_VALUE) + 36;
    }
    
    /**
     * This is what the tuna does most of the time: it hunts for cod and algae.
     * In the process, it might breed, die of hunger or sleep,
     * or die of old age.
     * Tunas are awake from 9am to 5pm
     * @param step the current step in the simulation
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    @Override
    public void act(int step, Field currentField, Field nextFieldState)
    {
        if(isAlive())
        {
            incrementAge();
            incrementHunger();
            // deals with necessary infection operations that occur every step
            infectionCheck();
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            // Tunas are awake from 9am to 5pm so only do actions in that time frame
            // and only if the weather is not frozen
            if ((step % 24) >= 9 && (step % 24) <= 17 && Simulator.getWeather() != WeatherType.FROZEN)
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
             // if the tuna is near an infected tuna it will become infected
            if (isCompromised(currentField, 2)) {
                    setInfected();
                }
        }
        else {
            nextFieldState.placeOrganism(this, getLocation());
            }
        }
    }


    /**
     * Overrides toString method so it returns inforrmation about the tuna:
     * Including its age, location, whether it is alive and its food level.
     * @return Information about the tuna.
     */
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
     * Look for algae and cod adjacent (of radius 1) to the current location.
     * Then eat them and increase the food level.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
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
            if(Organism instanceof Cod cod) {
                if(cod.isAlive()) {
                    // if the tuna eats an infected cod, it becomes infected
                    if (cod.isInfected())
                    {
                        setInfected();
                    }
                    cod.setDead();
                    foodLevel += COD_FOOD_VALUE;
                    foodLocation = loc;
                }

            }
            else if(Organism instanceof Algae algae) {
                if(algae.isAlive()) {
                    algae.setDead();
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
     * @param freeLocations a list of free adjacent locations.
     * @param nextFieldState The updated field.
     */
    @Override
    public void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New Tunas are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed(nextFieldState);
        if(births > 0) {
            for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                boolean babyGender = rand.nextBoolean();
                Tuna young = new Tuna(false, loc, babyGender);
                nextFieldState.placeOrganism(young, loc);
                // if parent is infected the child becomes infected upon birth
                if (infected == true){
                    young.setInfected();
                }
            }
        }
    }
        
     /**
     * Checks if there is a mating pair (male and female) of tunas nearby.
     * @param field The field currently occupied.
     * @return true if there is a male and female tuna nearby.
     */
    @Override
    public boolean canMate(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 25);
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
}
