package com.sapphic.efxmco;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import com.sapphic.efxmco.client.EpicFightCompatClientEvents;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = epicfightxminecolonies.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = epicfightxminecolonies.MODID, value = Dist.CLIENT)
public class epicfightxminecoloniesClient {
    public epicfightxminecoloniesClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        // RegisterPatchedRenderersEvent.AddEntity is an Epic Fight-native event, registered through
        // Epic Fight's own EventHook system rather than NeoForge's — see EpicFightCompatClientEvents.
        EpicFightCompatClientEvents.register();
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        epicfightxminecolonies.LOGGER.info("HELLO FROM CLIENT SETUP");
        epicfightxminecolonies.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
