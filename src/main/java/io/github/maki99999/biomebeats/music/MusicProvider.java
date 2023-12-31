package io.github.maki99999.biomebeats.music;

import io.github.maki99999.biomebeats.Config;
import io.github.maki99999.biomebeats.BiomeBeats;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.Pair;

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
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BiomeBeats.MOD_ID);

    //<editor-fold defaultstate="collapsed" desc="all sound event registrations">
    private static final RegistryObject<SoundEvent> m_custom0 = registerSoundEvent("custom0");
    private static final RegistryObject<SoundEvent> m_custom1 = registerSoundEvent("custom1");
    private static final RegistryObject<SoundEvent> m_custom2 = registerSoundEvent("custom2");
    private static final RegistryObject<SoundEvent> m_custom3 = registerSoundEvent("custom3");
    private static final RegistryObject<SoundEvent> m_custom4 = registerSoundEvent("custom4");
    private static final RegistryObject<SoundEvent> m_custom5 = registerSoundEvent("custom5");
    private static final RegistryObject<SoundEvent> m_custom6 = registerSoundEvent("custom6");
    private static final RegistryObject<SoundEvent> m_custom7 = registerSoundEvent("custom7");
    private static final RegistryObject<SoundEvent> m_custom8 = registerSoundEvent("custom8");
    private static final RegistryObject<SoundEvent> m_custom9 = registerSoundEvent("custom9");
    private static final RegistryObject<SoundEvent> m_custom10 = registerSoundEvent("custom10");
    private static final RegistryObject<SoundEvent> m_custom11 = registerSoundEvent("custom11");
    private static final RegistryObject<SoundEvent> m_custom12 = registerSoundEvent("custom12");
    private static final RegistryObject<SoundEvent> m_custom13 = registerSoundEvent("custom13");
    private static final RegistryObject<SoundEvent> m_custom14 = registerSoundEvent("custom14");
    private static final RegistryObject<SoundEvent> m_custom15 = registerSoundEvent("custom15");
    private static final RegistryObject<SoundEvent> m_custom16 = registerSoundEvent("custom16");
    private static final RegistryObject<SoundEvent> m_custom17 = registerSoundEvent("custom17");
    private static final RegistryObject<SoundEvent> m_custom18 = registerSoundEvent("custom18");
    private static final RegistryObject<SoundEvent> m_custom19 = registerSoundEvent("custom19");
    private static final RegistryObject<SoundEvent> m_custom20 = registerSoundEvent("custom20");
    private static final RegistryObject<SoundEvent> m_custom21 = registerSoundEvent("custom21");
    private static final RegistryObject<SoundEvent> m_custom22 = registerSoundEvent("custom22");
    private static final RegistryObject<SoundEvent> m_custom23 = registerSoundEvent("custom23");
    private static final RegistryObject<SoundEvent> m_custom24 = registerSoundEvent("custom24");
    private static final RegistryObject<SoundEvent> m_custom25 = registerSoundEvent("custom25");
    private static final RegistryObject<SoundEvent> m_custom26 = registerSoundEvent("custom26");
    private static final RegistryObject<SoundEvent> m_custom27 = registerSoundEvent("custom27");
    private static final RegistryObject<SoundEvent> m_custom28 = registerSoundEvent("custom28");
    private static final RegistryObject<SoundEvent> m_custom29 = registerSoundEvent("custom29");
    private static final RegistryObject<SoundEvent> m_custom30 = registerSoundEvent("custom30");
    private static final RegistryObject<SoundEvent> m_custom31 = registerSoundEvent("custom31");
    private static final RegistryObject<SoundEvent> m_custom32 = registerSoundEvent("custom32");
    private static final RegistryObject<SoundEvent> m_custom33 = registerSoundEvent("custom33");
    private static final RegistryObject<SoundEvent> m_custom34 = registerSoundEvent("custom34");
    private static final RegistryObject<SoundEvent> m_custom35 = registerSoundEvent("custom35");
    private static final RegistryObject<SoundEvent> m_custom36 = registerSoundEvent("custom36");
    private static final RegistryObject<SoundEvent> m_custom37 = registerSoundEvent("custom37");
    private static final RegistryObject<SoundEvent> m_custom38 = registerSoundEvent("custom38");
    private static final RegistryObject<SoundEvent> m_custom39 = registerSoundEvent("custom39");
    private static final RegistryObject<SoundEvent> m_custom40 = registerSoundEvent("custom40");
    private static final RegistryObject<SoundEvent> m_custom41 = registerSoundEvent("custom41");
    private static final RegistryObject<SoundEvent> m_custom42 = registerSoundEvent("custom42");
    private static final RegistryObject<SoundEvent> m_custom43 = registerSoundEvent("custom43");
    private static final RegistryObject<SoundEvent> m_custom44 = registerSoundEvent("custom44");
    private static final RegistryObject<SoundEvent> m_custom45 = registerSoundEvent("custom45");
    private static final RegistryObject<SoundEvent> m_custom46 = registerSoundEvent("custom46");
    private static final RegistryObject<SoundEvent> m_custom47 = registerSoundEvent("custom47");
    private static final RegistryObject<SoundEvent> m_custom48 = registerSoundEvent("custom48");
    private static final RegistryObject<SoundEvent> m_custom49 = registerSoundEvent("custom49");
    private static final RegistryObject<SoundEvent> m_custom50 = registerSoundEvent("custom50");
    private static final RegistryObject<SoundEvent> m_custom51 = registerSoundEvent("custom51");
    private static final RegistryObject<SoundEvent> m_custom52 = registerSoundEvent("custom52");
    private static final RegistryObject<SoundEvent> m_custom53 = registerSoundEvent("custom53");
    private static final RegistryObject<SoundEvent> m_custom54 = registerSoundEvent("custom54");
    private static final RegistryObject<SoundEvent> m_custom55 = registerSoundEvent("custom55");
    private static final RegistryObject<SoundEvent> m_custom56 = registerSoundEvent("custom56");
    private static final RegistryObject<SoundEvent> m_custom57 = registerSoundEvent("custom57");
    private static final RegistryObject<SoundEvent> m_custom58 = registerSoundEvent("custom58");
    private static final RegistryObject<SoundEvent> m_custom59 = registerSoundEvent("custom59");
    private static final RegistryObject<SoundEvent> m_custom60 = registerSoundEvent("custom60");
    private static final RegistryObject<SoundEvent> m_custom61 = registerSoundEvent("custom61");
    private static final RegistryObject<SoundEvent> m_custom62 = registerSoundEvent("custom62");
    private static final RegistryObject<SoundEvent> m_custom63 = registerSoundEvent("custom63");
    private static final RegistryObject<SoundEvent> m_custom64 = registerSoundEvent("custom64");
    private static final RegistryObject<SoundEvent> m_custom65 = registerSoundEvent("custom65");
    private static final RegistryObject<SoundEvent> m_custom66 = registerSoundEvent("custom66");
    private static final RegistryObject<SoundEvent> m_custom67 = registerSoundEvent("custom67");
    private static final RegistryObject<SoundEvent> m_custom68 = registerSoundEvent("custom68");
    private static final RegistryObject<SoundEvent> m_custom69 = registerSoundEvent("custom69");
    private static final RegistryObject<SoundEvent> m_custom70 = registerSoundEvent("custom70");
    private static final RegistryObject<SoundEvent> m_custom71 = registerSoundEvent("custom71");
    private static final RegistryObject<SoundEvent> m_custom72 = registerSoundEvent("custom72");
    private static final RegistryObject<SoundEvent> m_custom73 = registerSoundEvent("custom73");
    private static final RegistryObject<SoundEvent> m_custom74 = registerSoundEvent("custom74");
    private static final RegistryObject<SoundEvent> m_custom75 = registerSoundEvent("custom75");
    private static final RegistryObject<SoundEvent> m_custom76 = registerSoundEvent("custom76");
    private static final RegistryObject<SoundEvent> m_custom77 = registerSoundEvent("custom77");
    private static final RegistryObject<SoundEvent> m_custom78 = registerSoundEvent("custom78");
    private static final RegistryObject<SoundEvent> m_custom79 = registerSoundEvent("custom79");
    private static final RegistryObject<SoundEvent> m_custom80 = registerSoundEvent("custom80");
    private static final RegistryObject<SoundEvent> m_custom81 = registerSoundEvent("custom81");
    private static final RegistryObject<SoundEvent> m_custom82 = registerSoundEvent("custom82");
    private static final RegistryObject<SoundEvent> m_custom83 = registerSoundEvent("custom83");
    private static final RegistryObject<SoundEvent> m_custom84 = registerSoundEvent("custom84");
    private static final RegistryObject<SoundEvent> m_custom85 = registerSoundEvent("custom85");
    private static final RegistryObject<SoundEvent> m_custom86 = registerSoundEvent("custom86");
    private static final RegistryObject<SoundEvent> m_custom87 = registerSoundEvent("custom87");
    private static final RegistryObject<SoundEvent> m_custom88 = registerSoundEvent("custom88");
    private static final RegistryObject<SoundEvent> m_custom89 = registerSoundEvent("custom89");
    private static final RegistryObject<SoundEvent> m_custom90 = registerSoundEvent("custom90");
    private static final RegistryObject<SoundEvent> m_custom91 = registerSoundEvent("custom91");
    private static final RegistryObject<SoundEvent> m_custom92 = registerSoundEvent("custom92");
    private static final RegistryObject<SoundEvent> m_custom93 = registerSoundEvent("custom93");
    private static final RegistryObject<SoundEvent> m_custom94 = registerSoundEvent("custom94");
    private static final RegistryObject<SoundEvent> m_custom95 = registerSoundEvent("custom95");
    private static final RegistryObject<SoundEvent> m_custom96 = registerSoundEvent("custom96");
    private static final RegistryObject<SoundEvent> m_custom97 = registerSoundEvent("custom97");
    private static final RegistryObject<SoundEvent> m_custom98 = registerSoundEvent("custom98");
    private static final RegistryObject<SoundEvent> m_custom99 = registerSoundEvent("custom99");

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
        ResourceLocation location = new ResourceLocation(BiomeBeats.MOD_ID, name);
        return MUSIC_SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(location));
    }

    public static void register(IEventBus eventBus) {
        MUSIC_SOUND_EVENTS.register(eventBus);
    }

    public static void init() {
        //<editor-fold defaultstate="collapsed" desc="all sound event mappings">
        //noinspection RedundantTypeArguments (explicit type arguments speedup compilation and analysis time)
        Map<String, RegistryObject<SoundEvent>> allSoundEvents = Map.<String, RegistryObject<SoundEvent>>ofEntries(
                Map.entry("custom0", m_custom0),
                Map.entry("custom1", m_custom1),
                Map.entry("custom2", m_custom2),
                Map.entry("custom3", m_custom3),
                Map.entry("custom4", m_custom4),
                Map.entry("custom5", m_custom5),
                Map.entry("custom6", m_custom6),
                Map.entry("custom7", m_custom7),
                Map.entry("custom8", m_custom8),
                Map.entry("custom9", m_custom9),
                Map.entry("custom10", m_custom10),
                Map.entry("custom11", m_custom11),
                Map.entry("custom12", m_custom12),
                Map.entry("custom13", m_custom13),
                Map.entry("custom14", m_custom14),
                Map.entry("custom15", m_custom15),
                Map.entry("custom16", m_custom16),
                Map.entry("custom17", m_custom17),
                Map.entry("custom18", m_custom18),
                Map.entry("custom19", m_custom19),
                Map.entry("custom20", m_custom20),
                Map.entry("custom21", m_custom21),
                Map.entry("custom22", m_custom22),
                Map.entry("custom23", m_custom23),
                Map.entry("custom24", m_custom24),
                Map.entry("custom25", m_custom25),
                Map.entry("custom26", m_custom26),
                Map.entry("custom27", m_custom27),
                Map.entry("custom28", m_custom28),
                Map.entry("custom29", m_custom29),
                Map.entry("custom30", m_custom30),
                Map.entry("custom31", m_custom31),
                Map.entry("custom32", m_custom32),
                Map.entry("custom33", m_custom33),
                Map.entry("custom34", m_custom34),
                Map.entry("custom35", m_custom35),
                Map.entry("custom36", m_custom36),
                Map.entry("custom37", m_custom37),
                Map.entry("custom38", m_custom38),
                Map.entry("custom39", m_custom39),
                Map.entry("custom40", m_custom40),
                Map.entry("custom41", m_custom41),
                Map.entry("custom42", m_custom42),
                Map.entry("custom43", m_custom43),
                Map.entry("custom44", m_custom44),
                Map.entry("custom45", m_custom45),
                Map.entry("custom46", m_custom46),
                Map.entry("custom47", m_custom47),
                Map.entry("custom48", m_custom48),
                Map.entry("custom49", m_custom49),
                Map.entry("custom50", m_custom50),
                Map.entry("custom51", m_custom51),
                Map.entry("custom52", m_custom52),
                Map.entry("custom53", m_custom53),
                Map.entry("custom54", m_custom54),
                Map.entry("custom55", m_custom55),
                Map.entry("custom56", m_custom56),
                Map.entry("custom57", m_custom57),
                Map.entry("custom58", m_custom58),
                Map.entry("custom59", m_custom59),
                Map.entry("custom60", m_custom60),
                Map.entry("custom61", m_custom61),
                Map.entry("custom62", m_custom62),
                Map.entry("custom63", m_custom63),
                Map.entry("custom64", m_custom64),
                Map.entry("custom65", m_custom65),
                Map.entry("custom66", m_custom66),
                Map.entry("custom67", m_custom67),
                Map.entry("custom68", m_custom68),
                Map.entry("custom69", m_custom69),
                Map.entry("custom70", m_custom70),
                Map.entry("custom71", m_custom71),
                Map.entry("custom72", m_custom72),
                Map.entry("custom73", m_custom73),
                Map.entry("custom74", m_custom74),
                Map.entry("custom75", m_custom75),
                Map.entry("custom76", m_custom76),
                Map.entry("custom77", m_custom77),
                Map.entry("custom78", m_custom78),
                Map.entry("custom79", m_custom79),
                Map.entry("custom80", m_custom80),
                Map.entry("custom81", m_custom81),
                Map.entry("custom82", m_custom82),
                Map.entry("custom83", m_custom83),
                Map.entry("custom84", m_custom84),
                Map.entry("custom85", m_custom85),
                Map.entry("custom86", m_custom86),
                Map.entry("custom87", m_custom87),
                Map.entry("custom88", m_custom88),
                Map.entry("custom89", m_custom89),
                Map.entry("custom90", m_custom90),
                Map.entry("custom91", m_custom91),
                Map.entry("custom92", m_custom92),
                Map.entry("custom93", m_custom93),
                Map.entry("custom94", m_custom94),
                Map.entry("custom95", m_custom95),
                Map.entry("custom96", m_custom96),
                Map.entry("custom97", m_custom97),
                Map.entry("custom98", m_custom98),
                Map.entry("custom99", m_custom99),

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
//</editor-fold>

        for (var entry : allSoundEvents.entrySet()) {
            musicByName.put(entry.getKey().toLowerCase(), new CustomMusic(entry.getKey(), entry.getValue().get()));
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
        if (!isInitialized)
            return new ArrayList<>();
        return Config.genericSongs.stream().map(musicByName::get).collect(Collectors.toList());
    }

    public static Pair<List<CustomMusic>, List<CustomMusic>> getSongsFromTagStream(Stream<TagKey<Biome>> tags) {
        final List<CustomMusic> normalPrioritySongs = new ArrayList<>();
        final List<CustomMusic> lowPrioritySongs = new ArrayList<>();

        tags.forEach((tagKey) -> {
            String cleanTag = getCleanTag(tagKey.toString());

            if (cleanTag != null) {
                if (Config.songsPerTag.containsKey(cleanTag))
                    normalPrioritySongs.addAll(Config.songsPerTag.get(cleanTag).stream().map(musicByName::get).toList());
                else if (Config.songsPerTagLowPriority.containsKey(cleanTag))
                    lowPrioritySongs.addAll(Config.songsPerTagLowPriority.get(cleanTag).stream().map(musicByName::get).toList());
            }
        });

        return Pair.of(normalPrioritySongs, lowPrioritySongs);
    }
}
