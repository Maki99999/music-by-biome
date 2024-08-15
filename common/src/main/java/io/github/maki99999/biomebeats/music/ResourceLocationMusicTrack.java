package io.github.maki99999.biomebeats.music;

import net.minecraft.resources.ResourceLocation;

import static io.github.maki99999.biomebeats.util.StringUtils.formatToTitleCase;

public class ResourceLocationMusicTrack extends MusicTrack {
    private final ResourceLocation resourceLocation;

    public ResourceLocationMusicTrack(ResourceLocation resourceLocation) {
        super(formatToTitleCase(resourceLocation), formatToTitleCase(resourceLocation, true), resourceLocation.toString());
        this.resourceLocation = resourceLocation;
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }
}
