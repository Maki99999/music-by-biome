package io.github.maki99999.biomebeats.conditionmusic;

import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.condition.*;
import io.github.maki99999.biomebeats.config.ConfigChangeListener;
import io.github.maki99999.biomebeats.config.MainConfig;
import io.github.maki99999.biomebeats.mixin.MixinWeighedSoundEvents;
import io.github.maki99999.biomebeats.music.MusicTrack;
import io.github.maki99999.biomebeats.music.ResourceLocationMusicTrack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.io.IOException;
import java.util.*;

public class ConditionMusicManager implements ActiveConditionsListener, ConfigChangeListener {
    private Map<Condition, Collection<MusicTrack>> musicTracksByCondition = Map.of();
    private final RandomSource rdm = RandomSource.create();

    public void init() {
        Constants.CONFIG_IO.addListenerConditionMusicManager(this);
        Constants.CONDITION_MANAGER.addListener(this);
    }

    @Override
    public void onActiveConditionsChanged(Collection<? extends Condition> activeConditions) {
        int highestPriority = activeConditions.stream()
                .filter(c -> musicTracksByCondition.containsKey(c))
                .mapToInt(Condition::getPriority)
                .max()
                .orElse(Integer.MIN_VALUE);

        Constants.MUSIC_MANAGER.setCurrentMusicTracks(
                musicTracksByCondition
                        .entrySet()
                        .stream()
                        .filter(e -> activeConditions.contains(e.getKey()) && e.getKey().getPriority() == highestPriority)
                        .flatMap(e -> e.getValue().stream())
                        .toList()
        );
    }

    @Override
    public void beforeConfigChange(MainConfig config) {
        Map<String, Collection<String>> musicTrackIdsByConditionId = config.getMusicTrackIdsByConditionId();
        for (Map.Entry<? extends Condition, Collection<MusicTrack>> entry : musicTracksByCondition.entrySet()) {
            Collection<String> musicTrackIds = musicTrackIdsByConditionId
                    .computeIfAbsent(entry.getKey().getId(), k -> new HashSet<>());
            musicTrackIds.clear();
            entry.getValue().forEach(musicTrack -> musicTrackIds.add(musicTrack.getId()));
        }

        config.setMusicTrackIdsByConditionId(musicTrackIdsByConditionId);
    }

    @Override
    public void afterConfigChange(MainConfig config) {
        var conditions = Constants.CONDITION_MANAGER.getConditions();
        var musicTracks = Constants.MUSIC_MANAGER.getMusicTracks();
        musicTracksByCondition = new HashMap<>();

        List<String> invalidConditionIds = new ArrayList<>();
        List<String> invalidMusicTrackIds = new ArrayList<>();

        for (Map.Entry<String, Collection<String>> entry : config.getMusicTrackIdsByConditionId().entrySet()) {
            Condition condition = conditions
                    .stream()
                    .filter(c -> entry.getKey().equals(c.getId()))
                    .findAny()
                    .orElse(null);

            if (condition == null) {
                invalidConditionIds.add(entry.getKey());
            } else {
                Collection<MusicTrack> conditionMusicTracks = new HashSet<>();
                for (String musicTrackId : entry.getValue()) {
                    MusicTrack musicTrack = musicTracks
                            .stream()
                            .filter(m -> musicTrackId.equals(m.getId()))
                            .findAny()
                            .orElse(null);

                    if (musicTrack == null) {
                        invalidMusicTrackIds.add(musicTrackId);
                    } else {
                        conditionMusicTracks.add(musicTrack);
                    }
                }

                if (!conditionMusicTracks.isEmpty()) {
                    musicTracksByCondition.put(condition, conditionMusicTracks);
                }
            }
        }

        if (!invalidConditionIds.isEmpty()) {
            Constants.LOG.info("Conditions have been changed. Unknown condition IDs: {}.", String.join(", ",
                    invalidConditionIds));
        }

        if (!invalidMusicTrackIds.isEmpty()) {
            Constants.LOG.info("Music Tracks have been changed. Unknown music track ids: {}.", String.join(", ",
                    invalidMusicTrackIds));
        }

        if (config.isNewConfig()) {
            if (!musicTracksByCondition.isEmpty()) {
                Constants.LOG.warn("New config, but 'musicTracksByCondition' is not empty?");
            }

            addDefaultConfig();
        }
    }

