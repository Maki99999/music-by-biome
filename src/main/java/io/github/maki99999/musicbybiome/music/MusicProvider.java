package io.github.maki99999.musicbybiome.music;

import io.github.maki99999.musicbybiome.Config;
import io.github.maki99999.musicbybiome.MusicByBiome;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MusicProvider {
    private static boolean isInitialized = false;

    private static final Map<String, CustomMusic> musicByName = new HashMap<>();

    private static final DeferredRegister<SoundEvent> MUSIC_SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MusicByBiome.MOD_ID);

    //<editor-fold defaultstate="collapsed" desc="all sound event registrations">
    //public static RegistryObject<SoundEvent> test = registerSoundEvent("test");
    private static final RegistryObject<SoundEvent> m_030CraggyCoast = registerSoundEvent("030craggycoast");
    private static final RegistryObject<SoundEvent> m_032MtHorn = registerSoundEvent("032mthorn");
    private static final RegistryObject<SoundEvent> m_033FoggyForest = registerSoundEvent("033foggyforest");
    private static final RegistryObject<SoundEvent> m_034SteamCave = registerSoundEvent("034steamcave");
    private static final RegistryObject<SoundEvent> m_035UpperSteamCave = registerSoundEvent("035uppersteamcave");
    private static final RegistryObject<SoundEvent> m_036AmpPlains = registerSoundEvent("036ampplains");
    private static final RegistryObject<SoundEvent> m_037FarAmpPlains = registerSoundEvent("037farampplains");
    private static final RegistryObject<SoundEvent> m_040NorthernDesert = registerSoundEvent("040northerndesert");
    private static final RegistryObject<SoundEvent> m_041QuicksandCave = registerSoundEvent("041quicksandcave");
    private static final RegistryObject<SoundEvent> m_042QuicksandPit = registerSoundEvent("042quicksandpit");
    private static final RegistryObject<SoundEvent> m_043CrystalCave = registerSoundEvent("043crystalcave");
    private static final RegistryObject<SoundEvent> m_045EndOfTheDay = registerSoundEvent("045endoftheday");
    private static final RegistryObject<SoundEvent> m_046InTheFuture = registerSoundEvent("046inthefuture");
    private static final RegistryObject<SoundEvent> m_047PlanetParalysis = registerSoundEvent("047planetparalysis");
    private static final RegistryObject<SoundEvent> m_048ChasmCave = registerSoundEvent("048chasmcave");
    private static final RegistryObject<SoundEvent> m_052DuskForest = registerSoundEvent("052duskforest");
    private static final RegistryObject<SoundEvent> m_055TreeshroudForest = registerSoundEvent("055treeshroudforest");
    private static final RegistryObject<SoundEvent> m_058HiddenLand = registerSoundEvent("058hiddenland");
    private static final RegistryObject<SoundEvent> m_062ThroughTheSeaOfTime = registerSoundEvent("062throughtheseaoftime");
    private static final RegistryObject<SoundEvent> m_064TemporalTower = registerSoundEvent("064temporaltower");
    private static final RegistryObject<SoundEvent> m_079DoYourBest = registerSoundEvent("079doyourbest");
    private static final RegistryObject<SoundEvent> m_080ShayminVillage = registerSoundEvent("080shayminvillage");
    private static final RegistryObject<SoundEvent> m_081SkyPeakForest = registerSoundEvent("081skypeakforest");
    private static final RegistryObject<SoundEvent> m_082SkyPeakCave = registerSoundEvent("082skypeakcave");
    private static final RegistryObject<SoundEvent> m_083SkyPeakPrairie = registerSoundEvent("083skypeakprairie");
    private static final RegistryObject<SoundEvent> m_085SkyPeakSnowfield = registerSoundEvent("085skypeaksnowfield");
    private static final RegistryObject<SoundEvent> m_086SkyPeakFinalPass = registerSoundEvent("086skypeakfinalpass");
    private static final RegistryObject<SoundEvent> m_088SurroundedSea = registerSoundEvent("088surroundedsea");
    private static final RegistryObject<SoundEvent> m_089MiracleSea = registerSoundEvent("089miraclesea");
    private static final RegistryObject<SoundEvent> m_090AegisCave = registerSoundEvent("090aegiscave");
    private static final RegistryObject<SoundEvent> m_092ConcealedRuins = registerSoundEvent("092concealedruins");
    private static final RegistryObject<SoundEvent> m_096DarkCrater = registerSoundEvent("096darkcrater");
    private static final RegistryObject<SoundEvent> m_097DeepDarkCrater = registerSoundEvent("097deepdarkcrater");
    private static final RegistryObject<SoundEvent> m_100RandomDungeonTheme3 = registerSoundEvent("100randomdungeontheme3");
    private static final RegistryObject<SoundEvent> m_106StarCave = registerSoundEvent("106starcave");
    private static final RegistryObject<SoundEvent> m_111FortuneRavine = registerSoundEvent("111fortuneravine");
    private static final RegistryObject<SoundEvent> m_120SouthernJungle = registerSoundEvent("120southernjungle");
    private static final RegistryObject<SoundEvent> m_127BarrenValley = registerSoundEvent("127barrenvalley");
    private static final RegistryObject<SoundEvent> m_128DarkWasteland = registerSoundEvent("128darkwasteland");
    private static final RegistryObject<SoundEvent> m_135VastIceMountainPeak = registerSoundEvent("135vasticemountainpeak");
    private static final RegistryObject<SoundEvent> m_136InTheMorningSun = registerSoundEvent("136inthemorningsun");
    private static final RegistryObject<SoundEvent> m_166GoodnightAlternate = registerSoundEvent("166goodnightalternate");
    private static final RegistryObject<SoundEvent> m_AmitySquare = registerSoundEvent("amitysquare");
    private static final RegistryObject<SoundEvent> m_BeginningDimension = registerSoundEvent("beginningdimension");
    private static final RegistryObject<SoundEvent> m_Bicycle = registerSoundEvent("bicycle");
    private static final RegistryObject<SoundEvent> m_CanalaveCity = registerSoundEvent("canalavecity");
    private static final RegistryObject<SoundEvent> m_DawnTheme = registerSoundEvent("dawntheme");
    private static final RegistryObject<SoundEvent> m_DeepWithinTeamGalacticHQ = registerSoundEvent("deepwithinteamgalactichq");
    private static final RegistryObject<SoundEvent> m_DistortionWorld = registerSoundEvent("distortionworld");
    private static final RegistryObject<SoundEvent> m_Ending = registerSoundEvent("ending");
    private static final RegistryObject<SoundEvent> m_EternaCity = registerSoundEvent("eternacity");
    private static final RegistryObject<SoundEvent> m_EternaForest = registerSoundEvent("eternaforest");
    private static final RegistryObject<SoundEvent> m_FightArea = registerSoundEvent("fightarea");
    private static final RegistryObject<SoundEvent> m_FloaromaTown = registerSoundEvent("floaromatown");
    private static final RegistryObject<SoundEvent> m_GameCorner = registerSoundEvent("gamecorner");
    private static final RegistryObject<SoundEvent> m_GlobalTradeStation = registerSoundEvent("globaltradestation");
    private static final RegistryObject<SoundEvent> m_GreatMarsh = registerSoundEvent("greatmarsh");
    private static final RegistryObject<SoundEvent> m_HearthomeCity = registerSoundEvent("hearthomecity");
    private static final RegistryObject<SoundEvent> m_HearthomeCityNight = registerSoundEvent("hearthomecitynight");
    private static final RegistryObject<SoundEvent> m_JubilifeCity = registerSoundEvent("jubilifecity");
    private static final RegistryObject<SoundEvent> m_JubilifeCityNight = registerSoundEvent("jubilifecitynight");
    private static final RegistryObject<SoundEvent> m_Lake = registerSoundEvent("lake");
    private static final RegistryObject<SoundEvent> m_LucasTheme = registerSoundEvent("lucastheme");
    private static final RegistryObject<SoundEvent> m_MenuMusic = registerSoundEvent("menumusic");
    private static final RegistryObject<SoundEvent> m_MtCoronet = registerSoundEvent("mtcoronet");
    private static final RegistryObject<SoundEvent> m_OldChateau = registerSoundEvent("oldchateau");
    private static final RegistryObject<SoundEvent> m_OreburghCity = registerSoundEvent("oreburghcity");
    private static final RegistryObject<SoundEvent> m_OreburghMine = registerSoundEvent("oreburghmine");
    private static final RegistryObject<SoundEvent> m_Poffin = registerSoundEvent("poffin");
    private static final RegistryObject<SoundEvent> m_PokemonCenter = registerSoundEvent("pokemoncenter");
    private static final RegistryObject<SoundEvent> m_PokemonLeague = registerSoundEvent("pokemonleague");
    private static final RegistryObject<SoundEvent> m_Route201 = registerSoundEvent("route201");
    private static final RegistryObject<SoundEvent> m_Route203 = registerSoundEvent("route203");
    private static final RegistryObject<SoundEvent> m_Route205 = registerSoundEvent("route205");
    private static final RegistryObject<SoundEvent> m_Route206 = registerSoundEvent("route206");
    private static final RegistryObject<SoundEvent> m_Route209 = registerSoundEvent("route209");
    private static final RegistryObject<SoundEvent> m_Route210 = registerSoundEvent("route210");
    private static final RegistryObject<SoundEvent> m_Route216 = registerSoundEvent("route216");
    private static final RegistryObject<SoundEvent> m_Route225 = registerSoundEvent("route225");
    private static final RegistryObject<SoundEvent> m_SandgemTown = registerSoundEvent("sandgemtown");
    private static final RegistryObject<SoundEvent> m_SnowpointCity = registerSoundEvent("snowpointcity");
    private static final RegistryObject<SoundEvent> m_SolaceonTown = registerSoundEvent("solaceontown");
    private static final RegistryObject<SoundEvent> m_SpearPillar = registerSoundEvent("spearpillar");
    private static final RegistryObject<SoundEvent> m_StarkMountain = registerSoundEvent("starkmountain");
    private static final RegistryObject<SoundEvent> m_SunyshoreCity = registerSoundEvent("sunyshorecity");
    private static final RegistryObject<SoundEvent> m_Surf = registerSoundEvent("surf");
    private static final RegistryObject<SoundEvent> m_TeamGalacticBuilding = registerSoundEvent("teamgalacticbuilding");
    private static final RegistryObject<SoundEvent> m_TwinleafTown = registerSoundEvent("twinleaftown");
    private static final RegistryObject<SoundEvent> m_UndergroundPassage = registerSoundEvent("undergroundpassage");
    private static final RegistryObject<SoundEvent> m_ValorLakefront = registerSoundEvent("valorlakefront");
    private static final RegistryObject<SoundEvent> m_VeilstoneCity = registerSoundEvent("veilstonecity");
    private static final RegistryObject<SoundEvent> m_Villa = registerSoundEvent("villa");
    private static final RegistryObject<SoundEvent> m_003Welcome = registerSoundEvent("003welcome");
    private static final RegistryObject<SoundEvent> m_010Goodnight = registerSoundEvent("010goodnight");
    private static final RegistryObject<SoundEvent> m_014TreasureTown = registerSoundEvent("014treasuretown");
    private static final RegistryObject<SoundEvent> m_015Heartwarming = registerSoundEvent("015heartwarming");
    private static final RegistryObject<SoundEvent> m_020TimeGearRemix = registerSoundEvent("020timegearremix");
    private static final RegistryObject<SoundEvent> m_024WaterfallCave = registerSoundEvent("024waterfallcave");
    private static final RegistryObject<SoundEvent> m_026TeamSkull = registerSoundEvent("026teamskull");
    private static final RegistryObject<SoundEvent> m_027SpindaCafe = registerSoundEvent("027spindacafe");
    private static final RegistryObject<SoundEvent> m_029AppleWoods = registerSoundEvent("029applewoods");
