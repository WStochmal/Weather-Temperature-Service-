package weather.adapter.outbound.openmeteo;

import tools.jackson.databind.ObjectMapper;
import weather.adapter.outbound.openmeteo.dto.GeocodingResponse;
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
    private static final String WEATHER_URL = "https://api.open-meteo.com/v1/forecast";
    private static final String GEOCODING_URL = "https://geocoding-api.open-meteo.com/v1/search";

    public MeteoClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(4))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // # Function to get current temperature for a city
    @Override
    public double getCurrentTemperature(String city) {
        // 1. Fetch city coordinates
        CityCoordinates coords = fetchCoordinates(city);

        // 2. Return temperature
        return fetchCityCurrentTemperature(coords);
    }

    // # Function to fetch city coordinates from geocoding API
    private CityCoordinates fetchCoordinates(String city) {
        try {

            // 1. Construct full API URL with query parameters
            String fullUrl = String.format("%s?name=%s&count=1&language=en&format=json",
                    GEOCODING_URL, city.replace(" ", "+"));

            // 2. Send request
            String responseBody = sendRequest(fullUrl);

            // 3. Parse JSON response into DTO and validate dto
            GeocodingResponse dto = objectMapper.readValue(responseBody, GeocodingResponse.class);

            if (dto == null || dto.results() == null || dto.results().isEmpty()) {
                throw new RuntimeException("City not found: " + city);
            }

            // 4. Get first result from array
            var firstResult = dto.results().getFirst();

            // 5. Return city coordinates
            return new CityCoordinates(firstResult.latitude(), firstResult.longitude());

        } catch (ConnectException e) {
            throw new RuntimeException("Failed to connect to the geocoding service", e);
        } catch (HttpConnectTimeoutException e) {
            throw new RuntimeException("Connection timed out while trying to fetch geocoding data", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // # Function to fetch current temperature for given city coordinates
    private double fetchCityCurrentTemperature(CityCoordinates coords) {
        try {
            // 1. Construct full API URL with query parameters
            String fullUrl = String.format("%s?latitude=%s&longitude=%s&current_weather=true",
                    WEATHER_URL, coords.latitude(), coords.longitude());

            // 2. Send request
            String responseBody = sendRequest(fullUrl);

            // 3. Parse JSON response into DTO and validate dto
            MeteoResponse dto = objectMapper.readValue(responseBody, MeteoResponse.class);

            if (dto == null || dto.currentWeather() == null) {
                throw new RuntimeException("Invalid response format from Weather API");
            }

            // 4. Extract and return current temperature
            return dto.currentWeather().temperature();

        } catch (ConnectException e) {
            throw new RuntimeException("Failed to connect to the weather service", e);
        } catch (HttpConnectTimeoutException e) {
            throw new RuntimeException("Connection timed out while trying to fetch weather data", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // # Helper function to send HTTP GET request and return response body
    private String sendRequest(String url) throws Exception {
        // 1. Create HTTP GET request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(8))
                .GET()
                .build();

        // 2. Send request
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("API Error: HTTP " + response.statusCode());
        }

        // 3. Return response body
        return response.body();
    }
}
