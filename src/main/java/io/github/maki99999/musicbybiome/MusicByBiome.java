package io.github.maki99999.musicbybiome;

import com.cupboard.config.CupboardConfig;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.github.maki99999.musicbybiome.MusicByBiome.MODID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MODID)
public class MusicByBiome {
    public static final String MODID = "musicbybiome";
    public static final Logger LOGGER = LogManager.getLogger();
    public static CupboardConfig<Config> config = new CupboardConfig<>(MODID, new Config());

    public MusicByBiome() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "", (c, b) -> true));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info(MODID + " mod initialized");
    }
}
