package io.github.maki99999.biomebeats.music.statemachine;

import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.music.MusicTrack;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WaitingBreakState extends IdleState {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledFuture<?> breakTask;
    private final MusicTrack track;

    public WaitingBreakState(JavaStreamPlayer context, MusicTrack track) {
        super(context);
        this.track = track;

        ctx.unsetCurrentSong();

        breakTask = scheduler.schedule(
                () -> super.play(track),
                Constants.CONFIG_IO.getGeneralConfig().getBreakTime(),
                TimeUnit.SECONDS
        );
    }

    @Override
    public void play(MusicTrack song) {
        cancelTask();
        super.play(song);
    }

    @Override
    public void pause() {
        cancelTask();
        ctx.openPlay(track);
        ctx.setStatePause();
    }

    @Override
    public void stop() {
        cancelTask();
        ctx.setStateIdle();
    }

    @Override
    public String getName() {
        return "Waiting Break";
    }

    private void cancelTask() {
        if (breakTask != null) {
            breakTask.cancel(true);
        }
    }

    @Override
    public void close() {
        breakTask.cancel(true);
    }
}
