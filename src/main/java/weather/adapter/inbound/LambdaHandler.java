package weather.adapter.inbound;

import weather.adapter.outbound.openmeteo.MeteoClient;
import weather.core.service.WeatherService;
import weather.port.inbound.GetWeatherInfoUseCase;

import java.util.Map;

public class LambdaHandler {

	private final GetWeatherInfoUseCase weatherService;


	public LambdaHandler() {
		MeteoClient meteoClient = new MeteoClient();
		this.weatherService = new WeatherService(meteoClient);
	}


	public Object handleRequest() {
		try {
			return weatherService.getWeatherInfo("Wrocław");
		} catch (Exception e) {
			return Map.of(
					"error", "Service Unavailable",
					"message", e.getMessage()
			);
		}
	}
}