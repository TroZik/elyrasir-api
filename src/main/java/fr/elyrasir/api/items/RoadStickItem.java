package fr.elyrasir.api.items;

import fr.elyrasir.api.client.items.RoadStickClientHandler;
import fr.elyrasir.api.client.render.DigitParticleRenderer;
import fr.elyrasir.api.network.ClientSelectionManager;
import fr.elyrasir.api.road.RoadSelectionManager;
import fr.elyrasir.api.road.RoadStickTypeManager;
import fr.elyrasir.api.utils.RoadValidationUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class RoadStickItem extends Item {

    public RoadStickItem(Properties properties) {
        super(properties);
    }

    // ✅ Clic droit dans le vide (ex: Shift + clic droit pour ouvrir le GUI)
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (level.isClientSide) {
                if (ClientSelectionManager.size() < 2) {
                    player.displayClientMessage(Component.literal("§cAjoutez plus de points !"), true);
                    return InteractionResultHolder.fail(stack);
                }

                // ➕ Place ici ta logique de vérification client-side (validation rapide)
                RoadValidationUtils.ValidationResult result = RoadValidationUtils.validateRoad(
                        ClientSelectionManager.getSelectedPoints(),
                        3, // ou une valeur par défaut
                        0  // ou slope par défaut
                );

                if (!result.valid()) {
                    player.displayClientMessage(Component.literal(result.errorMessage()), true);
                    return InteractionResultHolder.fail(stack);
                }

                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    RoadStickClientHandler.tryOpenNamingScreen();
                });
            }
            return InteractionResultHolder.success(stack);
        }


        return InteractionResultHolder.pass(stack);
    }

    // ✅ Clic droit sur un bloc (ajout du point uniquement)
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();

        if (player == null || !level.isClientSide) {
            return InteractionResult.PASS;
        }

        int currentMax = RoadStickTypeManager.getCurrentTypeMaxPoints();
        if (RoadSelectionManager.getSelectionSize() >= currentMax) {
            player.displayClientMessage(
                    Component.literal("§cTrop de points pour ce type de route."), true
            );
            return InteractionResult.FAIL;
        }

        RoadSelectionManager.addPoint(pos);
        int index = RoadSelectionManager.getSelectionSize() - 1;

        // Affichage des particules de numérotation
        DigitParticleRenderer.renderDigit(level, pos, index);

        player.displayClientMessage(
                Component.literal("Point " + (index + 1) + " ajouté : " + pos.toShortString()),
                true
        );

        return InteractionResult.SUCCESS;
    }

    private int getCurrentWidth() {
        return switch (RoadStickTypeManager.getCurrentTypeName()) {
            case "Rue" -> 3;
            case "Avenue" -> 5;
            case "Boulevard" -> 7;
            default -> 0;
        };
    }

    private double getCurrentSlope() {
        return switch (RoadStickTypeManager.getCurrentTypeName()) {
            case "Rue" -> 45.0;
            case "Avenue", "Boulevard" -> 22.5;
            default -> 0.0;
        };
    }
}



