package weather.core.service;

import weather.core.model.TemperatureCategory;
import weather.core.model.WeatherInfo;
import weather.port.inbound.GetWeatherInfoUseCase;
import weather.port.outbound.WeatherProviderPort;

public class WeatherService implements GetWeatherInfoUseCase {

    private final WeatherProviderPort weatherProviderPort;

    public WeatherService(WeatherProviderPort weatherProviderPort) {
        this.weatherProviderPort = weatherProviderPort;
    }

    @Override
    public WeatherInfo getWeatherInfo(String city) {

        // 1. Fetch temperature from the weather provider
        double temperature = weatherProviderPort.getCurrentTemperature();

        // 2. Categorize the temperature
        TemperatureCategory category = TemperatureCategory.categorizeTemperature(temperature);

        // 3. Return the weather info
        return new WeatherInfo(city, temperature, category);
    }
}
