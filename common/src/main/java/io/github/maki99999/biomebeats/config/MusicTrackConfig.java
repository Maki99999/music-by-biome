package io.github.maki99999.biomebeats.config;

public class MusicTrackConfig {
    private String customName;
    private double volumeMultiplier = 1d;

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public double getVolumeMultiplier() {
        return volumeMultiplier;
    }

    public void setVolumeMultiplier(double volumeMultiplier) {
        this.volumeMultiplier = volumeMultiplier;
    }
}
