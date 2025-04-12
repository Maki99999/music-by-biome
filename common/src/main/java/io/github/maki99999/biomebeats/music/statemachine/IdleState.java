package io.github.maki99999.biomebeats.music.statemachine;

import io.github.maki99999.biomebeats.music.MusicTrack;

public class IdleState extends PlayerState {
    public IdleState(JavaStreamPlayer context) {
        super(context);
        ctx.unsetCurrentSong();
        ctx.stopAndResetGain();
    }

    @Override
    public void play(MusicTrack song) {
        ctx.setStateMusic(song);
    }

    @Override
    public void pause() {
        JavaStreamPlayer.suspiciousTransition(getName(), "pause");
    }

    @Override
    public void stop() {}

    @Override
    public void resume() {
        JavaStreamPlayer.suspiciousTransition(getName(), "resume");
    }

    @Override
    public void musicEnded(MusicTrack nextSong) {
        JavaStreamPlayer.suspiciousTransition(getName(), "musicEnded");
    }

    @Override
    public String getName() {
        return "Idle";
    }

    @Override
    public void close() {}
}
