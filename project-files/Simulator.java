import java.util.*;

/**
 * A predator-prey simulator, based on a rectangular field containing 
 * tuna, cod, sharks, orcas, and whales.
 * 
 * @author David J. Barnes and Michael Kölling and Areeb Rafiq
 * @version 7.1
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probabilities that each organism will be created in any given grid position.
    private static final double ALGAE_CREATION_PROBABILITY = 0.12;
    private static final double COD_CREATION_PROBABILITY = 0.13;
    private static final double TUNA_CREATION_PROBABILITY = 0.08;
    private static final double ANGLERFISH_CREATION_PROBABILITY = 0.024;
    private static final double SHARK_CREATION_PROBABILITY = 0.03;
    private static final double ORCA_CREATION_PROBABILITY = 0.014;
    private static final double WHALE_CREATION_PROBABILITY = 0.012;
    private static Weather weather = new Weather();

    // The current state of the field.
    private Field field;
    // The current step of the simulation (each step is equivilant to an hour).
    public static int step;
    // A graphical view of the simulation.
    private final SimulatorView view;



    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
        reset();
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

    public static int getStep()
    {
        return step;
    }
    /**
     * Run the simulation from its current state for a reasonably long 
     * period (4000 steps).
     * read this
     */
    public void runLongSimulation()
    {
        simulate(240);
    }
    
    /**
     * Run the simulation for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        reportStats();
        for(int n = 1; n <= numSteps; n++) {
            simulateOneStep();
            delay(15);         // adjust this to change execution speed
        }
        finalReport();

    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each tuna and cod.
     */
    public void simulateOneStep()
    {
        step++;
        if (step % 6 == 0)
        {
            weather.update();
        }
        // Use a separate Field to store the starting state of
        // the next step.
        Field nextFieldState = new Field(field.getDepth(), field.getWidth());

        List<Organism> Organisms = field.getOrganisms();
        for (Organism anOrganism : Organisms) {
            if (anOrganism.isAlive())
            {
                anOrganism.act(step, field, nextFieldState);
            }
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

                if(rand.nextDouble() <= WHALE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    boolean isMale = rand.nextBoolean();
                    Whale whale = new Whale(true, location, isMale);
                    field.placeOrganism(whale, location);

                }

                else if(rand.nextDouble() <= ORCA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    boolean isMale = rand.nextBoolean();
                    Orca orca = new Orca(true, location, isMale);
                    field.placeOrganism(orca, location);

                }

                else if(rand.nextDouble() <= SHARK_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    boolean isMale = rand.nextBoolean();
                    Shark shark = new Shark(true, location, isMale);
                    field.placeOrganism(shark, location);

                }
                else if(rand.nextDouble() <= ANGLERFISH_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    boolean isMale = rand.nextBoolean();
                    Anglerfish anglerfish = new Anglerfish(true, location, isMale);
                    field.placeOrganism(anglerfish, location);
                }
                else if(rand.nextDouble() <= TUNA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    boolean isMale = rand.nextBoolean();
                    Tuna tuna = new Tuna(true, location, isMale);
                    field.placeOrganism(tuna, location);
                }
                else if(rand.nextDouble() <= COD_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    boolean isMale = rand.nextBoolean();
                    Cod cod = new Cod(true, location, isMale);
                    field.placeOrganism(cod, location);
                }
                else if(rand.nextDouble() <= ALGAE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Algae algae = new Algae(true, location);
                    field.placeOrganism(algae, location);
                }
                // else leave the location empty.
            }
        }
    }

    /**
     * Report on the number of each type of Organism in the field.
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
    public static WeatherType getWeather() {
        return weather.getCurrentWeather();
    }

    public static void main (String[] arg)
    {
        Simulator simulation = new Simulator();
        simulation.runLongSimulation();
    }
    private void finalReport()
    {
        System.out.println("Cods dead via Consumption: " + Cod.getConsumed()
                + "\nTunas dead via Consumption: " + Tuna.getConsumed()
                + "\nSharks dead via Consumption: " + Shark.getConsumed()
                + "\nWhales dead via Consumption: " + Whale.getConsumed()
                + "\nAnglerfishes dead via Consumption: " + Anglerfish.getConsumed());
    }
}
