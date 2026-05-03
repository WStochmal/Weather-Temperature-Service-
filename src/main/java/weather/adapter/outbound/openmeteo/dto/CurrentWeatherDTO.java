package weather.adapter.outbound.openmeteo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CurrentWeatherDTO(
        double temperature
) {}
