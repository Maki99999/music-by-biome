package io.github.maki99999.biomebeats.music;

import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.service.Services;

import java.io.File;

import static io.github.maki99999.biomebeats.util.StringUtils.formatToTitleCase;

public class FileMusicTrack extends MusicTrack {
    private final String filePath;

    public FileMusicTrack(String filePath) {
        super(formatToTitleCase(filePath), filePath, filePath);
        this.filePath = filePath;
    }

    public File getFile() {
        return Services.PLATFORM.getModConfigFolder().resolve(Constants.MUSIC_FOLDER).resolve(filePath).toFile();
    }
}