//</editor-fold>

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation location = new ResourceLocation(MusicByBiome.MOD_ID, name);
        return MUSIC_SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(location));
    }

    public static void register(IEventBus eventBus) {
        MUSIC_SOUND_EVENTS.register(eventBus);
    }

    public static void init() {
        //noinspection RedundantTypeArguments (explicit type arguments speedup compilation and analysis time)
        Map<String, RegistryObject<SoundEvent>> allSoundEvents = Map.<String, RegistryObject<SoundEvent>>ofEntries(
                Map.entry("030CraggyCoast", m_030CraggyCoast),
                Map.entry("032MtHorn", m_032MtHorn),
                Map.entry("033FoggyForest", m_033FoggyForest),
                Map.entry("034SteamCave", m_034SteamCave),
                Map.entry("035UpperSteamCave", m_035UpperSteamCave),
                Map.entry("036AmpPlains", m_036AmpPlains),
                Map.entry("037FarAmpPlains", m_037FarAmpPlains),
                Map.entry("040NorthernDesert", m_040NorthernDesert),
                Map.entry("041QuicksandCave", m_041QuicksandCave),
                Map.entry("042QuicksandPit", m_042QuicksandPit),
                Map.entry("043CrystalCave", m_043CrystalCave),
                Map.entry("045EndOfTheDay", m_045EndOfTheDay),
                Map.entry("046InTheFuture", m_046InTheFuture),
                Map.entry("047PlanetParalysis", m_047PlanetParalysis),
                Map.entry("048ChasmCave", m_048ChasmCave),
                Map.entry("052DuskForest", m_052DuskForest),
                Map.entry("055TreeshroudForest", m_055TreeshroudForest),
                Map.entry("058HiddenLand", m_058HiddenLand),
                Map.entry("062ThroughTheSeaOfTime", m_062ThroughTheSeaOfTime),
                Map.entry("064TemporalTower", m_064TemporalTower),
                Map.entry("079DoYourBest", m_079DoYourBest),
                Map.entry("080ShayminVillage", m_080ShayminVillage),
                Map.entry("081SkyPeakForest", m_081SkyPeakForest),
                Map.entry("082SkyPeakCave", m_082SkyPeakCave),
                Map.entry("083SkyPeakPrairie", m_083SkyPeakPrairie),
                Map.entry("085SkyPeakSnowfield", m_085SkyPeakSnowfield),
                Map.entry("086SkyPeakFinalPass", m_086SkyPeakFinalPass),
                Map.entry("088SurroundedSea", m_088SurroundedSea),
                Map.entry("089MiracleSea", m_089MiracleSea),
                Map.entry("090AegisCave", m_090AegisCave),
                Map.entry("092ConcealedRuins", m_092ConcealedRuins),
                Map.entry("096DarkCrater", m_096DarkCrater),
                Map.entry("097DeepDarkCrater", m_097DeepDarkCrater),
                Map.entry("100RandomDungeonTheme3", m_100RandomDungeonTheme3),
                Map.entry("106StarCave", m_106StarCave),
                Map.entry("111FortuneRavine", m_111FortuneRavine),
                Map.entry("120SouthernJungle", m_120SouthernJungle),
                Map.entry("127BarrenValley", m_127BarrenValley),
                Map.entry("128DarkWasteland", m_128DarkWasteland),
                Map.entry("135VastIceMountainPeak", m_135VastIceMountainPeak),
                Map.entry("136InTheMorningSun", m_136InTheMorningSun),
                Map.entry("166GoodnightAlternate", m_166GoodnightAlternate),
                Map.entry("AmitySquare", m_AmitySquare),
                Map.entry("BeginningDimension", m_BeginningDimension),
                Map.entry("Bicycle", m_Bicycle),
                Map.entry("CanalaveCity", m_CanalaveCity),
                Map.entry("DawnTheme", m_DawnTheme),
                Map.entry("DeepWithinTeamGalacticHQ", m_DeepWithinTeamGalacticHQ),
                Map.entry("DistortionWorld", m_DistortionWorld),
                Map.entry("Ending", m_Ending),
                Map.entry("EternaCity", m_EternaCity),
                Map.entry("EternaForest", m_EternaForest),
                Map.entry("FightArea", m_FightArea),
                Map.entry("FloaromaTown", m_FloaromaTown),
                Map.entry("GameCorner", m_GameCorner),
                Map.entry("GlobalTradeStation", m_GlobalTradeStation),
                Map.entry("GreatMarsh", m_GreatMarsh),
                Map.entry("HearthomeCity", m_HearthomeCity),
                Map.entry("HearthomeCityNight", m_HearthomeCityNight),
                Map.entry("JubilifeCity", m_JubilifeCity),
                Map.entry("JubilifeCityNight", m_JubilifeCityNight),
                Map.entry("Lake", m_Lake),
                Map.entry("LucasTheme", m_LucasTheme),
                Map.entry("MenuMusic", m_MenuMusic),
                Map.entry("MtCoronet", m_MtCoronet),
                Map.entry("OldChateau", m_OldChateau),
                Map.entry("OreburghCity", m_OreburghCity),
                Map.entry("OreburghMine", m_OreburghMine),
                Map.entry("Poffin", m_Poffin),
                Map.entry("PokemonCenter", m_PokemonCenter),
                Map.entry("PokemonLeague", m_PokemonLeague),
                Map.entry("Route201", m_Route201),
                Map.entry("Route203", m_Route203),
                Map.entry("Route205", m_Route205),
                Map.entry("Route206", m_Route206),
                Map.entry("Route209", m_Route209),
                Map.entry("Route210", m_Route210),
                Map.entry("Route216", m_Route216),
                Map.entry("Route225", m_Route225),
                Map.entry("SandgemTown", m_SandgemTown),
                Map.entry("SnowpointCity", m_SnowpointCity),
                Map.entry("SolaceonTown", m_SolaceonTown),
                Map.entry("SpearPillar", m_SpearPillar),
                Map.entry("StarkMountain", m_StarkMountain),
                Map.entry("SunyshoreCity", m_SunyshoreCity),
                Map.entry("Surf", m_Surf),
                Map.entry("TeamGalacticBuilding", m_TeamGalacticBuilding),
                Map.entry("TwinleafTown", m_TwinleafTown),
                Map.entry("UndergroundPassage", m_UndergroundPassage),
                Map.entry("ValorLakefront", m_ValorLakefront),
                Map.entry("VeilstoneCity", m_VeilstoneCity),
                Map.entry("Villa", m_Villa),
                Map.entry("003Welcome", m_003Welcome),
                Map.entry("010Goodnight", m_010Goodnight),
                Map.entry("014TreasureTown", m_014TreasureTown),
                Map.entry("015Heartwarming", m_015Heartwarming),
                Map.entry("020TimeGearRemix", m_020TimeGearRemix),
                Map.entry("024WaterfallCave", m_024WaterfallCave),
                Map.entry("026TeamSkull", m_026TeamSkull),
                Map.entry("027SpindaCafe", m_027SpindaCafe),
                Map.entry("029AppleWoods", m_029AppleWoods)
        );

        for (var entry : allSoundEvents.entrySet()) {
            musicByName.put(entry.getKey().toLowerCase(), new CustomMusic(entry.getValue().get()));
        }

        isInitialized = true;
    }

    @Nullable
    private static String getCleanTag(String uncleanTag) {
        for (var tag : Config.TAGS) {
            if (uncleanTag.contains(":" + tag))
                return tag;
        }
        return null;
    }

    public static List<CustomMusic> getMenuSongs() {
        if (!isInitialized)
            return new ArrayList<>();
        return Config.menuSongs.stream().map(musicByName::get).collect(Collectors.toList());
    }

    public static List<CustomMusic> getNightSongs() {
        return Config.nightSongs.stream().map(musicByName::get).collect(Collectors.toList());
    }

    public static List<CustomMusic> getRainSongs() {
        return Config.rainSongs.stream().map(musicByName::get).collect(Collectors.toList());
    }

    public static List<CustomMusic> getGenericSongs() {
        return Config.genericSongs.stream().map(musicByName::get).collect(Collectors.toList());
    }

    public static List<CustomMusic> getSongsFromTagStream(Stream<TagKey<Biome>> tags) {
        final List<CustomMusic> songs = new ArrayList<>();

        tags.forEach((tagKey) -> {
            String cleanTag = getCleanTag(tagKey.toString());

            if (cleanTag != null) {
                if (Config.songsPerTag.containsKey(cleanTag))
                    songs.addAll(Config.songsPerTag.get(cleanTag).stream().map(musicByName::get).toList());
                else if (Config.songsPerTagLowPriority.containsKey(cleanTag))
                    songs.addAll(Config.songsPerTagLowPriority.get(cleanTag).stream().map(musicByName::get).toList());
            }
        });

        return songs;
    }
}
