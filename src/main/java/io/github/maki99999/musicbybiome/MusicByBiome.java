package io.github.maki99999.musicbybiome;

import io.github.maki99999.musicbybiome.music.MusicProvider;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.github.maki99999.musicbybiome.MusicByBiome.MOD_ID;

@Mod(MOD_ID)
public class MusicByBiome {
    public static final String MOD_ID = "musicbybiome";
    public static final Logger LOGGER = LogManager.getLogger();

    public MusicByBiome() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        MusicProvider.register(modEventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
    }

    private void setup(final FMLClientSetupEvent event) {
        LOGGER.info(MOD_ID + " mod initialized");
        MusicProvider.init();
    }
}
