package io.github.maki99999.musicbybiome;

import com.cupboard.config.CupboardConfig;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.github.maki99999.musicbybiome.MusicByBiome.MOD_ID;

@Mod(MOD_ID)
public class MusicByBiome {
    public static final String MOD_ID = "musicbybiome";
    public static final Logger LOGGER = LogManager.getLogger();
    public static CupboardConfig<Config> config = new CupboardConfig<>(MOD_ID, new Config());

    public MusicByBiome() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        MusicProvider.register(modEventBus);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info(MOD_ID + " mod initialized");
        MusicProvider.init();
    }
}
