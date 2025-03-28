package io.github.maki99999.biomebeats.music;

import com.goxr3plus.streamplayer.stream.StreamPlayer;
import com.goxr3plus.streamplayer.stream.StreamPlayerException;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;
import io.github.maki99999.biomebeats.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Mth;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaStreamPlayer {
    private static final long FADE_DURATION = 1000;
    private static final Object STOP = new Object();
    private static final Object PAUSE = new Object();
    private static final int FADE_STEPS = 20;

    private State currentState = State.NoMusic;
    private double targetGain = 1;
    private double currentGain = targetGain;
    private MusicTrack currentSong = null;
    private Object queue = null;

    private final StreamPlayer player;
    private final ExecutorService fadeExecutor;
    private Future<?> fadeTask = null;

    private StateLogger debugLogger;

    public JavaStreamPlayer() {
        Logger.getLogger(StreamPlayer.class.getName()).setLevel(Level.OFF);
        this.player = new StreamPlayer();
        this.fadeExecutor = Executors.newSingleThreadExecutor();
    }

    JavaStreamPlayer(StateLogger debugLogger) {
        this.player = new StreamPlayer();
        this.fadeExecutor = Executors.newSingleThreadExecutor();
        this.debugLogger = debugLogger;
    }

    public void setTargetGain(double targetGain) {
        this.targetGain = targetGain;
        if (currentState != State.FadeIn && currentState != State.FadeOut) {
            currentGain = targetGain;
            player.setGain(currentGain);
        }
    }

    public String getDebugString() {
        return "S: %s C: %.3f T: %.3f".formatted(currentState.getDescription(), currentGain, targetGain);
    }

    private synchronized void setCurrentState(State currentState) {
        this.currentState = currentState;
        if (debugLogger != null) debugLogger.log("Current state: " + currentState);
    }

    private synchronized void openPlay(MusicTrack musicTrack) {
        Minecraft minecraft = Minecraft.getInstance();
        if (musicTrack instanceof ResourceLocationMusicTrack rlMusicTrack) {
            var fileResourceLocation = Sound.SOUND_LISTER.idToFile(rlMusicTrack.getResourceLocation());
            Optional<Resource> optionalResource = minecraft.getResourceManager().getResource(fileResourceLocation);

            if (optionalResource.isPresent()) {
                try {
                    player.open(new BufferedInputStream(optionalResource.get().open()));
                    player.play();
                    player.setGain(this.currentGain);
                } catch (IOException | StreamPlayerException e) {
                    Constants.LOG.error(e.getMessage(), e);
                }
            } else {
                Constants.LOG.error("Resource not found: {}", rlMusicTrack);
            }
        } else if (musicTrack instanceof FileMusicTrack fileMusicTrack) {
            try {
                player.open(fileMusicTrack.getFile());
                player.play();
                player.setGain(this.currentGain);
            } catch (StreamPlayerException e) {
                Constants.LOG.error(e.getMessage(), e);
            }
        }
    }

    public synchronized void play(MusicTrack song) {
        if (debugLogger != null) debugLogger.log("New signal: play, song: " + song);
        switch (currentState) {
            case NoMusic:
                if (song.equals(currentSong)) {
                    player.resume();
                } else if (currentSong == null) {
                    openPlay(song);
                } else {
                    stopAndResetGain();
                    openPlay(song);
                }
                startFadeIn();
                currentSong = song;
                queue = null;
                break;
            case FadeIn, Music:
                if (!song.equals(currentSong)) {
                    startFadeOut();
                    queue = song;
                }
                break;
            case FadeOut:
                if (song.equals(currentSong)) {
                    startFadeIn();
                    queue = null;
                } else {
                    queue = song;
                }
                break;
        }
    }

    private void stopAndResetGain() {
        player.stop();
        currentGain = 0;
        player.setGain(currentGain);
    }

    public synchronized void pause() {
        if (debugLogger != null) debugLogger.log("New signal: pause");
        switch (currentState) {
            case NoMusic:
                break;
            case FadeIn, Music:
                startFadeOut();
                if (queue != null && queue instanceof MusicTrack queuedMusic && queuedMusic != currentSong) {
                    queue = new PauseWithSong(queuedMusic);
                } else {
                    queue = PAUSE;
                }
                break;
            case FadeOut:
                if (queue != null && queue instanceof MusicTrack queuedMusic && queuedMusic != currentSong) {
                    queue = new PauseWithSong(queuedMusic);
                } else {
                    queue = PAUSE;
                }
                break;
        }
    }

    public synchronized void stop() {
        if (debugLogger != null) debugLogger.log("New signal: stop");
        switch (currentState) {
            case NoMusic:
                stopAndResetGain();
                break;
            case FadeIn, Music:
                startFadeOut();
                queue = STOP;
                break;
            case FadeOut:
                queue = STOP;
                break;
        }
    }

    public synchronized void resume() {
        if (debugLogger != null) debugLogger.log("New signal: resume");
        switch (currentState) {
            case NoMusic:
                startFadeIn();
                player.resume();
                queue = null;
                break;
            case FadeIn, Music:
                break;
            case FadeOut:
                startFadeIn();
                queue = null;
                break;
        }
    }

    private synchronized void fadeDone() {
        if (debugLogger != null) debugLogger.log("New signal: fadeDone");
        switch (currentState) {
            case NoMusic, Music:
                break;
            case FadeIn:
                setCurrentState(State.Music);
                queue = null;
                break;
            case FadeOut:
                processQueue();
                break;
        }
    }

    private void processQueue() {
        if (queue == null) {
            return;
        }

        if (queue == STOP) {
            setCurrentState(State.NoMusic);
            currentSong = null;
            queue = null;
            stopAndResetGain();
        } else if (queue.equals(PAUSE)) {
            setCurrentState(State.NoMusic);
            queue = null;
            player.pause();
        } else if (queue instanceof PauseWithSong pauseWithSong) {
            stopAndResetGain();
            openPlay(pauseWithSong.song);
            currentSong = pauseWithSong.song;
            setCurrentState(State.NoMusic);
            queue = null;
            player.pause();
        } else if (queue.equals(currentSong)) {
            startFadeIn();
            queue = null;
        } else if (queue instanceof MusicTrack queuedMusic) {
            startFadeIn();
            stopAndResetGain();
            openPlay(queuedMusic);
            currentSong = queuedMusic;
            queue = null;
        }
    }

    private synchronized void startFadeIn() {
        setCurrentState(State.FadeIn);
        fade(true);
    }

    private synchronized void startFadeOut() {
        setCurrentState(State.FadeOut);
        fade(false);
    }

    private synchronized void fade(boolean fadeIn) {
        if (fadeTask != null && !fadeTask.isDone()) {
            fadeTask.cancel(true);
        }
        fadeTask = fadeExecutor.submit(() -> {
            if ((!fadeIn && currentGain < 0.001d) || (fadeIn && currentGain == targetGain)) {
                fadeDone();
                return;
            }

            double startGain = currentGain;
            long stepDuration = FADE_DURATION / FADE_STEPS;

            for (double d = 0; d < 1; d += 1.0 / FADE_STEPS) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                currentGain = Mth.lerp(d, startGain, fadeIn ? targetGain : 0);
                player.setGain(currentGain);
                try {
                    Thread.sleep(stepDuration);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            currentGain = fadeIn ? targetGain : 0;
            player.setGain(currentGain);
            fadeDone();
        });
    }

    public void close() {
        if (fadeTask != null && !fadeTask.isDone()) {
            fadeTask.cancel(true);
        }
        stopAndResetGain();
        player.reset();
        fadeExecutor.close();
    }

    public void addStreamPlayerListener(StreamPlayerListener listener) {
        player.addStreamPlayerListener(listener);
    }

    public boolean isPausedOrPausing() {
        return (currentState == State.NoMusic && currentSong != null)
                || (currentState == State.FadeOut && queue != null && queue.equals(PAUSE));
    }

    public void musicEnded() {
        currentSong = null;
        queue = null;
        setCurrentState(State.NoMusic);
    }

    enum State {
        NoMusic("No Music"),
        FadeIn("Fading In"),
        FadeOut("Fading Out"),
        Music("Playing Music");

        private final String description;

        State(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    interface StateLogger {
        void log(String message);
    }

    private record PauseWithSong(MusicTrack song) {}
}
