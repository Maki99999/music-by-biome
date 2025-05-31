package io.github.maki99999.biomebeats.music.statemachine;

import io.github.maki99999.biomebeats.music.MusicTrack;

public class MusicState extends PlayerState {
    public MusicState(JavaStreamPlayer ctx) {
        super(ctx);
    }

    public MusicState(JavaStreamPlayer ctx, MusicTrack track) {
        super(ctx);

        if (track.equals(ctx.getCurrentSong())) {
            return;
        }

        ctx.setCurrentGain(ctx.getBaseTargetGain() * track.getVolumeMultiplier());
        ctx.openPlay(track);
    }

    @Override
    public void play(MusicTrack song) {
        if (!song.equals(ctx.getCurrentSong())) {
            ctx.setStateFadeOut(new PlaybackIntent.Play(song));
        }
    }

    @Override
    public void pause() {
        ctx.setStateFadeOut(new PlaybackIntent.Pause());
    }

    @Override
    public void stop() {
        ctx.setStateFadeOut(new PlaybackIntent.Stop());
    }

    @Override
    public void resume() {
        JavaStreamPlayer.suspiciousTransition(getName(), "resume");
    }

    @Override
    public void musicEnded(MusicTrack nextSong) {
        ctx.setStateWaitingBreak(nextSong);
    }

    @Override
    public String getName() {
        return "Music";
    }

    @Override
    public void close() {}
}
