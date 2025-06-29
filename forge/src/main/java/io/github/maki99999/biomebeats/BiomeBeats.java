package io.github.maki99999.biomebeats;

import io.github.maki99999.biomebeats.gui.DebugHud;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BiomeBeats {
    public BiomeBeats() {
        BiomeBeatsCommon.init();
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT)
    static class ClientEvents {
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void eventRenderGameOverlayEvent(CustomizeGuiOverlayEvent event) {
            DebugHud.onRenderHUD(event.getGuiGraphics());
        }

        @SubscribeEvent
        public static void eventClientTickEventPost(TickEvent.ClientTickEvent event) {
            BiomeBeatsCommon.tick();
        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    static class ModClientEvents {
        @SubscribeEvent
        public static void eventRegisterKeyMappingsEvent(RegisterKeyMappingsEvent event) {
            event.register(Constants.CONFIG_KEY_MAPPING);
            event.register(Constants.OPEN_DEBUG_SCREEN_KEY_MAPPING);
        }
    }
}