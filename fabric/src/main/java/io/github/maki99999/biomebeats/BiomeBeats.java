package io.github.maki99999.biomebeats;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class BiomeBeats implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BiomeBeatsCommon.init();

        KeyBindingHelper.registerKeyBinding(Constants.CONFIG_KEY_MAPPING);
        ClientTickEvents.END_CLIENT_TICK.register(BiomeBeats::onEndTick);
    }

    private static void onEndTick(Minecraft client) {
        BiomeBeatsCommon.tick();
    }
}