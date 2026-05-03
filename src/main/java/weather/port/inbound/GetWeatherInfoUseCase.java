package weather.port.inbound;

import weather.core.model.WeatherInfo;

public interface GetWeatherInfoUseCase {
    WeatherInfo getWeatherInfo(String city);
}
