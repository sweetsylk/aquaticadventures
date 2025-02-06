import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A SHARK!!!
 * Shark's age, move, eat cod and salmon, and die.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Shark extends Animal
{
    // Characteristics shared by all tuba (class variables).
    // The age at which a Shark can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a Shark can live.
    private static final int MAX_AGE = 85;
    // The likelihood of a Shark breeding.
    private static final double BREEDING_PROBABILITY = 0.1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value of a single prey. In effect, this is the
    // number of steps a Shark can go before it has to eat again.
    private static final int TUNA_FOOD_VALUE = 24;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The shark's age.
    private int age;
    // The shark's food level, which is increased by eating a shark.
    private int foodLevel;

    /**
     * Create a sharj. A shark can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the shark will have random age and hunger level.
     * @param location The location within the field.
     */
    public Shark(boolean randomAge, Location location)
    {
        super(location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
        foodLevel = rand.nextInt(TUNA_FOOD_VALUE);
    }

    /**
     * This is what the Shark does most of the time: it hunts for
     * Tuna. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    public void act(Field currentField, Field nextFieldState)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
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
                nextFieldState.placeAnimal(this, nextLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }



    @Override
    public String toString() {
        return "Shark{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }

    /**
     * Increase the age. This could result in the Shark's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this Shark more hungry. This could result in the Shark's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for animals adjacent to the current location.
     * Only the first live tuna is eaten.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Animal animal = field.getAnimalAt(loc);
            if(animal instanceof Tuna tuna) {
                if(tuna.isAlive()) {
                    tuna.setDead();
                    foodLevel += TUNA_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }

    /**
     * Check whether this Shark is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed();
        if(births > 0) {
            for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Shark young = new Shark(false, loc);
                nextFieldState.placeAnimal(young, loc);
            }
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        else {
            births = 0;
        }
        return births;
    }

    /**
     * A Shark can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
