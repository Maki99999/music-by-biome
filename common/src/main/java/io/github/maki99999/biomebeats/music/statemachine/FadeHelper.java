package io.github.maki99999.biomebeats.music.statemachine;

import io.github.maki99999.biomebeats.Constants;
import net.minecraft.util.Mth;

public class FadeHelper {
    private static final int FADE_STEPS = 20;

    public static void fade(boolean fadeIn, FadeDoneCallback fadeDoneCallback, JavaStreamPlayer ctx) {
        if ((!fadeIn && ctx.getCurrentGain() < 0.001d) || (fadeIn && ctx.getCurrentGain() == ctx.getTargetGain())) {
            fadeDoneCallback.fadeDone();
            return;
        }

        double startGain = ctx.getCurrentGain();
        long stepDuration = (Constants.CONFIG_IO.getGeneralConfig().getFadeTime() * 1000L) / FADE_STEPS;

        for (double d = 0; d < 1; d += 1.0 / FADE_STEPS) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            ctx.setCurrentGain(Mth.lerp(d, startGain, fadeIn ? ctx.getTargetGain() : 0));
            ctx.getPlayer().setGain(ctx.getCurrentGain());
            try {
                Thread.sleep(stepDuration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                ctx.getDebugLogger().log("Fade interrupted");
                return;
            }
        }

        ctx.setCurrentGain(fadeIn ? ctx.getTargetGain() : 0);
        ctx.getPlayer().setGain(ctx.getCurrentGain());
        fadeDoneCallback.fadeDone();
    }

    public interface FadeDoneCallback {
        void fadeDone();
    }
}
