import java.util.Random;

/**
 * A simple model of Weather:
 * Weather can be normal, frozen, acidic or warm.
 * Weather can change randomly.
 * 
 * @author Ridwan Adam and Areeb Rafiq
 * @version 1.0
 */
public class Weather
{

    // class fields
    private WeatherType currentWeather;
    private final Random rand;

    /**
     * Constructor for objects of class Weather:
     * Weather is normal by default.
     */
    public Weather()
    {
        rand = new Random();
        currentWeather = WeatherType.NORMAL;
    }

    /**
     * Update the weather.
     */
    public void update() {
        double chance = rand.nextDouble();
        if (chance < 0.01) {
            currentWeather = WeatherType.FROZEN;
            System.out.println("It's icy and cold today!");
        } else if (chance < 0.05) {
            currentWeather = WeatherType.ACIDIFIED;
            System.out.println("It's acidic today!");
        } else if (chance < 0.35) {
            currentWeather = WeatherType.WARM;
            System.out.println("It's warm today!");
        } else {
            currentWeather = WeatherType.NORMAL;
            System.out.println("It's a normal day today!");
        }
    }

    /**
     * Get the current weather.
     * @return The current weather.
     */
    public WeatherType getCurrentWeather() {
        return currentWeather;
    }

    /**
     * override the toString method to return the current weather.
     * @return The current weather.
     */
    @Override
    public String toString() {
        return "Weather: " + currentWeather;
    }
}


