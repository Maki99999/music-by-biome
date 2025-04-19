package io.github.maki99999.biomebeats.conditionmusic;

import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.condition.*;
import io.github.maki99999.biomebeats.config.ConfigChangeListener;
import io.github.maki99999.biomebeats.config.MainConfig;
import io.github.maki99999.biomebeats.mixin.MixinWeighedSoundEvents;
import io.github.maki99999.biomebeats.music.MusicTrack;
import io.github.maki99999.biomebeats.music.ResourceLocationMusicTrack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ConditionMusicManager implements ActiveConditionsListener, ConfigChangeListener {
    private Map<String, Collection<MusicTrack>> musicTracksByConditionId = Map.of();
    private final RandomSource rdm = RandomSource.create();

    public void init() {
        Constants.CONFIG_IO.addListenerConditionMusicManager(this);
        Constants.CONDITION_MANAGER.addListener(this);
    }

    @Override
    public void onActiveConditionsChanged(Collection<? extends Condition> activeConditions) {
        Constants.MUSIC_MANAGER.setCurrentMusicTracks(getTracksFromActiveConditions(activeConditions));
    }

    public Collection<MusicTrack> getTracksFromActiveConditions(Collection<? extends Condition> activeConditions) {
        int highestPriority = getHighestPriorityOfConditionsWithMusicTracks(activeConditions);
        List<String> relevantConditionIds = activeConditions.stream()
                .filter(x -> x.getPriority() == highestPriority)
                .map(Condition::getId).toList();

        return musicTracksByConditionId
                .entrySet()
                .stream()
                .filter(e -> relevantConditionIds.contains(e.getKey()))
                .flatMap(e -> e.getValue().stream())
                .toList();
    }

    public int getHighestPriorityOfConditionsWithMusicTracks(Collection<? extends Condition> conditions) {
        return conditions.stream()
                .filter(c -> musicTracksByConditionId.containsKey(c.getId()))
                .mapToInt(Condition::getPriority)
                .max()
                .orElse(Integer.MIN_VALUE);
    }

    @Override
    public void beforeConfigChange(MainConfig config) {
        Map<String, Collection<String>> musicTrackIdsByConditionId = config.getMusicTrackIdsByConditionId();
        for (Map.Entry<String, Collection<MusicTrack>> entry : musicTracksByConditionId.entrySet()) {
            Collection<String> musicTrackIds = musicTrackIdsByConditionId
                    .computeIfAbsent(entry.getKey(), k -> new HashSet<>());
            musicTrackIds.clear();
            entry.getValue().forEach(musicTrack -> musicTrackIds.add(musicTrack.getId()));
        }

        config.setMusicTrackIdsByConditionId(musicTrackIdsByConditionId);
    }

    @Override
    public void afterConfigChange(MainConfig config) {
        Collection<? extends MusicTrack> musicTracks = Constants.MUSIC_MANAGER.getMusicTracks();
        musicTracksByConditionId = new HashMap<>();

        List<String> invalidMusicTrackIds = new ArrayList<>();

        for (Map.Entry<String, Collection<String>> entry : config.getMusicTrackIdsByConditionId().entrySet()) {
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
                musicTracksByConditionId.put(entry.getKey(), conditionMusicTracks);
            }
        }

        if (!invalidMusicTrackIds.isEmpty()) {
            Constants.LOG.info("Music Tracks have been changed. Unknown music track ids: {}.", String.join(", ",
                    invalidMusicTrackIds));
        }

        if (config.isNewConfig()) {
            if (!musicTracksByConditionId.isEmpty()) {
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

        addMusicToCondition(musicTracks, Musics.CREDITS, ScreenCondition.WIN_SCREEN);
        addMusicToCondition(musicTracks, Musics.MENU, ScreenCondition.MAIN_MENU);
        addMusicToCondition(musicTracks, Musics.CREATIVE, InGameModeCondition.getId(GameType.CREATIVE));
        addMusicToCondition(musicTracks, Musics.GAME, NoOtherMusicCondition.ID);

        Condition endBossCondition = Constants.CONDITION_MANAGER.findCondition(
                c -> c instanceof CombinedCondition combCond
                        && combCond.getName().equals(ConditionManager.END_BOSS_CONDITION_NAME));
        if (endBossCondition != null) {
            addMusicToCondition(musicTracks, Musics.END_BOSS, endBossCondition.getId());
        }

        Condition isEndCondition = Constants.CONDITION_MANAGER.findCondition(
                c -> c instanceof TagCondition tagCondition && tagCondition.getName().equals("Is End"));
        if (isEndCondition != null) {
            addMusicToCondition(musicTracks, Musics.END, isEndCondition.getId());
        }

        Condition underwaterCondition1 = Constants.CONDITION_MANAGER.findCondition(
                c -> c instanceof TagCondition tagCondition && tagCondition.getName().equals("Plays Underwater Music"));
        if (underwaterCondition1 != null) {
            addMusicToCondition(musicTracks, Musics.UNDER_WATER, underwaterCondition1.getId());
        }

        Condition underwaterCondition2 =
                Constants.CONDITION_MANAGER.findCondition(c -> c instanceof IsUnderWaterCondition);
        if (underwaterCondition2 != null) {
            addMusicToCondition(musicTracks, Musics.UNDER_WATER, underwaterCondition2.getId());
        }

        Collection<? extends Condition> biomeConditions = Constants.CONDITION_MANAGER.getBiomeConditions();

        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        try (Level level = player.level()) {
            Registry<Biome> biomeRegistry = level.registryAccess().lookupOrThrow(Registries.BIOME);
            for (Condition condition : biomeConditions) {
                ResourceLocation biomeRl = ((BiomeCondition) condition).getBiomeRl();
                Biome biome = biomeRegistry.getValue(biomeRl);
                if (biome == null) continue;

                Optional<SimpleWeightedRandomList<Music>> biomeBgms = biome.getBackgroundMusic();
                if (biomeBgms.isEmpty()) continue;

                for (var biomeBgm : biomeBgms.get().unwrap()) {
                    addMusicToCondition(musicTracks, biomeBgm.data(), condition.getId());
                }
            }
        } catch (IOException e) {
            Constants.LOG.error(e.getMessage(), e);
        }
    }

    private void addMusicToCondition(Collection<ResourceLocationMusicTrack> musicTracks,
                                     Music music,
                                     String conditionId) {
        if (music == null) {
            return;
        }

        MixinWeighedSoundEvents musicSoundEvents = (MixinWeighedSoundEvents) Minecraft.getInstance().getSoundManager()
                .getSoundEvent(music.getEvent().value().location());
        if (musicSoundEvents == null) {
            return;
        }

        List<ResourceLocation> musicRLs = musicSoundEvents.list().stream()
                .map(weightedSound -> weightedSound.getSound(rdm))
                .map(Sound::getLocation)
                .distinct()
                .toList();

        musicTracks.stream()
                .filter(m -> musicRLs.stream().anyMatch(rl -> rl.equals(m.getResourceLocation())))
                .forEach(musicTrack -> musicTracksByConditionId
                        .computeIfAbsent(conditionId, k -> new HashSet<>())
                        .add(musicTrack));
    }

    public void addTrackToCondition(String conditionId, MusicTrack track) {
        musicTracksByConditionId.computeIfAbsent(conditionId, k -> new HashSet<>()).add(track);
    }

    public void removeTrackToCondition(String conditionId, MusicTrack track) {
        if (musicTracksByConditionId.containsKey(conditionId)) {
            musicTracksByConditionId.get(conditionId).remove(track);
            if (musicTracksByConditionId.get(conditionId).isEmpty()) {
                musicTracksByConditionId.remove(conditionId);
            }
        }
    }

    public Map<Condition, Collection<MusicTrack>> getMusicTracksByCondition() {
        return musicTracksByConditionId.entrySet()
                .stream()
                .map(entry -> {
                    Condition condition = ConditionManager.getCondition(entry.getKey());
                    return condition == null ? null : Map.entry(condition, entry.getValue());
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Collection<MusicTrack> getMusicTracksForCondition(String id) {
        return musicTracksByConditionId.getOrDefault(id, Collections.emptyList());
    }
}
