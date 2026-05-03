package weather.adapter.inbound;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import weather.adapter.outbound.openmeteo.MeteoClient;
import weather.core.service.WeatherService;
import weather.port.inbound.GetWeatherInfoUseCase;
import java.util.Map;

public class Task3LambdaHandler implements RequestHandler<Map<String, Object>, Object> {

    private final GetWeatherInfoUseCase weatherService;

    public Task3LambdaHandler() {
        MeteoClient meteoClient = new MeteoClient();
        this.weatherService = new WeatherService(meteoClient);
    }

    @Override
    public Object handleRequest(Map<String, Object> input, Context context) {
        try {
            // 1. Extract city from query parameters
            Map<String, String> queryParams = (Map<String, String>) input.get("queryStringParameters");
            String city = (queryParams != null) ? queryParams.get("city") : "";

            // 2. Validate city
            if(city == null || city.isBlank()) throw new IllegalArgumentException("City is null or blank");

            // 3. Get weather info from the service
            return weatherService.getWeatherInfo(city);
        } catch (Exception e) {
            return Map.of(
                    "error", "Error while getting weather info",
                    "message", e.getMessage()
            );
        }
    }
}