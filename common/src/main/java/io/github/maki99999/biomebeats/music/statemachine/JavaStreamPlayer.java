package io.github.maki99999.biomebeats.music.statemachine;

import com.goxr3plus.streamplayer.stream.StreamPlayer;
import com.goxr3plus.streamplayer.stream.StreamPlayerException;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;
import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.music.FileMusicTrack;
import io.github.maki99999.biomebeats.music.MusicTrack;
import io.github.maki99999.biomebeats.music.ResourceLocationMusicTrack;
import io.github.maki99999.biomebeats.service.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaStreamPlayer {
    private final String name;
    private final StreamPlayer player;
    private final ExecutorService mainExecutor;
    private final StateLogger debugLogger;
    private final Object gainLock = new Object();
    private Thread mainExecutorThread = null;
    private @NotNull PlayerState state;
    private double baseTargetGain = 1;
    private double targetGain = baseTargetGain;
    private double currentGain = targetGain;
    private MusicTrack currentSong = null;

    public JavaStreamPlayer(String name) {
        this.name = name;
        mainExecutor = Executors.newSingleThreadExecutor(r -> {
            mainExecutorThread = new Thread(r);
            mainExecutorThread.setName("BiomeBeats-StreamPlayerThread-" + name);
            mainExecutorThread.setDaemon(true);
            return mainExecutorThread;
        });

        debugLogger = Services.PLATFORM.isDevelopmentEnvironment() ? Constants.LOG::debug : null;
        Logger.getLogger(StreamPlayer.class.getName()).setLevel(Level.OFF);

        this.player = new StreamPlayer();
        state = new IdleState(this);
    }

    static void suspiciousTransition(String stateName, String signalName) {
        Constants.LOG.warn("Suspicious transition: {} {}", stateName, signalName);
    }

    public void runOnMain(Runnable runnable) {
        if (Thread.currentThread() == mainExecutorThread) {
            runnable.run();
        } else {
            mainExecutor.execute(runnable);
        }
    }

    double getCurrentGain() {
        synchronized (gainLock) {
            return currentGain;
        }
    }

    void setCurrentGain(double currentGain) {
        synchronized (gainLock) {
            this.currentGain = currentGain;
        }
    }

    double getTargetGain() {
        return targetGain;
    }

    double getBaseTargetGain() {
        return baseTargetGain;
    }

    public void setTargetGain(double targetGain) {
        baseTargetGain = targetGain;
        double newTargetGain = baseTargetGain * (currentSong == null ? 1 : currentSong.getVolumeMultiplier());

        if (newTargetGain == this.targetGain) {
            return;
        }

        this.targetGain = newTargetGain;
        runOnMain(() -> {
            if (!state.controlsGain()) {
                synchronized (gainLock) {
                    currentGain = this.targetGain;
                }
                player.setGain(currentGain);
            }
        });
    }

    MusicTrack getCurrentSong() {
        return currentSong;
    }

    void unsetCurrentSong() {
        this.currentSong = null;
    }

    StreamPlayer getPlayer() {
        return player;
    }

    StateLogger getDebugLogger() {
        return debugLogger;
    }

    public void close() {
        state.close();
        stopAndResetGain();
        player.reset();
        mainExecutor.shutdownNow();
    }

    public void addStreamPlayerListener(StreamPlayerListener listener) {
        player.addStreamPlayerListener(listener);
    }

    public String getDebugString() {
        return "%s S: %s C: %.3f T: %.3f".formatted(name, state.getName(), currentGain, targetGain);
    }

    public void play(MusicTrack song) {
        runOnMain(() -> {
            logTransition("play");
            state.play(song);
        });
    }

    public void pause() {
        runOnMain(() -> {
            logTransition("pause");
            state.pause();
        });
    }

    public void stop() {
        runOnMain(() -> {
            logTransition("stop");
            state.stop();
        });
    }

    public void resume() {
        runOnMain(() -> {
            logTransition("resume");
            state.resume();
        });
    }

    public void resumeIfPausedOrPausing() {
        runOnMain(() -> {
            if (state instanceof PausedState || (state instanceof FadeOutState fadeOutState && fadeOutState.isPausing())) {
                resume();
            }
        });
    }

    public void musicEnded(MusicTrack nextSong) {
        runOnMain(() -> {
            logTransition("musicEnded");
            state.musicEnded(nextSong);
        });
    }

    public void openPlay(MusicTrack musicTrack) {
        runOnMain(() -> {
            currentSong = musicTrack;
            targetGain = baseTargetGain * musicTrack.getVolumeMultiplier();

            Minecraft minecraft = Minecraft.getInstance();
            if (musicTrack instanceof ResourceLocationMusicTrack rlMusicTrack) {
                ResourceLocation fileResourceLocation = Sound.SOUND_LISTER.idToFile(rlMusicTrack.getResourceLocation());
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
        });
    }

    void stopAndResetGain() {
        runOnMain(() -> {
            player.stop();
            synchronized (gainLock) {
                currentGain = 0;
            }
            player.setGain(currentGain);
        });
    }

    void stopOpenPlayPause(MusicTrack track) {
        stopAndResetGain();
        openPlay(track);
        player.pause();
    }

    void setStateFadeIn(MusicTrack track) {
        setState(new FadeInState(this, track));
    }

    void setStateFadeOut(PlaybackIntent queue) {
        setState(new FadeOutState(this, queue));
    }

    void setStateIdle() {
        setState(new IdleState(this));
    }

    void setStatePause() {
        setState(new PausedState(this));
    }

    void setStatePause(MusicTrack track) {
        setState(new PausedState(this, track));
    }

    void setStateMusic() {
        setState(new MusicState(this));
    }

    void setStateMusic(MusicTrack track) {
        setState(new MusicState(this, track));
    }

    void setStateWaitingBreak(MusicTrack nextSong) {
        setState(new WaitingBreakState(this, nextSong));
    }

    private void setState(PlayerState newState) {
        runOnMain(() -> {
            if (debugLogger != null) {
                debugLogger.log("%s - Current State: %s, New State: %s".formatted(name, state.getName(), newState.getName()));
            }
            state.close();
            state = newState;
        });
    }

    private void logTransition(String transition) {
        if (debugLogger != null) {
            debugLogger.log("%s - Current State: %s, New Transition: %s".formatted(name, state.getName(), transition));
        }
    }

    public interface StateLogger {
        void log(String message);
    }
}
