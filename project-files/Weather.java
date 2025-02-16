import java.util.Random;
public class Weather
{
    private WeatherType currentWeather;
    private final Random rand;
    public Weather()
    {
        rand = new Random();
        currentWeather = WeatherType.NORMAL;


    }
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

    public WeatherType getCurrentWeather() {
        return currentWeather;
    }

    @Override
    public String toString() {
        return "" + currentWeather;
    }
}


