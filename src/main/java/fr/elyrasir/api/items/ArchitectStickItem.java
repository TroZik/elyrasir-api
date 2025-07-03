package fr.elyrasir.api.items;

import fr.elyrasir.api.claims.LandManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ArchitectStickItem extends Item {

    public ArchitectStickItem(Properties properties) {
        super(properties);
    }

    // Quand on clique sur un bloc avec l'item
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown() && level.isClientSide) {
            LandManager.get().openNamingScreen(player); // Ouvre l'écran de nommage
        }

        return InteractionResultHolder.success(stack);
    }

    // Quand on utilise l'item sur un bloc (clic droit sur bloc)
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();

        if (!level.isClientSide) {
            if (player.isShiftKeyDown()) {
                // LandManager.get().finalizeSelectionWithName(player); // ✅ Appel côté serveur uniquement
            } else {
                LandManager.get().selectPoint(player, pos); // ✅ Appel côté serveur uniquement
            }
        }

        return InteractionResult.SUCCESS;
    }

}
