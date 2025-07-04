package fr.elyrasir.api.network;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientSelectionManager {

    private static final List<BlockPos> selectedPoints = new ArrayList<>();

    public static void addPoint(BlockPos pos) {
        if (selectedPoints.size() < 16) { // Limite à 16 points
            selectedPoints.add(pos);
        }
    }

    public static List<BlockPos> getFullSelection() {
        return Collections.unmodifiableList(selectedPoints);
    }

    public static void clear() {
        selectedPoints.clear();
    }

    public static int size() {
        return selectedPoints.size();
    }

    public static boolean isComplete() {
        return selectedPoints.size() >= 3; // Exemple : on considère 3+ points = prêt à valider
    }
}
