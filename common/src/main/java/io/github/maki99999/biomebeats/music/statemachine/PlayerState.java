package io.github.maki99999.biomebeats.music.statemachine;

import io.github.maki99999.biomebeats.music.MusicTrack;

public abstract class PlayerState {
    protected final JavaStreamPlayer ctx;

    public PlayerState(JavaStreamPlayer ctx) {
        this.ctx = ctx;
    }

    public abstract void play(MusicTrack song);
    public abstract void pause();
    public abstract void stop();
    public abstract void resume();
    public abstract void musicEnded(MusicTrack nextSong);
    public abstract String getName();
    public abstract void close();

    public boolean controlsGain() {
        return false;
    }
}
