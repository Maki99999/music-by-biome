package io.github.maki99999.musicbybiome;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class MusicProvider {
    public static final Map<String, Music> musicByName = new HashMap<>();

    public static final DeferredRegister<SoundEvent> MUSIC_SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MusicByBiome.MOD_ID);

    //public static RegistryObject<SoundEvent> test = registerSoundEvent("test");
    public static RegistryObject<SoundEvent> m_030CraggyCoast = registerSoundEvent("030craggycoast");
    public static RegistryObject<SoundEvent> m_032MtHorn = registerSoundEvent("032mthorn");
    public static RegistryObject<SoundEvent> m_033FoggyForest = registerSoundEvent("033foggyforest");
    public static RegistryObject<SoundEvent> m_034SteamCave = registerSoundEvent("034steamcave");
    public static RegistryObject<SoundEvent> m_035UpperSteamCave = registerSoundEvent("035uppersteamcave");
    public static RegistryObject<SoundEvent> m_036AmpPlains = registerSoundEvent("036ampplains");
    public static RegistryObject<SoundEvent> m_037FarAmpPlains = registerSoundEvent("037farampplains");
    public static RegistryObject<SoundEvent> m_040NorthernDesert = registerSoundEvent("040northerndesert");
    public static RegistryObject<SoundEvent> m_041QuicksandCave = registerSoundEvent("041quicksandcave");
    public static RegistryObject<SoundEvent> m_042QuicksandPit = registerSoundEvent("042quicksandpit");
    public static RegistryObject<SoundEvent> m_043CrystalCave = registerSoundEvent("043crystalcave");
    public static RegistryObject<SoundEvent> m_045EndOfTheDay = registerSoundEvent("045endoftheday");
    public static RegistryObject<SoundEvent> m_046InTheFuture = registerSoundEvent("046inthefuture");
    public static RegistryObject<SoundEvent> m_047PlanetParalysis = registerSoundEvent("047planetparalysis");
    public static RegistryObject<SoundEvent> m_048ChasmCave = registerSoundEvent("048chasmcave");
    public static RegistryObject<SoundEvent> m_052DuskForest = registerSoundEvent("052duskforest");
    public static RegistryObject<SoundEvent> m_055TreeshroudForest = registerSoundEvent("055treeshroudforest");
    public static RegistryObject<SoundEvent> m_058HiddenLand = registerSoundEvent("058hiddenland");
    public static RegistryObject<SoundEvent> m_062ThroughTheSeaOfTime = registerSoundEvent("062throughtheseaoftime");
    public static RegistryObject<SoundEvent> m_064TemporalTower = registerSoundEvent("064temporaltower");
    public static RegistryObject<SoundEvent> m_079DoYourBest = registerSoundEvent("079doyourbest");
    public static RegistryObject<SoundEvent> m_080ShayminVillage = registerSoundEvent("080shayminvillage");
    public static RegistryObject<SoundEvent> m_081SkyPeakForest = registerSoundEvent("081skypeakforest");
    public static RegistryObject<SoundEvent> m_082SkyPeakCave = registerSoundEvent("082skypeakcave");
    public static RegistryObject<SoundEvent> m_083SkyPeakPrairie = registerSoundEvent("083skypeakprairie");
    public static RegistryObject<SoundEvent> m_085SkyPeakSnowfield = registerSoundEvent("085skypeaksnowfield");
    public static RegistryObject<SoundEvent> m_086SkyPeakFinalPass = registerSoundEvent("086skypeakfinalpass");
    public static RegistryObject<SoundEvent> m_088SurroundedSea = registerSoundEvent("088surroundedsea");
    public static RegistryObject<SoundEvent> m_089MiracleSea = registerSoundEvent("089miraclesea");
    public static RegistryObject<SoundEvent> m_090AegisCave = registerSoundEvent("090aegiscave");
    public static RegistryObject<SoundEvent> m_092ConcealedRuins = registerSoundEvent("092concealedruins");
    public static RegistryObject<SoundEvent> m_096DarkCrater = registerSoundEvent("096darkcrater");
    public static RegistryObject<SoundEvent> m_097DeepDarkCrater = registerSoundEvent("097deepdarkcrater");
    public static RegistryObject<SoundEvent> m_100RandomDungeonTheme3 = registerSoundEvent("100randomdungeontheme3");
    public static RegistryObject<SoundEvent> m_106StarCave = registerSoundEvent("106starcave");
    public static RegistryObject<SoundEvent> m_111FortuneRavine = registerSoundEvent("111fortuneravine");
    public static RegistryObject<SoundEvent> m_120SouthernJungle = registerSoundEvent("120southernjungle");
    public static RegistryObject<SoundEvent> m_127BarrenValley = registerSoundEvent("127barrenvalley");
    public static RegistryObject<SoundEvent> m_128DarkWasteland = registerSoundEvent("128darkwasteland");
    public static RegistryObject<SoundEvent> m_135VastIceMountainPeak = registerSoundEvent("135vasticemountainpeak");
    public static RegistryObject<SoundEvent> m_136InTheMorningSun = registerSoundEvent("136inthemorningsun");
    public static RegistryObject<SoundEvent> m_166GoodnightAlternate = registerSoundEvent("166goodnightalternate");
    public static RegistryObject<SoundEvent> m_AmitySquare = registerSoundEvent("amitysquare");
    public static RegistryObject<SoundEvent> m_BeginningDimension = registerSoundEvent("beginningdimension");
    public static RegistryObject<SoundEvent> m_Bicycle = registerSoundEvent("bicycle");
    public static RegistryObject<SoundEvent> m_CanalaveCity = registerSoundEvent("canalavecity");
    public static RegistryObject<SoundEvent> m_DawnTheme = registerSoundEvent("dawntheme");
    public static RegistryObject<SoundEvent> m_DeepWithinTeamGalacticHQ = registerSoundEvent("deepwithinteamgalactichq");
    public static RegistryObject<SoundEvent> m_DistortionWorld = registerSoundEvent("distortionworld");
    public static RegistryObject<SoundEvent> m_Ending = registerSoundEvent("ending");
    public static RegistryObject<SoundEvent> m_EternaCity = registerSoundEvent("eternacity");
    public static RegistryObject<SoundEvent> m_EternaForest = registerSoundEvent("eternaforest");
    public static RegistryObject<SoundEvent> m_FightArea = registerSoundEvent("fightarea");
    public static RegistryObject<SoundEvent> m_FloaromaTown = registerSoundEvent("floaromatown");
    public static RegistryObject<SoundEvent> m_GameCorner = registerSoundEvent("gamecorner");
    public static RegistryObject<SoundEvent> m_GlobalTradeStation = registerSoundEvent("globaltradestation");
    public static RegistryObject<SoundEvent> m_GreatMarsh = registerSoundEvent("greatmarsh");
    public static RegistryObject<SoundEvent> m_HearthomeCity = registerSoundEvent("hearthomecity");
    public static RegistryObject<SoundEvent> m_HearthomeCityNight = registerSoundEvent("hearthomecitynight");
    public static RegistryObject<SoundEvent> m_JubilifeCity = registerSoundEvent("jubilifecity");
    public static RegistryObject<SoundEvent> m_JubilifeCityNight = registerSoundEvent("jubilifecitynight");
    public static RegistryObject<SoundEvent> m_Lake = registerSoundEvent("lake");
    public static RegistryObject<SoundEvent> m_LucasTheme = registerSoundEvent("lucastheme");
    public static RegistryObject<SoundEvent> m_MenuMusic = registerSoundEvent("menumusic");
    public static RegistryObject<SoundEvent> m_MtCoronet = registerSoundEvent("mtcoronet");
    public static RegistryObject<SoundEvent> m_OldChateau = registerSoundEvent("oldchateau");
    public static RegistryObject<SoundEvent> m_OreburghCity = registerSoundEvent("oreburghcity");
    public static RegistryObject<SoundEvent> m_OreburghMine = registerSoundEvent("oreburghmine");
    public static RegistryObject<SoundEvent> m_Poffin = registerSoundEvent("poffin");
    public static RegistryObject<SoundEvent> m_PokemonCenter = registerSoundEvent("pokemoncenter");
    public static RegistryObject<SoundEvent> m_PokemonLeague = registerSoundEvent("pokemonleague");
    public static RegistryObject<SoundEvent> m_Route201 = registerSoundEvent("route201");
    public static RegistryObject<SoundEvent> m_Route203 = registerSoundEvent("route203");
    public static RegistryObject<SoundEvent> m_Route205 = registerSoundEvent("route205");
    public static RegistryObject<SoundEvent> m_Route206 = registerSoundEvent("route206");
    public static RegistryObject<SoundEvent> m_Route209 = registerSoundEvent("route209");
    public static RegistryObject<SoundEvent> m_Route210 = registerSoundEvent("route210");
    public static RegistryObject<SoundEvent> m_Route216 = registerSoundEvent("route216");
    public static RegistryObject<SoundEvent> m_Route225 = registerSoundEvent("route225");
    public static RegistryObject<SoundEvent> m_SandgemTown = registerSoundEvent("sandgemtown");
    public static RegistryObject<SoundEvent> m_SnowpointCity = registerSoundEvent("snowpointcity");
    public static RegistryObject<SoundEvent> m_SolaceonTown = registerSoundEvent("solaceontown");
    public static RegistryObject<SoundEvent> m_SpearPillar = registerSoundEvent("spearpillar");
    public static RegistryObject<SoundEvent> m_StarkMountain = registerSoundEvent("starkmountain");
    public static RegistryObject<SoundEvent> m_SunyshoreCity = registerSoundEvent("sunyshorecity");
    public static RegistryObject<SoundEvent> m_Surf = registerSoundEvent("surf");
    public static RegistryObject<SoundEvent> m_TeamGalacticBuilding = registerSoundEvent("teamgalacticbuilding");
    public static RegistryObject<SoundEvent> m_TwinleafTown = registerSoundEvent("twinleaftown");
    public static RegistryObject<SoundEvent> m_UndergroundPassage = registerSoundEvent("undergroundpassage");
    public static RegistryObject<SoundEvent> m_ValorLakefront = registerSoundEvent("valorlakefront");
    public static RegistryObject<SoundEvent> m_VeilstoneCity = registerSoundEvent("veilstonecity");
    public static RegistryObject<SoundEvent> m_Villa = registerSoundEvent("villa");
    public static RegistryObject<SoundEvent> m_003Welcome = registerSoundEvent("003welcome");
    public static RegistryObject<SoundEvent> m_010Goodnight = registerSoundEvent("010goodnight");
    public static RegistryObject<SoundEvent> m_014TreasureTown = registerSoundEvent("014treasuretown");
    public static RegistryObject<SoundEvent> m_015Heartwarming = registerSoundEvent("015heartwarming");
    public static RegistryObject<SoundEvent> m_020TimeGearRemix = registerSoundEvent("020timegearremix");
    public static RegistryObject<SoundEvent> m_024WaterfallCave = registerSoundEvent("024waterfallcave");
    public static RegistryObject<SoundEvent> m_026TeamSkull = registerSoundEvent("026teamskull");
    public static RegistryObject<SoundEvent> m_027SpindaCafe = registerSoundEvent("027spindacafe");
    public static RegistryObject<SoundEvent> m_029AppleWoods = registerSoundEvent("029applewoods");

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

        for (var event : allSoundEvents.entrySet()) {
            Holder<SoundEvent> soundEventHolder = new Holder.Direct<>(event.getValue().get());
            musicByName.put(event.getKey().toLowerCase(), Musics.createGameMusic(soundEventHolder));
        }
    }
}
