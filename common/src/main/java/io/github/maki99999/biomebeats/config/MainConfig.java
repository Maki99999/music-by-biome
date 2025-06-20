package io.github.maki99999.biomebeats.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MainConfig {
    public static final int CURRENT_VERSION = 2;

    private int version = CURRENT_VERSION;
    @JsonIgnore
    private boolean newConfig = false;
    @JsonDeserialize(using = MusicTrackIdsByConditionIdDeserializer.class)
    private Map<String, Collection<String>> musicTrackIdsByConditionId = new HashMap<>();
    private Map<String, MusicTrackConfig> musicTrackConfigById = new HashMap<>();
    private Map<String, ConditionConfig> conditionConfigById = new HashMap<>();
    private Collection<CombinedConditionConfig> combinedConditionConfigs = new ArrayList<>();
    private GeneralConfig generalConfig = new GeneralConfig();

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
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

    public GeneralConfig getGeneralConfig() {
        return generalConfig;
    }

    public void setGeneralConfig(GeneralConfig generalConfig) {
        this.generalConfig = generalConfig;
    }

    public Collection<CombinedConditionConfig> getCombinedConditionConfigs() {
        return combinedConditionConfigs;
    }

    public void setCombinedConditionConfigs(Collection<CombinedConditionConfig> combinedConditionConfigs) {
        this.combinedConditionConfigs = combinedConditionConfigs;
    }

    public boolean isNewConfig() {
        return newConfig;
    }

    void setNewConfig(boolean newConfig) {
        this.newConfig = newConfig;
    }

    public static MainConfig fromScratch() {
        MainConfig mainConfig = new MainConfig();
        mainConfig.setNewConfig(true);
        return mainConfig;
    }
}
