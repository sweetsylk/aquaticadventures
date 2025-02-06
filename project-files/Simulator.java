import java.util.*;

/**
 * A simple predator-prey simulator, based on a rectangular field containing 
 * rabbits and foxes.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a tuna will be created in any given grid position.
    private static final double TUNA_CREATION_PROBABILITY = 0.03;
    // The probability that a cod will be created in any given position.
    private static final double COD_CREATION_PROBABILITY = 0.1;
    private static final double SHARK_CREATION_PROBABILITY = 0.02;
    private static final double ORCA_CREATION_PROBABILITY = 0.015;

    // The current state of the field.
    private Field field;
    // The current step of the simulation (each step is equivilant to an hour).
    private int step;
    // A graphical view of the simulation.
    private final SimulatorView view;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be >= zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        field = new Field(depth, width);
        view = new SimulatorView(depth, width);

        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long 
     * period (4000 steps).
     * read this
     */
    public void runLongSimulation()
    {
        simulate(720);
    }
    
    /**
     * Run the simulation for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        reportStats();
        for(int n = 1; n <= numSteps && field.isViable(); n++) {
            simulateOneStep();
            delay(50);         // adjust this to change execution speed
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each tuna and cod.
     */
    public void simulateOneStep()
    {
        step++;
        // Use a separate Field to store the starting state of
        // the next step.
        Field nextFieldState = new Field(field.getDepth(), field.getWidth());

        List<Animal> animals = field.getAnimals();
        for (Animal anAnimal : animals) {
            anAnimal.act(field, nextFieldState);
        }
        
        // Replace the old state with the new one.
        field = nextFieldState;

        reportStats();
        view.showStatus(step, field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        populate();
        view.showStatus(step, field);
    }
    
    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {

                if(rand.nextDouble() <= ORCA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Orca orca = new Orca(true, location);
                    field.placeAnimal(orca, location);

                }

                else if(rand.nextDouble() <= SHARK_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Shark shark = new Shark(true, location);
                    field.placeAnimal(shark, location);

                }
                else if(rand.nextDouble() <= TUNA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Tuna tuna = new Tuna(true, location);
                    field.placeAnimal(tuna, location);
                }
                else if(rand.nextDouble() <= COD_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Cod cod = new Cod(true, location);
                    field.placeAnimal(cod, location);
                }
                // else leave the location empty.
            }
        }
    }

    /**
     * Report on the number of each type of animal in the field.
     */
    public void reportStats()
    {
        //System.out.print("Step: " + step + " ");
        field.fieldStats();
    }
    
    /**
     * Pause for a given time.
     * @param milliseconds The time to pause for, in milliseconds
     */
    private void delay(int milliseconds)
    {
        try {
            Thread.sleep(milliseconds);
        }
        catch(InterruptedException e) {
            // ignore
        }
    }

    public static void main (String[] arg)
    {
        Simulator simulation = new Simulator();
        simulation.runLongSimulation();
    }
}
