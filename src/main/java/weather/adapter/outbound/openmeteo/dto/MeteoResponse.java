package weather.adapter.outbound.openmeteo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MeteoResponse(
        @JsonProperty("current_weather")
        CurrentWeatherDTO currentWeather
) {}
