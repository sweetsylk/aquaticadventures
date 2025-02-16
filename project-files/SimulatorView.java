import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.0
 */
public class SimulatorView extends JFrame
{
    // Colors used for empty locations.
    private static Color EMPTY_COLOR_DAY = new Color(32, 230, 230);
    private static Color EMPTY_COLOR_NIGHT_ACIDIC = new Color(32, 230, 60);
    private static Color EMPTY_COLOR_NIGHT = new Color(8, 60, 100);
    private static Color EMPTY_COLOR_DAY_ACIDIC = new Color(32/2, 230/2, 60);


    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;
    private static final Color UNKNOWN_COLOR_NIGHT = new Color(0,0,0);

    private final String DAY_PREFIX = "Day: ";
    private final String POPULATION_PREFIX = "Population: ";
    private final String WEATHER_PREFIX = "Weather: ";
    private final JLabel dayLabel;
    private final JLabel population;
    private final JLabel weather;
    private final FieldView fieldView;
    
    // A map for storing colors for participants in the simulation
    private final Map<Class<?>, Color> colors;
    // A statistics object computing and storing simulation information
    private final FieldStats stats;

    private static boolean dayTime;
    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width)
    {
        /**
         * Sharks are grey
         * the water switches between light and dark blue
         * the Tuna is pinkish
         * the cod is yellow
         * orcas are charcoal
         * whales are purple
         * anglerfish are brown
         * algaes are green
         *
         */
        Color sharkColor = new Color(150,120,130);
        Color waterColor = new Color(32, 230, 230);
        Color tunaColor = new Color(240, 60, 73);
        Color codColor = new Color(200, 200, 12);
        Color orcaColor = new Color(49,37,32);
        Color whaleColor = new Color(110, 0, 150);
        Color anglerfishColor = new Color(150, 50, 0);
        Color algaeColor = new Color(12,100,12 );
        stats = new FieldStats();
        colors = new LinkedHashMap<>();
        setColor(Cod.class, codColor);
        setColor(Tuna.class, tunaColor);
        setColor(Shark.class, sharkColor);
        setColor(Orca.class, orcaColor);
        setColor(Whale.class, whaleColor);
        setColor(Anglerfish.class, anglerfishColor);
        setColor(Algae.class, algaeColor);

        setTitle("Aquatic Adventures");
        dayLabel = new JLabel(DAY_PREFIX, JLabel.CENTER);
        weather = new JLabel(WEATHER_PREFIX, JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        setLocation(100, 50);

        fieldView = new FieldView(height, width);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(dayLabel);
        topPanel.add(weather);

        Container contents = getContentPane();
        contents.add(topPanel, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);

        pack();
        setVisible(true);


    }
    
    /**
     * Define a color to be used for a given class of Organism.
     * @param OrganismClass The Organism's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class<?> OrganismClass, Color color)
    {
        colors.put(OrganismClass, color);
    }

    /**
     * @return The color to be used for a given class of Organism.
     */
    private Color getColor(Class<?> OrganismClass)
    {
        Color col = colors.get(OrganismClass);
        if(col == null) {
            // no color defined for this class
            boolean daytime = SimulatorView.getdayTime();
            if (daytime == true)
            {
                return UNKNOWN_COLOR;
            }
            else
            {
                return UNKNOWN_COLOR_NIGHT;
            }
        }
        else {
            return col;
        }
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, Field field, String current_weather)
    {
        if(!isVisible()) {
            setVisible(true);
        }
        StringBuilder time = new StringBuilder("");
        time.append(step / 24);
        time.append(" ");
        time.append((step % 24));
        time.append(":");
        time.append("00");


        dayLabel.setText(DAY_PREFIX + (time));
        weather.setText(WEATHER_PREFIX + (current_weather));

        stats.reset();
        if ((step % 24) >= 6 && (step % 24) <= 19) {
            dayTime = true;
        } else {
            dayTime = false;
        }
        fieldView.preparePaint();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object Organism = field.getOrganismAt(new Location(row, col));
                Organism organism = field.getOrganismAt(new Location(row, col));
                if(Organism != null && organism.isAlive()) {
                    stats.incrementCount(Organism.getClass());
                    fieldView.drawMark(col, row, getColor(Organism.getClass()));
                }
                else {

                    if (Simulator.getWeather() != null && Simulator.getWeather() != WeatherType.ACIDIFIED) {
                        fieldView.drawMark(col, row, dayTime ? EMPTY_COLOR_DAY : EMPTY_COLOR_NIGHT);
                    }
                    else{
                        fieldView.drawMark(col, row, dayTime ? EMPTY_COLOR_DAY_ACIDIC : EMPTY_COLOR_NIGHT_ACIDIC);

                    }

                }
            }
        }
        stats.countFinished();

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        fieldView.repaint();
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }
    
    /**
     * Provide a graphical view of a rectangular field. This is 
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     * This is rather advanced GUI stuff - you can ignore this 
     * for your project if you like.
     */
    private class FieldView extends JPanel
    {
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private final int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image fieldImage;

        /**
         * Create a new FieldView component.
         */
        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        /**
         * Tell the GUI manager how big we would like to be.
         */
        public Dimension getPreferredSize()
        {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                                 gridHeight * GRID_VIEW_SCALING_FACTOR);
        }

        /**
         * Prepare for a new round of painting. Since the component
         * may be resized, compute the scaling factor again.
         */
        public void preparePaint()
        {
            if(! size.equals(getSize())) {  // if the size has changed...
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if(xScale < 1) {
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if(yScale < 1) {
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }

                g.setColor(dayTime ? EMPTY_COLOR_DAY : EMPTY_COLOR_NIGHT);
                g.fillRect(0, 0, size.width, size.height);
            }
        }
        
        /**
         * Paint on grid location on this field in a given color.
         */
        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);

            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
        }

        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         */
        public void paintComponent(Graphics g)
        {
            if(fieldImage != null) {
                Dimension currentSize = getSize();
                if(size.equals(currentSize)) {
                    g.drawImage(fieldImage, 0, 0, null);
                }
                else {
                    // Rescale the previous image.
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
    }

    public static boolean getdayTime()
    {
        return dayTime;
    }

}
