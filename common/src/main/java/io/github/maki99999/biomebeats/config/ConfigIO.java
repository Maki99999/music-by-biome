package io.github.maki99999.biomebeats.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.conditionmusic.ConditionMusicManager;
import io.github.maki99999.biomebeats.service.Services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;

public class ConfigIO {
    private final Collection<ConfigChangeListener> listeners = new HashSet<>();
    private ConfigChangeListener conditionMusicManagerConfigChangeListener;
    private MainConfig config;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Path getConfigFilePath() {
        return Services.PLATFORM.getModConfigFolder().resolve(Constants.CONFIG_FILENAME);
    }

    public void addListener(ConfigChangeListener listener) {
        listeners.add(listener);
        if (config != null) listener.afterConfigChange(config);
    }

    public void addListenerConditionMusicManager(ConditionMusicManager conditionMusicManager) {
        conditionMusicManagerConfigChangeListener = conditionMusicManager;
    }

    public void removeListener(ConfigChangeListener listener) {
        listeners.remove(listener);
    }

    public void saveConfig(MainConfig config) {
        config.setNewConfig(false);
        notifyBeforeConfigChangeListeners(config);

        setConfig(config);
    }

    private void setConfig(MainConfig config) {
        Path configFilePath = getConfigFilePath();

        try {
            Files.createDirectories(configFilePath.getParent());

            objectMapper.writeValue(configFilePath.toFile(), config);
            this.config = config;

            notifyAfterConfigChangeListeners(config);

            Constants.LOG.info("Configuration saved to {}", configFilePath);
        } catch (IOException e) {
            Constants.LOG.error("Failed to save configuration to {}", configFilePath, e);
        }
    }

    private void notifyBeforeConfigChangeListeners(MainConfig config) {
        for (ConfigChangeListener listener : listeners) {
            listener.beforeConfigChange(config);
        }
        conditionMusicManagerConfigChangeListener.beforeConfigChange(config);
    }

    private void notifyAfterConfigChangeListeners(MainConfig config) {
        for (ConfigChangeListener listener : listeners) {
            listener.afterConfigChange(config);
        }
        conditionMusicManagerConfigChangeListener.afterConfigChange(config);
    }

    public void loadConfig() {
        Path configFilePath = getConfigFilePath();
        if (Files.exists(configFilePath)) {
            try {
                config = objectMapper.readValue(configFilePath.toFile(), MainConfig.class);
                Constants.LOG.info("Configuration loaded from {}", configFilePath);
            } catch (IOException e) {
                Constants.LOG.error("Failed to load configuration from {}", configFilePath, e);
            }
        } else {
            Constants.LOG.warn("Configuration file {} does not exist", configFilePath);
        }

        if (config == null) {
            config = MainConfig.fromScratch();
        }

        notifyAfterConfigChangeListeners(config);
    }

    public void updateConfigListeners() {
        notifyAfterConfigChangeListeners(config);
    }

    public void resetConfig() {
        MainConfig config = MainConfig.fromScratch();
        setConfig(config);
        saveConfig(config);
    }

    public void clearConfig() {
        setConfig(new MainConfig());
    }

    public GeneralConfig getGeneralConfig() {
        return config.getGeneralConfig();
    }
}
