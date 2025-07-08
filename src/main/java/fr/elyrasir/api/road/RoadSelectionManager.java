package fr.elyrasir.api.road;

import fr.elyrasir.api.client.render.DigitParticleRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class RoadSelectionManager {
    private static final List<BlockPos> selectedPoints = new ArrayList<>();

    public static void addPoint(BlockPos pos) {
        if (!selectedPoints.contains(pos)) {
            selectedPoints.add(pos);
        }
    }

    public static void clearSelection() {
        selectedPoints.clear();
    }

    public static List<BlockPos> getSelection() {
        return selectedPoints;
    }

    public static int getSelectionSize() {
        return selectedPoints.size();
    }

    public static BlockPos getPoint(int index) {
        if (index >= 0 && index < selectedPoints.size()) {
            return selectedPoints.get(index);
        }
        return null;
    }

    public static List<BlockPos> getCopy() {
        return new ArrayList<>(selectedPoints);
    }

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level != null && mc.player != null && !selectedPoints.isEmpty()) {
            for (int i = 0; i < selectedPoints.size(); i++) {
                BlockPos pos = selectedPoints.get(i);
                DigitParticleRenderer.renderDigit(mc.level, pos.above(), i);
            }
        }
    }





}
