package fr.elyrasir.api.client.init;

import fr.elyrasir.api.items.ArchitectStickItem;
import net.minecraft.world.item.Item;

public class ModItemClientInit {
    public static Item createArchitectStick() {
        return new ArchitectStickItem(new Item.Properties().stacksTo(1));
    }
}