package io.github.maki99999.biomebeats.conditionmusic;

import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.condition.ActiveConditionsListener;
import io.github.maki99999.biomebeats.condition.Condition;
import io.github.maki99999.biomebeats.config.ConfigChangeListener;
import io.github.maki99999.biomebeats.config.MainConfig;
import io.github.maki99999.biomebeats.music.MusicTrack;

import java.util.*;

public class ConditionMusicManager implements ActiveConditionsListener, ConfigChangeListener {
    private Map<Condition, Collection<MusicTrack>> musicTracksByCondition = Map.of();

    public void init() {
        Constants.CONFIG_IO.addListenerConditionMusicManager(this);
        Constants.CONDITION_MANAGER.addListener(this);
    }

    @Override
    public void onActiveConditionsChanged(Collection<? extends Condition> activeConditions) {
        int highestPriority = activeConditions.stream().mapToInt(Condition::getPriority).max().orElse(0);
        Collection<? extends Condition> activeConditionsOfHighestPriority = activeConditions
                .stream()
                .filter(c -> c.getPriority() == highestPriority)
                .toList();

        Constants.MUSIC_MANAGER.setCurrentMusicTracks(
                musicTracksByCondition
                        .entrySet()
                        .stream()
                        .filter(e -> activeConditionsOfHighestPriority.contains(e.getKey()))
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
            Constants.LOG.info("Conditions have been changed. Unknown condition IDs are: {}.", String.join(", ", invalidConditionIds));
        }

        if (!invalidMusicTrackIds.isEmpty()) {
            Constants.LOG.info("Music Tracks have been changed. Unknown music track ids: {}.", String.join(", ", invalidMusicTrackIds));
        }
    }

    public Map<Condition, Collection<MusicTrack>> getMusicTracksByCondition() {
        return musicTracksByCondition;
    }
}
