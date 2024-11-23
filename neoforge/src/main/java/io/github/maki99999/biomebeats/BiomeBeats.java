package io.github.maki99999.biomebeats;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Constants.MOD_ID)
@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BiomeBeats {
    public BiomeBeats(IEventBus eventBus) {
        BiomeBeatsCommon.init();

        NeoForge.EVENT_BUS.addListener(BiomeBeats::onClientTick);
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        BiomeBeatsCommon.tick();
    }

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(Constants.CONFIG_KEY_MAPPING);
    }
}
