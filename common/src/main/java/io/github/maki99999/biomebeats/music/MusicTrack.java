package io.github.maki99999.biomebeats.music;

public abstract class MusicTrack {
    private final String name;
    private final String pathName;
    private final String id;
    private String customName;
    private double volumeMultiplier = 1d;

    public MusicTrack(String name, String pathName, String id) {
        this.name = name;
        this.pathName = pathName;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        if (customName == null || customName.isEmpty() || customName.isBlank()) {
            return name;
        } else {
            return customName;
        }
    }

    public String getPathName() {
        return pathName;
    }

    public String getId() {
        return id;
    }

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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MusicTrack mt && mt.getId().equals(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
