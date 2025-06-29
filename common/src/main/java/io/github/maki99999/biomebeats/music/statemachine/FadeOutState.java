package io.github.maki99999.biomebeats.music.statemachine;

import io.github.maki99999.biomebeats.music.MusicTrack;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FadeOutState extends PlayerState {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Future<?> fadeTask;
    private PlaybackIntent queue;

    FadeOutState(JavaStreamPlayer ctx, PlaybackIntent queue) {
        super(ctx);
        this.queue = queue;
        fadeTask = executorService.submit(() -> FadeHelper.fade(false, this::processQueue, ctx));
    }

    @Override
    public void play(MusicTrack song) {
        if (Objects.equals(song, ctx.getCurrentSong())) {
            ctx.setStateFadeIn(ctx.getCurrentSong());
        } else {
            queue = new PlaybackIntent.Play(song);
        }
    }

    @Override
    public void pause() {
        if (queue instanceof PlaybackIntent.Play playIntent && playIntent.song() != ctx.getCurrentSong()) {
            queue = new PlaybackIntent.PauseWithSong(playIntent.song());
        } else if (!(queue instanceof PlaybackIntent.Pause)) {
            queue = new PlaybackIntent.Pause();
        }
    }

    @Override
    public void stop() {
        queue = new PlaybackIntent.Stop();
    }

    @Override
    public void resume() {
        ctx.setStateFadeIn(ctx.getCurrentSong());
    }

    void processQueue() {
        if (queue instanceof PlaybackIntent.Stop) {
            ctx.setStateIdle();
        } else if (queue instanceof PlaybackIntent.Pause) {
            ctx.setStatePause();
        } else if (queue instanceof PlaybackIntent.PauseWithSong intent) {
            ctx.setStatePause(intent.song());
        } else if (queue instanceof PlaybackIntent.Play intent) {
            ctx.setStateFadeIn(intent.song());
        }
    }

    @Override
    public void musicEnded(MusicTrack nextSong) {
        if (queue instanceof PlaybackIntent.Pause) {
            ctx.stopOpenPlayPause(nextSong);
        }

        cancelFadeTask();
        processQueue();
    }

    @Override
    public String getName() {
        return "Fade Out";
    }

    @Override
    public boolean controlsGain() {
        return true;
    }

    public boolean isPausing() {
        return queue != null && queue instanceof PlaybackIntent.Pause;
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
