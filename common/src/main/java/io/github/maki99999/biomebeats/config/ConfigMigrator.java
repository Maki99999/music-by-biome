package io.github.maki99999.biomebeats.config;

import io.github.maki99999.biomebeats.Constants;

public class ConfigMigrator {
    public static void migrate(MainConfig config) {
        int version = config.getVersion();

        if (version != MainConfig.CURRENT_VERSION) {
            Constants.LOG.info("SettingsMigrator - file version: {}, current version: {}",
                               version,
                               MainConfig.CURRENT_VERSION);
        }

        if (version < 2) {
            migrateToV2(config);
        }

        config.setVersion(MainConfig.CURRENT_VERSION);
    }

    private static void migrateToV2(MainConfig config) {
        for (MusicTrackConfig trackConfig : config.getMusicTrackConfigById().values()) {
            if (trackConfig.getVolumeMultiplier() == 0.0d) {
                trackConfig.setVolumeMultiplier(1.0d);
            }
        }
    }
}
