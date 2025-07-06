package fr.elyrasir.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.elyrasir.api.items.ModItems;
import fr.elyrasir.api.network.ClientSelectionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "elyrasirapi", value = Dist.CLIENT)
public class ClientOverlayRenderer {

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null) return;

        // Vérifie si le joueur tient l'Architect Stick en main principale ou secondaire
        ItemStack main = mc.player.getMainHandItem();
        ItemStack off = mc.player.getOffhandItem();
        boolean holdingStick = main.is(ModItems.ARCHITECT_STICK.get()) || off.is(ModItems.ARCHITECT_STICK.get());

        if (!holdingStick) return;

        int index = ClientSelectionManager.getSelectedIndex();
        if (index < 1) return;

        Component msg = Component.literal("Point sélectionné : " + index);
        GuiGraphics guiGraphics = event.getGuiGraphics();
        PoseStack pose = guiGraphics.pose();

        pose.pushPose();

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int x = screenWidth / 2;
        int y = screenHeight - 55; // au-dessus de la hotbar

        guiGraphics.drawCenteredString(mc.font, msg, x, y, 0xFFFFFF);

        pose.popPose();
    }
}

