package fr.elyrasir.api.client;

import fr.elyrasir.api.network.ClientSelectionManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "elyrasirapi", value = Dist.CLIENT)
public class ClientTickHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (event.phase == ClientTickEvent.Phase.END) {
            ClientSelectionManager.tick();
        }
    }
}
