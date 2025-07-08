package fr.elyrasir.api.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec2;

import java.util.*;

public class RoadValidationUtils {

    private record Vec2(int x, int z) {}

    public static ValidationResult validateRoad(List<BlockPos> points, int width, double maxSlopeDeg) {
        ValidationResult result;

        result = checkSelfIntersection(points);
        if (!result.valid()) return result;

        result = checkWidthOverlap(points, width);
        if (!result.valid()) return result;

        result = checkSlope(points, maxSlopeDeg);
        if (!result.valid()) return result;

        return ValidationResult.success();
    }

    private static ValidationResult checkSelfIntersection(List<BlockPos> points) {
        System.out.println("[DEBUG] checkSelfIntersection appelé avec " + points.size() + " points");

        if (points == null || points.size() < 4) {
            return ValidationResult.success();
        }

        // Convertit les segments en blocs traversés
        Map<Integer, Set<Vec2>> segmentToBlocks = new HashMap<>();

        for (int i = 0; i < points.size() - 1; i++) {
            BlockPos start = points.get(i);
            BlockPos end = points.get(i + 1);
            Set<Vec2> traversed = computeTraversedBlocks(new Vec2(start.getX(), start.getZ()), new Vec2(end.getX(), end.getZ()));
            segmentToBlocks.put(i, traversed);
        }

        // Vérifie les chevauchements entre segments non consécutifs
        for (int i = 0; i < points.size() - 1; i++) {
            for (int j = i + 1; j < points.size() - 1; j++) {
                if (Math.abs(i - j) <= 1) continue; // segments consécutifs

                Set<Vec2> setA = segmentToBlocks.get(i);
                Set<Vec2> setB = segmentToBlocks.get(j);

                for (Vec2 b : setB) {
                    if (setA.contains(b)) {
                        return ValidationResult.invalid("§cErreur : croisement détecté entre les segments " +
                                i + " et " + j + " au bloc " + b);
                    }
                }
            }
        }

        return ValidationResult.success();
    }

    private static Set<Vec2> computeTraversedBlocks(Vec2 start, Vec2 end) {
        Set<Vec2> blocks = new HashSet<>();

        int x1 = start.x;
        int z1 = start.z;
        int x2 = end.x;
        int z2 = end.z;

        int dx = Math.abs(x2 - x1);
        int dz = Math.abs(z2 - z1);

        int sx = Integer.compare(x2, x1);
        int sz = Integer.compare(z2, z1);

        int err = dx - dz;

        int x = x1;
        int z = z1;

        while (true) {
            blocks.add(new Vec2(x, z));
            if (x == x2 && z == z2) break;
            int e2 = 2 * err;
            if (e2 > -dz) {
                err -= dz;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                z += sz;
            }
        }

        return blocks;
    }



    private static boolean segmentsIntersect2D(BlockPos a, BlockPos b, BlockPos c, BlockPos d) {
        return linesIntersect(a.getX(), a.getZ(), b.getX(), b.getZ(), c.getX(), c.getZ(), d.getX(), d.getZ());
    }

    private static boolean linesIntersect(int x1, int y1, int x2, int y2,
                                          int x3, int y3, int x4, int y4) {
        int d1 = direction(x3, y3, x4, y4, x1, y1);
        int d2 = direction(x3, y3, x4, y4, x2, y2);
        int d3 = direction(x1, y1, x2, y2, x3, y3);
        int d4 = direction(x1, y1, x2, y2, x4, y4);

        if (((d1 > 0 && d2 < 0) || (d1 < 0 && d2 > 0)) &&
                ((d3 > 0 && d4 < 0) || (d3 < 0 && d4 > 0))) {
            return true;
        }

        return (d1 == 0 && onSegment(x3, y3, x4, y4, x1, y1)) ||
                (d2 == 0 && onSegment(x3, y3, x4, y4, x2, y2)) ||
                (d3 == 0 && onSegment(x1, y1, x2, y2, x3, y3)) ||
                (d4 == 0 && onSegment(x1, y1, x2, y2, x4, y4));
    }

    private static int direction(int xi, int yi, int xj, int yj, int xk, int yk) {
        return (xk - xi) * (yj - yi) - (xj - xi) * (yk - yi);
    }

    private static boolean onSegment(int xi, int yi, int xj, int yj, int xk, int yk) {
        return Math.min(xi, xj) <= xk && xk <= Math.max(xi, xj) &&
                Math.min(yi, yj) <= yk && yk <= Math.max(yi, yj);
    }

    private static ValidationResult checkWidthOverlap(List<BlockPos> points, int width) {
        // À implémenter plus tard : vérifie si les zones latérales ne se croisent pas
        return ValidationResult.success();
    }

    private static ValidationResult checkSlope(List<BlockPos> points, double maxSlope) {
        // À implémenter plus tard : vérifie les différences de hauteur Y autorisées entre points
        return ValidationResult.success();
    }

    public static record ValidationResult(boolean valid, String errorMessage) {
        public static ValidationResult success() {
            return new ValidationResult(true, "");
        }

        public static ValidationResult invalid(String message) {
            return new ValidationResult(false, message);
        }
    }
}


