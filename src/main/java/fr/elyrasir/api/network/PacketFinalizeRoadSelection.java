package fr.elyrasir.api.network;

import fr.elyrasir.api.road.RoadManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketFinalizeRoadSelection {
    private final String roadName;
    private final List<BlockPos> points;

    public PacketFinalizeRoadSelection(String roadName, List<BlockPos> points) {
        this.roadName = roadName;
        this.points = points;
    }

    public static void encode(PacketFinalizeRoadSelection msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.roadName);
        buf.writeInt(msg.points.size());
        for (BlockPos pos : msg.points) {
            buf.writeBlockPos(pos);
        }
    }

    public static PacketFinalizeRoadSelection decode(FriendlyByteBuf buf) {
        String name = buf.readUtf(32767);
        int size = buf.readInt();
        List<BlockPos> points = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            points.add(buf.readBlockPos());
        }
        return new PacketFinalizeRoadSelection(name, points);
    }

    public static void handle(PacketFinalizeRoadSelection msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                RoadManager.get().finalizeRoadFromClient(player, msg.roadName, msg.points);
                PacketHandler.sendToClient(player, new PacketResetSelection());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

