package weather.port.outbound;

public interface WeatherProviderPort {
    double getCurrentTemperature(String city);
}
