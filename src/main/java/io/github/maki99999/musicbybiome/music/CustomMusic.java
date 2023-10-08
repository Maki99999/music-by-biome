package io.github.maki99999.musicbybiome.music;

import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;

public class CustomMusic {

    private final Music music;
    private final Music replacingMusic;

    public CustomMusic(SoundEvent soundEvent) {
        Holder<SoundEvent> soundEventHolder = new Holder.Direct<>(soundEvent);
        music = new Music(soundEventHolder, 0, 0, false);
        replacingMusic = new Music(soundEventHolder, 0, 0, true);
    }

    public Music getMusic() {
        return music;
    }

    public Music getReplacingMusic() {
        return replacingMusic;
    }
}
