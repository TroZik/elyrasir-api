package fr.elyrasir.api.client.items;

import fr.elyrasir.api.client.gui.NameParcelScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class ArchitectStickClientHandler {
    public static void openNamingScreen() {
        Minecraft.getInstance().setScreen(new NameParcelScreen());
    }
}