    private void addDefaultConfig() {
        Collection<ResourceLocationMusicTrack> musicTracks =
                Constants.MUSIC_MANAGER.getMusicTracks()
                        .stream()
                        .filter(m -> m instanceof ResourceLocationMusicTrack)
                        .map(m -> (ResourceLocationMusicTrack) m).toList();

        Condition winScreenCondition = Constants.CONDITION_MANAGER.findCondition(
                c -> c instanceof ScreenCondition screenCondition && Objects.equals(screenCondition.getScreen(),
                        WinScreen.class));
        addMusicToCondition(musicTracks, Musics.CREDITS, winScreenCondition);

        Condition menuCondition = Constants.CONDITION_MANAGER.findCondition(
                c -> c instanceof ScreenCondition screenCondition && screenCondition.getScreen() == null);
        addMusicToCondition(musicTracks, Musics.MENU, menuCondition);

        Condition endBossCondition =
                Constants.CONDITION_MANAGER.findCondition(c -> c instanceof CombinedCondition combinedCondition
                        && combinedCondition.getName().equals("End Boss"));
        addMusicToCondition(musicTracks, Musics.END_BOSS, endBossCondition);

        Condition isEndCondition = Constants.CONDITION_MANAGER.findCondition(
                c -> c instanceof TagCondition tagCondition && tagCondition.getName().equals("Is End"));
        if (isEndCondition != null)
            addMusicToCondition(musicTracks, Musics.END, isEndCondition);

        Condition underwaterCondition =
                Constants.CONDITION_MANAGER.findCondition(c -> c instanceof CombinedCondition combinedCondition
                        && combinedCondition.getName().equals("Is Under Water in Underwater Biome"));
        addMusicToCondition(musicTracks, Musics.UNDER_WATER, underwaterCondition);

        Condition creativeModeCondition = Constants.CONDITION_MANAGER.findCondition(
                c -> c instanceof InGameModeCondition inGameModeCondition
                        && inGameModeCondition.getName().contains("Creative"));
        addMusicToCondition(musicTracks, Musics.CREATIVE, creativeModeCondition);

        Condition noOtherMusicCondition =
                Constants.CONDITION_MANAGER.findCondition(c -> c instanceof NoOtherMusicCondition);
        addMusicToCondition(musicTracks, Musics.GAME, noOtherMusicCondition);

        Collection<? extends Condition> biomeConditions = Constants.CONDITION_MANAGER.getBiomeConditions();

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;

        try (Level level = player.level()) {
            Registry<Biome> biomeRegistry = level.registryAccess().registryOrThrow(Registries.BIOME);
            for (Condition condition : biomeConditions) {
                ResourceLocation biomeRL = ((BiomeCondition) condition).getBiomeRL();
                Biome biome = biomeRegistry.get(biomeRL);
                if (biome == null) continue;

                Optional<Music> biomeBgm = biome.getBackgroundMusic();
                if (biomeBgm.isEmpty()) continue;

                addMusicToCondition(musicTracks, biomeBgm.get(), condition);
            }
        } catch (IOException e) {
            Constants.LOG.error(e.getMessage(), e);
        }
    }

    private void addMusicToCondition(Collection<ResourceLocationMusicTrack> musicTracks,
                                     Music music,
                                     Condition condition) {
        if (music == null) return;

        MixinWeighedSoundEvents musicSoundEvents = (MixinWeighedSoundEvents) Minecraft.getInstance().getSoundManager()
                .getSoundEvent(music.getEvent().value().getLocation());
        if (musicSoundEvents == null) return;

        List<ResourceLocation> musicRLs = musicSoundEvents.list().stream()
                .map(weightedSound -> weightedSound.getSound(rdm))
                .map(Sound::getLocation)
                .distinct()
                .toList();

        musicTracks.stream()
                .filter(m -> musicRLs.stream().anyMatch(rl -> rl.equals(m.getResourceLocation())))
                .forEach(musicTrack -> musicTracksByCondition
                        .computeIfAbsent(condition, k -> new HashSet<>())
                        .add(musicTrack));
    }

    public Map<Condition, Collection<MusicTrack>> getMusicTracksByCondition() {
        return musicTracksByCondition;
    }
}
