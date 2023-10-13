package io.github.maki99999.biomebeats;

import io.github.maki99999.biomebeats.music.MusicProvider;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import static io.github.maki99999.biomebeats.BiomeBeats.MOD_ID;

@Mod(MOD_ID)
public class BiomeBeats {
    public static final String MOD_ID = "biomebeats";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Random RANDOM = new Random();

    public BiomeBeats() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        MusicProvider.register(modEventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
    }

    private void setup(final FMLClientSetupEvent event) {
        LOGGER.info(MOD_ID + " mod initialized");
        MusicProvider.init();
    }

    public static void debugMsg(String msg) {
        if(Config.debug)
            LOGGER.info(msg);
    }
}
