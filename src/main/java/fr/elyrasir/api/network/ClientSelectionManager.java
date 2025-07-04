package fr.elyrasir.api.network;

import fr.elyrasir.api.client.render.DigitParticleRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientSelectionManager {

    private static int frameCounter = 0;

    private static final List<BlockPos> selectedPoints = new ArrayList<>();

    public static void addPoint(BlockPos pos) {
        if (selectedPoints.size() < 16) { // Limite à 16 points
            selectedPoints.add(pos);

            // Affichage du chiffre en particules
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null && mc.player != null) {
                int index = selectedPoints.size() - 1;
                DigitParticleRenderer.renderDigit(mc.level, pos, index);
            }
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



    public static void tick() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level != null && mc.player != null && !selectedPoints.isEmpty()) {
            frameCounter++;
            if (frameCounter % 5 != 0) return; // ≈ toutes les 5 ticks

            for (int i = 0; i < selectedPoints.size(); i++) {
                BlockPos pos = selectedPoints.get(i);
                DigitParticleRenderer.renderDigit(mc.level, pos.above(), i);
            }
        }
    }
}
