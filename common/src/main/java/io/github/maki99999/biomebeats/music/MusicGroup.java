package io.github.maki99999.biomebeats.music;

import java.util.Collection;

import static io.github.maki99999.biomebeats.util.StringUtils.formatToTitleCase;
import static net.minecraft.client.resources.language.I18n.get;

public final class MusicGroup {
    private final String name;
    private final Collection<? extends MusicTrack> musicTracks;

    public MusicGroup(String namespace, Type type, Collection<? extends MusicTrack> musicTracks) {
        this.name = formatToTitleCase(namespace) + " (" + get(type.getTranslationKey()) + ")";
        this.musicTracks = musicTracks;
    }

    public MusicGroup(Collection<FileMusicTrack> musicTracks) {
        this.name = get(Type.CUSTOM.getTranslationKey());
        this.musicTracks = musicTracks;
    }

    public String getName() {
        return name;
    }

    public Collection<? extends MusicTrack> getMusicTracks() {return musicTracks;}

    public enum Type {
        BGM("menu.biomebeats.categories.bgm"),
        MUSIC_DISC("menu.biomebeats.categories.music_disc"),
        CUSTOM("menu.biomebeats.categories.custom"),
        UNKNOWN("menu.biomebeats.categories.others");

        private final String translationKey;

        Type(String translationKey) {
            this.translationKey = translationKey;
        }

        public String getTranslationKey() {
            return translationKey;
        }
    }
}
