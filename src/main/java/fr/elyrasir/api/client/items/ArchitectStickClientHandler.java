package fr.elyrasir.api.client.items;

import fr.elyrasir.api.client.gui.NameParcelScreen;
import fr.elyrasir.api.network.ClientSelectionManager;
import fr.elyrasir.api.utils.PolygonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArchitectStickClientHandler {

    public static void openNamingScreen() {
        Minecraft mc = Minecraft.getInstance();

        if (!PolygonUtils.isValidPolygon(ClientSelectionManager.getFullSelection())) {
            if (mc.player != null && mc.level != null) {
                mc.player.displayClientMessage(
                        Component.literal("§cSélection invalide : forme incorrecte."), true
                );
                mc.level.playLocalSound(
                        mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                        SoundEvents.VILLAGER_NO,
                        SoundSource.PLAYERS,
                        1.0f, 1.0f,
                        false
                );
            }
            return;
        }

        mc.setScreen(new NameParcelScreen());
    }
}