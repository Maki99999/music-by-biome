package io.github.maki99999.biomebeats.service;

import java.nio.file.Path;

public interface IPlatformHelper {

    /**
     * Gets the name of the current service
     * @return The name of the current service.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }

    /**
     * Gets the config folder for this mod.
     * @return The config folder path for this mod.
     */
    Path getModConfigFolder();
}