package pl.nadoba.rpi.soil.moisture;

public class OutputNormalizer {

    public static String toPercent(double d) {
        double inputPercent = (d / 1023d) * 100d;
        double result = Math.abs(100 - inputPercent);
        return String.format("%.0f", result);
    }
}
