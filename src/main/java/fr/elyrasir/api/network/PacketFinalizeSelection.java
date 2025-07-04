package fr.elyrasir.api.network;

import fr.elyrasir.api.claims.LandManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketFinalizeSelection {

    private final String name;
    private final List<BlockPos> points;

    public PacketFinalizeSelection(String name, List<BlockPos> points) {
        this.name = name;
        this.points = points;
    }

    public static void encode(PacketFinalizeSelection msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.name);
        buf.writeInt(msg.points.size());
        for (BlockPos pos : msg.points) {
            buf.writeBlockPos(pos);
        }
    }

    public static PacketFinalizeSelection decode(FriendlyByteBuf buf) {
        String name = buf.readUtf(32767);
        int size = buf.readInt();
        List<BlockPos> points = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            points.add(buf.readBlockPos());
        }
        return new PacketFinalizeSelection(name, points);
    }

    public static void handle(PacketFinalizeSelection msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null) {

                // 🔍 DEBUG
                System.out.println("[DEBUG] Serveur : réception du packet PacketFinalizeSelection");
                System.out.println("[DEBUG] Nom de la parcelle : " + msg.name);
                System.out.println("[DEBUG] Nombre de points : " + msg.points.size());
                for (BlockPos pos : msg.points) {
                    System.out.println("[DEBUG] Point reçu : " + pos);
                }

                LandManager.get().finalizeSelectionFromClient(player, msg.name, msg.points);
            }
        });
        ctx.get().setPacketHandled(true);
    }

}