import java.util.Random;

/**
 * A simple model of Weather:
 * Weather can be normal, frozen, acidic or warm.
 * acidic weather stops algae from breeding.
 * normal, frozen and warm weather affect number of animal offspring
 * frozen weather stops shark, tuna and cod from doing any actions
 * Weather can change randomly. 
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
     * Update the weather depending on probabilities
     */
    public void update() {
        double chance = rand.nextDouble();
        if (chance < 0.01) {
            currentWeather = WeatherType.ACIDIFIED;
        } else if (chance < 0.05) {
            currentWeather = WeatherType.FROZEN;
        } else if (chance < 0.35) {
            currentWeather = WeatherType.WARM;
        } else {
            currentWeather = WeatherType.NORMAL;
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
        return "" + currentWeather;
    }
}


