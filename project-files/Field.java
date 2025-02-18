import java.util.*;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single Organism/object.
 * 
 * @author David J. Barnes and Michael Kölling and Areeb Rafiq and Ridwan Adam
 * @version 7.1
 */
public class Field
{
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    
    // The dimensions of the field.
    private final int depth, width;
    // Organisms mapped by location.
    private final Map<Location, Organism> field = new HashMap<>();
    // The Organisms.
    private final List<Organism> Organisms = new ArrayList<>();

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
    }

    /**
     * Place an Organism at the given location.
     * If there is already an Organism at the location it will
     * be lost.
     * @param anOrganism The Organism to be placed.
     * @param location Where to place the Organism.
     */
    public void placeOrganism(Organism anOrganism, Location location)
    {
        if (location != null)
        {
        assert location != null;
        Object other = field.get(location);
        if(other != null) {
            Organisms.remove(other);
        }
        field.put(location, anOrganism);
        Organisms.add(anOrganism);
    }
    }
    
    /**
     * Return the Organism at the given location, if any.
     * @param location Where in the field.
     * @return The Organism at the given location, or null if there is none.
     */
    public Organism getOrganismAt(Location location)
    {
        return field.get(location);
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = getAdjacentLocations(location, 1);
        for(Location next : adjacent) {
            Organism anOrganism = field.get(next);
            if(anOrganism == null) {
                free.add(next);
            }
            else if(anOrganism.isAlive()) {
                free.add(next);
            }
        }
        return free;
    }


    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> getAdjacentLocations(Location location, int radius)
    {
        // The list of locations to be returned.
        List<Location> locations = new ArrayList<>();
        if(location != null) {
            int row = location.row();
            int col = location.col();
            for(int roffset = -(radius); roffset <= radius; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -1; coffset <= 1; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }
            
            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    /**
     * Print out the number of algaes, cods, angleFishes, tunas, sharks, whales and orcas in the field.
     */
    public void fieldStats()
    {
        int numTunas = 0, numCods = 0, numSharks = 0, numOrcas = 0, numWhales = 0, numAnglerfish = 0, numAlgae = 0;
        for(Organism anOrganism : field.values()) {
            if (anOrganism instanceof Tuna tuna) {
                if (tuna.isAlive()) {
                    numTunas++;
                }
            }
            else if (anOrganism instanceof Cod cod) {
                if (cod.isAlive()) {
                    numCods++;
                }
            }
            else if (anOrganism instanceof Shark shark) {
                if (shark.isAlive()) {
                    numSharks++;
                }
            }
            else if (anOrganism instanceof Orca orca) {
                if (orca.isAlive()) {
                    numOrcas++;
                }
            }
            else if (anOrganism instanceof Whale whale) {
                if (whale.isAlive()) {
                    numWhales++;
                }
            }
            else if (anOrganism instanceof Anglerfish anglerfish) {
                if (anglerfish.isAlive()) {
                    numAnglerfish++;
                }
            }
            else if (anOrganism instanceof Algae algae) {
                if (algae.isAlive()) {
                    numAlgae++;
                }
            }
        }

    }


    /**
     * Clear the field of all Organisms.
     */
    public void clear()
    {
        field.clear();
    }

    /**
     * Return whether there is at least one of every organism in the field.
     * @return true if there is at least one of every organism in the field.
     */
    public boolean isViable()
    {
        boolean codFound = false;
        boolean tunaFound = false;
        boolean sharkFound = false;
        boolean orcaFound = false;
        boolean whaleFound = false;
        boolean anglerfishFound = false;
        boolean algaeFound = false;
        Iterator<Organism> it = Organisms.iterator();
        while(it.hasNext() && ! (codFound && tunaFound && sharkFound && orcaFound && whaleFound && anglerfishFound && algaeFound)) {
            Organism anOrganism = it.next();
            if(anOrganism instanceof Cod cod) {
                if(cod.isAlive()) {
                    codFound = true;
                }
            }
            else if(anOrganism instanceof Tuna tuna) {
                if(tuna.isAlive()) {
                    tunaFound = true;
                }
            }
            else if(anOrganism instanceof Shark shark) {
                if(shark.isAlive()) {
                    sharkFound = true;
                }
            }
            else if(anOrganism instanceof Orca orca) {
                if(orca.isAlive()) {
                    orcaFound = true;
                }
            }
            else if(anOrganism instanceof Whale whale) {
                if (whale.isAlive()) {
                    whaleFound = true;
                }
            }
            else if(anOrganism instanceof Anglerfish anglerfish) {
                if (anglerfish.isAlive()) {
                    anglerfishFound = true;
                }
            }
            else if(anOrganism instanceof Algae algae) {
                if (algae.isAlive()) {
                    algaeFound = true;
                }
            }
        }
        return codFound && tunaFound && sharkFound && orcaFound && whaleFound && anglerfishFound && algaeFound ;
    }
    
    /**
     * Get the list of Organisms.
     * @return The list of Organisms.
     */
    public List<Organism> getOrganisms()
    {
        return Organisms;
    }



    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }
    
    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth()
    {
        return width;
    }
}
