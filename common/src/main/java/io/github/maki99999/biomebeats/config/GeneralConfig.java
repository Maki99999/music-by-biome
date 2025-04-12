package io.github.maki99999.biomebeats.config;

public class GeneralConfig {
    private int fadeTime = 1;
    private int breakTime;

    public int getFadeTime() {
        return fadeTime;
    }

    public void setFadeTime(int fadeTime) {
        this.fadeTime = fadeTime;
    }

    public int getBreakTime() {
        return breakTime;
    }

    public void setBreakTime(int breakTime) {
        this.breakTime = breakTime;
    }

    public void setDefaultBreakTime() {
        breakTime = 0;
    }

    public void setDefaultFadeTime() {
        fadeTime = 3;
    }
}
