package io.github.maki99999.biomebeats.gui;

import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.condition.*;
import io.github.maki99999.biomebeats.music.MusicTrack;
import io.github.maki99999.biomebeats.util.BiomeBeatsColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DebugHud {
    private static final int LINE_SPACING = 10;
    private static final int X_GLOBAL_OFFSET = 4;
    private static final int SMALL_OFFSET = 4;
    private static final int Y_PADDING = 2;
    private static final int Y_OFFSET = 2 + Y_PADDING;

    public static boolean enabled = true;

    public static void onRenderHUD(GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        if (mc.getDebugOverlay().showDebugScreen() || mc.options.hideGui || !enabled) {
            return;
        }


        Collection<Condition> conditions = Constants.CONDITION_MANAGER.getConditions();
        List<Condition> activeConditions = conditions.stream().filter(Condition::isConditionMet).toList();
        drawConditions(guiGraphics, font, conditions, activeConditions);

        drawMusicTracks(guiGraphics, font, activeConditions);
    }

    private static void drawMusicTracks(GuiGraphics guiGraphics, Font font, Collection<Condition> activeConditions) {
        AtomicInteger y = new AtomicInteger(Y_OFFSET);
        MusicTrack currentTrack = Constants.MUSIC_MANAGER.getCurrentMusicTrack();

        for (MusicTrack musicTrack : Constants.CONDITION_MUSIC_MANAGER.getTracksFromActiveConditions(activeConditions).stream().sorted(Comparator.comparing(x -> x != currentTrack)).toList()) {
            String text = musicTrack.getName();
            int width = font.width(text);
            guiGraphics.drawString(font, text, guiGraphics.guiWidth() - width - X_GLOBAL_OFFSET, y.get(), (musicTrack == currentTrack ? BiomeBeatsColor.BLUE : BiomeBeatsColor.WHITE).getHex());
            y.addAndGet(LINE_SPACING);
        }
    }

    private static void drawConditions(GuiGraphics guiGraphics, Font font, Collection<Condition> allConditions, Collection<Condition> activeConditions) {
        AtomicInteger y = new AtomicInteger(Y_OFFSET);

        int maxPriority = Constants.CONDITION_MUSIC_MANAGER.getHighestPriorityOfConditionsWithMusicTracks(activeConditions);

        Collection<CombinedCondition> combinedConditions = allConditions.stream().filter(x -> x instanceof CombinedCondition).map(x -> (CombinedCondition) x).toList();
        Collection<Condition> biomeConditions = activeConditions.stream().filter(x -> x instanceof BiomeCondition).toList();
        Collection<Condition> biomeTagConditions = activeConditions.stream().filter(x -> x instanceof TagCondition).toList();
        Collection<Condition> otherConditions = activeConditions.stream().filter(x -> !(x instanceof TagCondition) && !(x instanceof CombinedCondition) && !(x instanceof BiomeCondition)).toList();

        for (CombinedCondition combinedCondition : combinedConditions) {
            int color = (combinedCondition.isConditionMet() ? BiomeBeatsColor.BLUE : BiomeBeatsColor.WHITE).getHex();
            guiGraphics.drawString(font, "%s [%d] %s".formatted(combinedCondition.isConditionMet() ? "✔" : "✖", combinedCondition.getPriority(), combinedCondition.getName()), X_GLOBAL_OFFSET, y.get(), color);  // TODO: (neo)forge cannot handle ✔✖
            y.addAndGet(LINE_SPACING);
            combinedCondition.getConditionIds().stream()
                    .map(ConditionManager::getCondition)
                    .sorted(Comparator.comparing((Condition c) -> c == null ? "" : c.getName()).reversed())
                    .forEach(c -> {
                        if (c == null) {
                            guiGraphics.drawString(font, "? [?] <unknown>", X_GLOBAL_OFFSET + SMALL_OFFSET, y.get(), color);
                        } else {
                            guiGraphics.drawString(font, "%s [%d] %s".formatted(c.isConditionMet() ? "✔" : "✖", c.getPriority(), c.getName()), X_GLOBAL_OFFSET + SMALL_OFFSET, y.get(), color);  // TODO: (neo)forge cannot handle ✔✖
                        }
                        y.addAndGet(LINE_SPACING);
                    });
        }
        y.addAndGet(SMALL_OFFSET);

        drawConditionList(guiGraphics, biomeConditions, font, y, X_GLOBAL_OFFSET, maxPriority);
        drawConditionList(guiGraphics, biomeTagConditions, font, y, X_GLOBAL_OFFSET + SMALL_OFFSET, maxPriority);
        y.addAndGet(SMALL_OFFSET);
        drawConditionList(guiGraphics, otherConditions, font, y, X_GLOBAL_OFFSET, maxPriority);
    }

    private static void drawConditionList(GuiGraphics guiGraphics,
                                          Collection<Condition> conditions,
                                          Font font,
                                          AtomicInteger y,
                                          int x, int maxPrio) {
        conditions.stream()
                .sorted(Comparator.comparing(Condition::getPriority).reversed())
                .forEach(condition -> {
                    guiGraphics.drawString(font, "✔ [%d] %s".formatted(condition.getPriority(), condition.getName()), x, y.get(), condition.getPriority() == maxPrio ? BiomeBeatsColor.BLUE.getHex() : BiomeBeatsColor.WHITE.getHex());  // TODO: (neo)forge cannot handle ✔
                    y.addAndGet(LINE_SPACING);
                });
    }
}