package io.github.maki99999.biomebeats;

import io.github.maki99999.biomebeats.gui.DebugHud;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class BiomeBeats {
    public BiomeBeats() {
        BiomeBeatsCommon.init();
    }

    @EventBusSubscriber(value = Dist.CLIENT)
    static class ClientEvents {
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void eventRenderGameOverlayEvent(RenderGuiEvent.Post event) {
            DebugHud.onRenderHUD(event.getGuiGraphics());
        }

        @SubscribeEvent
        public static void eventClientTickEventPost(ClientTickEvent.Post event) {
            BiomeBeatsCommon.tick();
        }
    }

    @EventBusSubscriber(value = Dist.CLIENT)
    static class ModClientEvents {
        @SubscribeEvent
        public static void eventRegisterKeyMappingsEvent(RegisterKeyMappingsEvent event) {
            event.register(Constants.CONFIG_KEY_MAPPING);
            event.register(Constants.OPEN_DEBUG_SCREEN_KEY_MAPPING);
        }
    }
}