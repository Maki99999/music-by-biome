package io.github.maki99999.biomebeats.music.statemachine;

import io.github.maki99999.biomebeats.music.MusicTrack;

public class PausedState extends PlayerState {

    public PausedState(JavaStreamPlayer ctx) {
        super(ctx);
        ctx.getPlayer().pause();
    }

    public PausedState(JavaStreamPlayer ctx, MusicTrack track) {
        super(ctx);
        ctx.stopOpenPlayPause(track);
    }

    @Override
    public void play(MusicTrack song) {
        ctx.setStateFadeIn(song);
    }

    @Override
    public void pause() {
        // already paused
    }

    @Override
    public void stop() {
        ctx.setStateIdle();
    }

    @Override
    public void resume() {
        ctx.setStateFadeIn(ctx.getCurrentSong());
    }

    @Override
    public void musicEnded(MusicTrack nextSong) {
        ctx.stopOpenPlayPause(nextSong);
    }

    @Override
    public String getName() {
        return "Paused";
    }

    @Override
    public void close() {}
}
