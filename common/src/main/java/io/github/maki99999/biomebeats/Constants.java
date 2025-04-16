package io.github.maki99999.biomebeats;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.maki99999.biomebeats.biome.BiomeManager;
import io.github.maki99999.biomebeats.condition.ConditionManager;
import io.github.maki99999.biomebeats.conditionmusic.ConditionMusicManager;
import io.github.maki99999.biomebeats.config.ConfigIO;
import io.github.maki99999.biomebeats.music.MusicManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
    public static final String MOD_ID = "biomebeats";
    public static final String MOD_NAME = "BiomeBeats";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final String CONFIG_FILENAME = "biomebeats.json";
    public static final KeyMapping CONFIG_KEY_MAPPING = new KeyMapping("key.biomebeats.open_config",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, "key.biomebeats.categories.main");
    public static final KeyMapping OPEN_DEBUG_SCREEN_KEY_MAPPING = new KeyMapping("key.biomebeats.open_debug_screen",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.biomebeats.categories.main");
    public static final String MUSIC_FOLDER = "music";
    public static final Music EMPTY_MUSIC = new Music(new Holder.Direct<>(SoundEvents.EMPTY), Integer.MAX_VALUE,
            Integer.MAX_VALUE, true);
    public static final MusicManager MUSIC_MANAGER = new MusicManager();
    public static final ConditionManager CONDITION_MANAGER = new ConditionManager();
    public static final ConditionMusicManager CONDITION_MUSIC_MANAGER = new ConditionMusicManager();
    public static final ConfigIO CONFIG_IO = new ConfigIO();
    public static final BiomeManager BIOME_MANAGER = new BiomeManager();
    public static final String HELP_URL = "https://maki99999.github.io/biome-beats-wiki/";
}