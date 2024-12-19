package io.github.maki99999.biomebeats;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BiomeBeats {
    public BiomeBeats() {
        BiomeBeatsCommon.init();

        MinecraftForge.EVENT_BUS.addListener(BiomeBeats::onClientTick);
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        BiomeBeatsCommon.tick();
    }

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(Constants.CONFIG_KEY_MAPPING);
    }
}
