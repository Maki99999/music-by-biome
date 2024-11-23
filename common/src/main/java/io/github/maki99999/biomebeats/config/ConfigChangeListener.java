package io.github.maki99999.biomebeats.config;

public interface ConfigChangeListener {
    void beforeConfigChange(MainConfig config);
    void afterConfigChange(MainConfig config);
}
