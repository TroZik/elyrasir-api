package fr.elyrasir.api.road;

public class RoadStickTypeManager {
    private static final String[] TYPES = {"Rue", "Avenue", "Boulevard", "Modifier"};
    private static int index = 0;

    public static void cycleType() {
        index = (index + 1) % TYPES.length;
    }

    public static String getCurrentTypeName() {
        return TYPES[index];
    }

    public static int getCurrentIndex() {
        return index;
    }

    public static void reset() {
        index = 0;
    }

    public static int getCurrentTypeMaxPoints() {
        return switch (TYPES[index]) {
            case "Rue" -> 128;
            case "Avenue" -> 512;
            case "Boulevard" -> 2048;
            default -> 0; // Pour "Modifier" ou toute valeur inattendue
        };
    }



}