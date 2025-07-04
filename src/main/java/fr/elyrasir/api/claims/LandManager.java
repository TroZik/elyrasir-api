package fr.elyrasir.api.claims;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class LandManager {

    private static final LandManager INSTANCE = new LandManager();
    public static LandManager get() { return INSTANCE; }

    // Nouveau système : liste de sommets par joueur
    private final Map<UUID, List<BlockPos>> playerSelections = new HashMap<>();

    // Liste de toutes les parcelles existantes (à utiliser plus tard pour vérifier les chevauchements)
    private final List<List<BlockPos>> existingParcels = new ArrayList<>();

    /**
     * Sélectionne un point et l'ajoute à la liste du joueur
     */
    public void selectPoint(Player player, BlockPos pos) {
        UUID uuid = player.getUUID();
        List<BlockPos> current = playerSelections.getOrDefault(uuid, new ArrayList<>());

        if (current.size() >= 16) {
            player.sendSystemMessage(Component.literal("Nombre maximum de sommets atteint (16)."));
            return;
        }

        current.add(pos);
        playerSelections.put(uuid, current);

        player.sendSystemMessage(Component.literal("Point " + current.size() + " sélectionné: " + pos));
    }

    /**
     * Enregistre la parcelle avec le nom donné, après vérification
     */
    public void finalizeSelectionWithName(Player player, String name, List<BlockPos> points) {
        if (points == null || points.size() < 2) {
            player.sendSystemMessage(Component.literal("Veuillez sélectionner au moins 2 points."));
            return;
        }

        if (doesParcelOverlap(points, getVisibleParcels(player.level(), player.blockPosition(), 9999))) {
            player.sendSystemMessage(Component.literal("Erreur : la parcelle chevauche une autre existante."));
            return;
        }

        saveParcel(player.getServer(), name, points);
        existingParcels.add(points);
        player.sendSystemMessage(Component.literal("Parcelle '" + name + "' enregistrée avec " + points.size() + " sommets."));
    }


    /**
     * Sauvegarde une parcelle sous forme de JSON
     */
    private void saveParcel(MinecraftServer server, String name, List<BlockPos> vertices) {
        System.out.println("[DEBUG] finalizeSelectionFromClient() exécutée !");

        System.out.println("[DEBUG] Sauvegarde de la parcelle nommée : " + name);

        Path configPath = server.getWorldPath(LevelResource.ROOT)
                .resolve("serverconfig")
                .resolve("land_parcels.json");

        System.out.println("[Elyrasir] Writing parcel data to: " + configPath); // Debug

        File file = configPath.toFile();
        file.getParentFile().mkdirs();

        JsonObject parcel = new JsonObject();
        parcel.addProperty("name", name);

        JsonArray vertexArray = new JsonArray();
        for (BlockPos vertex : vertices) {
            vertexArray.add(toJson(vertex));
        }
        parcel.add("vertices", vertexArray);

        JsonArray array = new JsonArray();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                JsonElement existing = JsonParser.parseReader(reader);
                if (existing.isJsonArray()) {
                    array = existing.getAsJsonArray();
                }
            } catch (Exception e) {
                e.printStackTrace(); // Important pour debug
            }
        }

        array.add(parcel);

        try (FileWriter writer = new FileWriter(file, false)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(array, writer);
            System.out.println("[Elyrasir] Parcel saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Transforme un BlockPos en objet JSON
     */
    private JsonObject toJson(BlockPos pos) {
        JsonObject json = new JsonObject();
        json.addProperty("x", pos.getX());
        json.addProperty("y", pos.getY());
        json.addProperty("z", pos.getZ());
        return json;
    }

    /**
     * Stub temporaire : vérifier les chevauchements (à faire plus tard avec géométrie)
     */
    private boolean doesParcelOverlap(List<BlockPos> newParcel, List<List<BlockPos>> existingParcels) {
        if (newParcel == null || newParcel.size() < 2) return false;

        int newX1 = Math.min(newParcel.get(0).getX(), newParcel.get(1).getX());
        int newX2 = Math.max(newParcel.get(0).getX(), newParcel.get(1).getX());
        int newZ1 = Math.min(newParcel.get(0).getZ(), newParcel.get(1).getZ());
        int newZ2 = Math.max(newParcel.get(0).getZ(), newParcel.get(1).getZ());

        for (List<BlockPos> existing : existingParcels) {
            if (existing.size() < 2) continue;

            int exX1 = Math.min(existing.get(0).getX(), existing.get(1).getX());
            int exX2 = Math.max(existing.get(0).getX(), existing.get(1).getX());
            int exZ1 = Math.min(existing.get(0).getZ(), existing.get(1).getZ());
            int exZ2 = Math.max(existing.get(0).getZ(), existing.get(1).getZ());

            boolean overlap = newX1 <= exX2 && newX2 >= exX1 && newZ1 <= exZ2 && newZ2 >= exZ1;
            if (overlap) return true;
        }

        return false;
    }


    /**
     * Affiche le GUI côté client
     */
    @OnlyIn(Dist.CLIENT)
    public void openNamingScreen(Player player) {
        Minecraft.getInstance().setScreen(new fr.elyrasir.api.client.gui.NameParcelScreen());
    }

    /**
     * Récupère la sélection actuelle du joueur (client)
     */
    @OnlyIn(Dist.CLIENT)
    public BlockPos[] getSelection(Player player) {
        List<BlockPos> list = playerSelections.get(player.getUUID());
        if (list == null || list.size() != 2) {
            return new BlockPos[0];
        }
        return list.toArray(new BlockPos[0]);
    }


    @OnlyIn(Dist.CLIENT)
    public List<BlockPos> getFullSelection(Player player) {
        return new ArrayList<>(playerSelections.getOrDefault(player.getUUID(), new ArrayList<>()));
    }




    //méthode de récupération des parcelles à afficher
    @OnlyIn(Dist.CLIENT)
    public List<List<BlockPos>> getVisibleParcels(Level level, BlockPos center, int radius) {
        return existingParcels.stream()
                .filter(polygon -> polygon.stream().anyMatch(pos -> pos.closerThan(center, radius)))
                .toList();
    }

    @OnlyIn(Dist.CLIENT)
    public List<BlockPos[]> getNearbyParcels(Player player, int radius) {
        List<BlockPos[]> nearby = new ArrayList<>();
        MinecraftServer server = player.getServer();
        if (server == null) return nearby;

        File file = server.getLevel(Level.OVERWORLD)
                .getServer()
                .getWorldPath(LevelResource.ROOT)
                .resolve("serverconfig/land_parcels.json")
                .toFile();

        if (!file.exists()) return nearby;

        try (FileReader reader = new FileReader(file)) {
            JsonElement element = JsonParser.parseReader(reader);
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (JsonElement e : array) {
                    JsonObject obj = e.getAsJsonObject();
                    JsonArray verticesJson = obj.getAsJsonArray("vertices");
                    if (verticesJson == null || verticesJson.size() < 2) continue;

                    List<BlockPos> vertexList = new ArrayList<>();
                    for (JsonElement vert : verticesJson) {
                        vertexList.add(fromJson(vert.getAsJsonObject()));
                    }

                    // Calcule un centre moyen pour comparer la distance
                    BlockPos center = getPolygonCenter(vertexList);
                    if (center.closerThan(player.blockPosition(), radius)) {
                        nearby.add(vertexList.toArray(new BlockPos[0]));
                    }
                }
            }
        } catch (IOException ignored) {}

        return nearby;
    }

    private BlockPos getPolygonCenter(List<BlockPos> points) {
        int x = 0, y = 0, z = 0;
        for (BlockPos p : points) {
            x += p.getX();
            y += p.getY();
            z += p.getZ();
        }
        int n = points.size();
        return new BlockPos(x / n, y / n, z / n);
    }


    private BlockPos fromJson(JsonObject json) {
        return new BlockPos(json.get("x").getAsInt(), json.get("y").getAsInt(), json.get("z").getAsInt());
    }

    public void finalizeSelectionFromClient(Player player, String name, List<BlockPos> points) {
        if (points == null || points.size() < 2) {
            player.sendSystemMessage(Component.literal("Veuillez sélectionner au moins 2 points."));
            return;
        }

        // detection de colision de parcelle désactivé car client side
       /*
        if (doesParcelOverlap(points, getVisibleParcels(player.level(), player.blockPosition(), 9999))) {
            player.sendSystemMessage(Component.literal("Erreur : la parcelle chevauche une autre existante."));
            return;
        }
         */
        System.out.println("[DEBUG] saveParcel() appelée !");



        saveParcel(player.getServer(), name, points);
        existingParcels.add(points);
        player.sendSystemMessage(Component.literal("Parcelle '" + name + "' enregistrée avec " + points.size() + " sommets."));
    }






}
