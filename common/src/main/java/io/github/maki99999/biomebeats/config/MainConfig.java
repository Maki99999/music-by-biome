package io.github.maki99999.biomebeats.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MainConfig {
    public static final String CURRENT_VERSION = "1";

    @NotNull
    private String version = CURRENT_VERSION;
    @JsonDeserialize(using = MusicTrackIdsByConditionIdDeserializer.class)
    private Map<String, Collection<String>> musicTrackIdsByConditionId = new HashMap<>();
    private Map<String, MusicTrackConfig> musicTrackConfigById = new HashMap<>();
    private Map<String, ConditionConfig> conditionConfigById = new HashMap<>();

    public @NotNull String getVersion() {
        return version;
    }

    public void setVersion(@NotNull String version) {
        this.version = version;
    }

    public Map<String, Collection<String>> getMusicTrackIdsByConditionId() {
        return musicTrackIdsByConditionId;
    }

    public void setMusicTrackIdsByConditionId(Map<String, Collection<String>> musicTrackIdsByConditionId) {
        this.musicTrackIdsByConditionId = musicTrackIdsByConditionId;
    }

    public Map<String, MusicTrackConfig> getMusicTrackConfigById() {
        return musicTrackConfigById;
    }

    public void setMusicTrackConfigById(Map<String, MusicTrackConfig> musicTrackConfigById) {
        this.musicTrackConfigById = musicTrackConfigById;
    }

    public Map<String, ConditionConfig> getConditionConfigById() {
        return conditionConfigById;
    }

    public void setConditionConfigById(Map<String, ConditionConfig> conditionConfigById) {
        this.conditionConfigById = conditionConfigById;
    }
}
