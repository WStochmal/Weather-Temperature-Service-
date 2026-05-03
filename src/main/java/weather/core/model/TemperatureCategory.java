package weather.core.model;


public enum TemperatureCategory {
    FREEZING("Freezing"),
    COLD("Cold"),
    MILD("Mild"),
    WARM("Warm"),
    HOT("Hot");


    private final String label;


    TemperatureCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }


    public static TemperatureCategory categorizeTemperature(double temperature) {
        if (temperature < 0) return FREEZING;
        if (temperature <= 10) return COLD;
        if (temperature <= 20) return MILD;
        if (temperature <= 30) return WARM;
        return HOT;
    }
}
