package weather.adapter.outbound.openmeteo;

import tools.jackson.databind.ObjectMapper;
import weather.adapter.outbound.openmeteo.dto.MeteoResponse;
import weather.adapter.outbound.openmeteo.model.CityCoordinates;
import weather.port.outbound.WeatherProviderPort;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;


public class MeteoClient implements WeatherProviderPort {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";

    public MeteoClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(4))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public double getCurrentTemperature() {
        try
        {
            // Task 1 stage 1: Wroclaw coordinates
            CityCoordinates coords = new CityCoordinates(51.1079, 17.0385);

            // Task 1 stage 2: Build full URL
            String fullUrl = String.format("%s?latitude=%s&longitude=%s&current_weather=true",
                    BASE_URL, coords.latitude(), coords.longitude());

            // Task 1 stage 3: Create and send HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .timeout(Duration.ofSeconds(8))
                    .GET()
                    .build();

            // Task 1 stage 4: Handle response and errors
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() != 200) {
                throw new RuntimeException("Failed to fetch weather data: HTTP " + response.statusCode());
            }

            // Task 1 stage 5: Parse JSON response and handle errors
            MeteoResponse dto = objectMapper.readValue(response.body(), MeteoResponse.class);

            if (dto == null || dto.currentWeather() == null) {
                throw new RuntimeException("Invalid response format from Weather API");
            }

            // Task 1 stage 6: Return current temperature
            return dto.currentWeather().temperature();


        } catch (ConnectException e) {
            throw new RuntimeException("Failed to connect to the weather service", e);
        } catch (HttpConnectTimeoutException e) {
            throw new RuntimeException("Connection timed out while trying to fetch weather data", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
