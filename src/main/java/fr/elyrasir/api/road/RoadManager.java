package fr.elyrasir.api.road;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class RoadManager {

    private static final RoadManager INSTANCE = new RoadManager();

    public static RoadManager get() {
        return INSTANCE;
    }

    public void finalizeRoadFromClient(ServerPlayer player, String name, List<BlockPos> points) {
        // TODO : validation + ajout au fichier capitale.json
        System.out.println("[ROAD] Création de la route : " + name);
        System.out.println("[ROAD] Points reçus : " + points.size());

        // Ajout futur de la logique d’écriture JSON + vérifications
    }
}
