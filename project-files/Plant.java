public abstract class Plant extends Organism {
/**
 * A simple model of a plant of abstract type so can't be instantiated:
 * Plants are a subclass of Organisms so can do all the things organisms can do.
 * Plants include algae.
 *
 * @author Areeb Rafiq and Ridwan Adam
 * @version 1.0
 */
    

    /**
     * Constructor for objects of class plant.
     * @param randomAge If true, the Organism will have a random age.
     * @param location The Organism's location.
     * @param MAX_AGE The maximum age the Organism can reach.
     * @param BREEDING_PROBABILITY The Organism's probability of breeding.
     * @param MAX_LITTER_SIZE The maximum number of offspring.
     * 
     */
    public Plant(boolean randomAge, Location location, int maxAge, double breedingProbability, int maxLitterSize) {

        super(randomAge, location, maxAge, breedingProbability, maxLitterSize);
    }


    /**
     * abstract method to be overriden by subclasses.
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     * @param step The current step (hour) the simulation is at
     */
    @Override
    public abstract void act(int step, Field currentField, Field nextFieldState);

    /**
     * randomise how many births will happen 
     * @param field The current field to breed in.
     * @return the number of births to happen 
     */
    public int breed(Field field)
    {
        int births;
        if((rand.nextDouble() <= BREEDING_PROBABILITY)) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        else {
            births = 0;
        }
        return births;
    }


}


