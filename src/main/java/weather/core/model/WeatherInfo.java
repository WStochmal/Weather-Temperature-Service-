package weather.core.model;

public record WeatherInfo(
        String city,
        double temperature,
        TemperatureCategory category
) {}
