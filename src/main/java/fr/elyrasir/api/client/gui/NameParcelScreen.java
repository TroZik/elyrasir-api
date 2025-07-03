package fr.elyrasir.api.client.gui;

import fr.elyrasir.api.claims.LandManager;
import fr.elyrasir.api.network.PacketFinalizeSelection;
import fr.elyrasir.api.network.PacketHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Arrays;
import java.util.List;

public class NameParcelScreen extends Screen {

    private EditBox textField;
    private final Player player;

    public NameParcelScreen(Player player) {
        super(Component.literal("Nommer la parcelle"));
        this.player = player;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        textField = new EditBox(this.font, centerX - 100, centerY - 20, 200, 20, Component.literal("Nom de la parcelle"));
        this.addRenderableWidget(textField);

        Button confirmButton = Button.builder(Component.literal("Valider"), (btn) -> {
            String name = textField.getValue();
            if (!name.isBlank()) {
                List<BlockPos> points = LandManager.get().getFullSelection(player); // ✅ CORRECTION ICI
                PacketHandler.sendToServer(new PacketFinalizeSelection(name, points));
                Minecraft.getInstance().setScreen(null); // Ferme la GUI
            }
        }).bounds(centerX - 50, centerY + 10, 100, 20).build();


        this.addRenderableWidget(confirmButton);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        textField.render(graphics, mouseX, mouseY, partialTicks);

        BlockPos[] points = LandManager.get().getSelection(player);

        if (points != null && points.length >= 1 && points[0] != null) {
            graphics.drawString(this.font, "Pos1: " + points[0].toShortString(), 10, 10, 0xFFFFFF);
        }
        if (points != null && points.length >= 2 && points[1] != null) {
            graphics.drawString(this.font, "Pos2: " + points[1].toShortString(), 10, 25, 0xFFFFFF);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
