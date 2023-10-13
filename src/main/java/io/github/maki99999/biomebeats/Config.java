package io.github.maki99999.biomebeats;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = BiomeBeats.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    public static final List<String> TAGS = List.of(
            "is_hot/overworld",
            "is_cold/overworld",
            "is_sparse/overworld",
            "is_dense/overworld",
            "is_wet/overworld",
            "is_dry/overworld",
            "is_coniferous",
            "is_river",
            "is_spooky",
            "is_dead",
            "is_lush",
            "is_mushroom",
            "is_magical",
            "is_rare",
            "is_plateau",
            "is_modified",
            "is_water",
            "is_desert",
            "is_plains",
            "is_swamp",
            "is_sandy",
            "is_snowy",
            "is_wasteland",
            "is_void",
            "is_underground",
            "is_cave",
            "is_peak",
            "is_slope",
            "is_mountain",
            "is_beach",
            "is_forest",
            "is_ocean",
            "is_deep_ocean",
            "is_jungle",
            "is_nether",
            "is_end"
    );

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue DEBUG;

    private static final Map<String, ForgeConfigSpec.ConfigValue<List<? extends String>>> SONGS_PER_TAG = new HashMap<>();
    private static final Map<String, ForgeConfigSpec.ConfigValue<List<? extends String>>> SONGS_PER_TAG_LOW_PRIORITY = new HashMap<>();

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> MENU_SONGS;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> NIGHT_SONGS;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> RAIN_SONGS;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> GENERIC_SONGS;

    static final ForgeConfigSpec SPEC;

    static {
        BUILDER.push("Configs for the BiomeBeats mod");

        DEBUG = BUILDER.comment("debug mode").define("debug_mode", false);

        //special
        MENU_SONGS = BUILDER.comment("menu music")
                .defineListAllowEmpty("menu_songs", new ArrayList<>(), Config::validateItemName);
        NIGHT_SONGS = BUILDER.comment("night music (high priority)")
                .defineListAllowEmpty("night_songs", new ArrayList<>(), o -> o instanceof String);
        RAIN_SONGS = BUILDER.comment("rain music (high priority)")
                .defineListAllowEmpty("rain_songs", new ArrayList<>(), o -> o instanceof String);
        GENERIC_SONGS = BUILDER.comment("fallback music that plays when no other song can play (very low priority)")
                .defineListAllowEmpty("generic_songs", new ArrayList<>(), o -> o instanceof String);

        //tags
        for (var tag : TAGS) {
            if (tag.contains("/overworld"))
                SONGS_PER_TAG_LOW_PRIORITY.put(tag, BUILDER.comment("music for the tag '" + tag + "' (low priority)")
                        .defineListAllowEmpty("songs_" + tag.replace("/", "_"), new ArrayList<>(), o -> o instanceof String));
            else
                SONGS_PER_TAG.put(tag, BUILDER.comment("music for the tag '" + tag + "'")
                        .defineListAllowEmpty("songs_" + tag, new ArrayList<>(), o -> o instanceof String));
        }

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String;
    }

    public static boolean debug;

    public static Map<String, List<String>> songsPerTag;
    public static Map<String, List<String>> songsPerTagLowPriority;

    public static List<String> menuSongs;
    public static List<String> nightSongs;
    public static List<String> rainSongs;
    public static List<String> genericSongs;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        debug = DEBUG.get();

        songsPerTag = new HashMap<>();
        for (var entry : SONGS_PER_TAG.entrySet()) {
            songsPerTag.put(entry.getKey(), toLowerCaseList(entry.getValue().get()));
        }

        songsPerTagLowPriority = new HashMap<>();
        for (var entry : SONGS_PER_TAG_LOW_PRIORITY.entrySet()) {
            songsPerTagLowPriority.put(entry.getKey(), toLowerCaseList(entry.getValue().get()));
        }

        menuSongs = toLowerCaseList(MENU_SONGS.get());
        nightSongs = toLowerCaseList(NIGHT_SONGS.get());
        rainSongs = toLowerCaseList(RAIN_SONGS.get());
        genericSongs = toLowerCaseList(GENERIC_SONGS.get());
    }

    private static List<String> toLowerCaseList(List<? extends String> list) {
        return list.stream().map(String::toLowerCase).collect(Collectors.toList());
    }
}
