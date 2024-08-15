package io.github.maki99999.biomebeats.music;

import com.goxr3plus.streamplayer.stream.StreamPlayer;
import com.goxr3plus.streamplayer.stream.StreamPlayerException;
import io.github.maki99999.biomebeats.Constants;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaStreamPlayer extends StreamPlayer {
    private double currentGain = 1;

    public JavaStreamPlayer() {
        Logger.getLogger(StreamPlayer.class.getName()).setLevel(Level.OFF);
    }

    public void stopOpenPlay(Object input) {
            stop();
            try {
                open(input);
                play();
            } catch (StreamPlayerException e) {
                Constants.LOG.error(e.getMessage(), e);
            }
            setGain(currentGain);
    }

    @Override
    public void setGain(double fGain) {
        currentGain = fGain;
        super.setGain(fGain);
    }

    public void close() {
        stop();
        reset();
    }
}