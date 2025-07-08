package fr.elyrasir.api.client.items;

import com.mojang.blaze3d.platform.InputConstants;
import fr.elyrasir.api.client.gui.NameParcelScreen;
import fr.elyrasir.api.items.ModItems;
import fr.elyrasir.api.network.ClientSelectionManager;
import fr.elyrasir.api.network.PacketFinalizeRoadSelection;
import fr.elyrasir.api.network.PacketHandler;
import fr.elyrasir.api.road.RoadStickTypeManager;
import fr.elyrasir.api.utils.RoadValidationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = "elyrasirapi", value = Dist.CLIENT)
public class RoadStickClientHandler {

    private static boolean wasClicking = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.screen != null) return;

        LocalPlayer player = mc.player;
        ItemStack held = player.getMainHandItem();

        if (!held.is(ModItems.ROAD_STICK.get())) return;

        boolean shiftDown =
                InputConstants.isKeyDown(mc.getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) ||
                        InputConstants.isKeyDown(mc.getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT);

        boolean leftClick =
                GLFW.glfwGetMouseButton(mc.getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;

        if (shiftDown && leftClick && !wasClicking) {
            RoadStickTypeManager.cycleType();
            String typeName = RoadStickTypeManager.getCurrentTypeName();
            player.displayClientMessage(Component.literal("Type sélectionné : " + typeName), true);
        }

        wasClicking = leftClick;
    }

    public static void tryOpenNamingScreen() {
        Minecraft mc = Minecraft.getInstance();
        String roadName = "temp road"; // obtenu depuis l’UI plus tard
        List<BlockPos> points = ClientSelectionManager.getSelectedPoints();

        PacketHandler.sendToServer(new PacketFinalizeRoadSelection(roadName, points));

        if (mc.player == null || mc.level == null) return;

        var validation = RoadValidationUtils.validateRoad(

                ClientSelectionManager.getSelectedPoints(),
                getCurrentWidth(),
                getCurrentSlope()
        );

        if (!validation.valid()) {
            mc.player.displayClientMessage(
                    Component.literal("§c" + validation.errorMessage()), true
            );
            mc.level.playLocalSound(
                    mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                    SoundEvents.VILLAGER_NO,
                    SoundSource.PLAYERS,
                    1.0f, 1.0f,
                    false
            );
            return;
        }

        mc.setScreen(new NameParcelScreen());
    }

    private static int getCurrentWidth() {
        return switch (RoadStickTypeManager.getCurrentTypeName()) {
            case "Rue" -> 3;
            case "Avenue" -> 5;
            case "Boulevard" -> 7;
            default -> 0;
        };
    }

    private static double getCurrentSlope() {
        return switch (RoadStickTypeManager.getCurrentTypeName()) {
            case "Rue" -> Math.tan(Math.toRadians(45));
            case "Avenue", "Boulevard" -> Math.tan(Math.toRadians(22.5));
            default -> 0;
        };
    }

}
