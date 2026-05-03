package weather.adapter.inbound;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import weather.adapter.outbound.openmeteo.MeteoClient;
import weather.core.service.WeatherService;
import weather.port.inbound.GetWeatherInfoUseCase;
import java.util.Map;

public class Task2LambdaHandler implements RequestHandler<String, Object>  {

	private final GetWeatherInfoUseCase weatherService;

	public Task2LambdaHandler() {
		MeteoClient meteoClient = new MeteoClient();
		this.weatherService = new WeatherService(meteoClient);
	}
	
	@Override
	public Object handleRequest(String input, Context context) {
		try {
			// 1. Validate city input
			if(input == null || input.isBlank()) throw new IllegalArgumentException("City is null or blank");

			// 2. Get weather info from the service
			return weatherService.getWeatherInfo(input);
		} catch (Exception e) {
			return Map.of(
					"error", "Error while getting weather info",
					"message", e.getMessage()
			);
		}
	}
}
