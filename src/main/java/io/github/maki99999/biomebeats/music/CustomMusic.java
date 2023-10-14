package io.github.maki99999.biomebeats.music;

import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;

public class CustomMusic {

    private final String name;
    private final Music music;
    private final Music replacingMusic;

    public CustomMusic(String name, SoundEvent soundEvent) {
        this.name = name;
        Holder<SoundEvent> soundEventHolder = new Holder.Direct<>(soundEvent);
        music = new Music(soundEventHolder, 10, 10, false);
        replacingMusic = new Music(soundEventHolder, 10, 10, true);
    }

    public String getName() {
        return name;
    }

    public Music getMusic() {
        return music;
    }

    public Music getReplacingMusic() {
        return replacingMusic;
    }
}
