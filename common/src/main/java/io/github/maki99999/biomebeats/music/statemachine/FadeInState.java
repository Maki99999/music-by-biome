package io.github.maki99999.biomebeats.music.statemachine;

import io.github.maki99999.biomebeats.music.MusicTrack;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FadeInState extends PlayerState {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    Future<?> fadeTask;

    public FadeInState(JavaStreamPlayer ctx, MusicTrack track) {
        super(ctx);

        if (!Objects.equals(track, ctx.getCurrentSong())) {
            ctx.stopAndResetGain();
            ctx.openPlay(track);
        } else {
            ctx.getPlayer().resume();
        }

        fadeTask = executorService.submit(() -> FadeHelper.fade(true, ctx::setStateMusic, ctx));
    }

    @Override
    public void play(MusicTrack song) {
        if (!Objects.equals(song, ctx.getCurrentSong())) {
            ctx.setStateFadeOut(new PlaybackIntent.Play(song));
        }
    }

    @Override
    public void pause() {
        ctx.setStatePause();
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
        cancelFadeTask();
        ctx.setStateWaitingBreak(nextSong);
    }

    @Override
    public String getName() {
        return "Fade In";
    }

    @Override
    public boolean controlsGain() {
        return true;
    }

    void cancelFadeTask() {
        if (fadeTask != null && !fadeTask.isDone()) {
            fadeTask.cancel(true);
        }
    }

    @Override
    public void close() {
        fadeTask.cancel(true);
        executorService.shutdownNow();
    }
}
