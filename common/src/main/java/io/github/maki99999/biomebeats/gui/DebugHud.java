package io.github.maki99999.biomebeats.gui;

import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.condition.*;
import io.github.maki99999.biomebeats.music.MusicTrack;
import io.github.maki99999.biomebeats.service.Services;
import io.github.maki99999.biomebeats.gui.util.BiomeBeatsColor;
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

    public static boolean enabled = Services.PLATFORM.isDevelopmentEnvironment();

    public static void onRenderHUD(GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        if (mc.options.renderDebug || mc.options.hideGui || !enabled) {
            return;
        }

        Collection<Condition> conditions = Constants.CONDITION_MANAGER.getConditions();
        List<Condition> activeConditions = conditions.stream().filter(Condition::isConditionMet).toList();
        drawConditions(guiGraphics, font, conditions, activeConditions);

        drawMusicPlayerInfo(guiGraphics, font);
        drawMusicTracks(guiGraphics, font, activeConditions);
    }

    private static void drawMusicPlayerInfo(GuiGraphics guiGraphics, Font font) {
        AtomicInteger y = new AtomicInteger(Y_OFFSET);

        String text = Constants.MUSIC_MANAGER.getDebugString1();
        int width = font.width(text);
        guiGraphics.drawString(font, text, guiGraphics.guiWidth() - width - X_GLOBAL_OFFSET, y.get(), BiomeBeatsColor.WHITE.getHex());
        y.addAndGet(LINE_SPACING);

        text = Constants.MUSIC_MANAGER.getDebugString2();
        width = font.width(text);
        guiGraphics.drawString(font, text, guiGraphics.guiWidth() - width - X_GLOBAL_OFFSET, y.get(), BiomeBeatsColor.WHITE.getHex());
    }

    private static void drawMusicTracks(GuiGraphics guiGraphics, Font font, Collection<Condition> activeConditions) {
        AtomicInteger y = new AtomicInteger(Y_OFFSET + LINE_SPACING + LINE_SPACING + SMALL_OFFSET);
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
        Collection<Condition> inactiveBiomeConditions = allConditions.stream().filter(x -> x instanceof BiomeCondition && !x.isConditionMet()).toList();
        Collection<Condition> biomeTagConditions = activeConditions.stream().filter(x -> x instanceof TagCondition).toList();
        Collection<Condition> inactiveBiomeTagConditions = allConditions.stream().filter(x -> x instanceof TagCondition && !x.isConditionMet()).toList();
        Collection<Condition> otherConditions = activeConditions.stream().filter(x -> !(x instanceof TagCondition) && !(x instanceof CombinedCondition) && !(x instanceof BiomeCondition)).toList();
        Collection<Condition> inactiveOtherConditions = allConditions.stream().filter(x -> !(x instanceof TagCondition) && !(x instanceof CombinedCondition) && !(x instanceof BiomeCondition) && !x.isConditionMet()).toList();

        // Draw Combined Conditions with subconditions
        for (CombinedCondition combinedCondition : combinedConditions) {
            int color = (combinedCondition.getPriority() == maxPriority ? BiomeBeatsColor.BLUE : BiomeBeatsColor.WHITE).getHex();
            guiGraphics.drawString(font, "%s [%d] %s".formatted(combinedCondition.isConditionMet() ? "✔" : "✖", combinedCondition.getPriority(), combinedCondition.getName()), X_GLOBAL_OFFSET, y.get(), color);
            y.addAndGet(LINE_SPACING);
            combinedCondition.getConditionIds().stream()
                    .map(ConditionManager::getCondition)
                    .sorted(Comparator.comparing((Condition c) -> c == null ? "" : c.getName()).reversed())
                    .forEach(c -> {
                        if (c == null) {
                            guiGraphics.drawString(font, "? [?] <unknown>", X_GLOBAL_OFFSET + SMALL_OFFSET, y.get(), color);
                        } else {
                            guiGraphics.drawString(font, "%s [%d] %s".formatted(c.isConditionMet() ? "✔" : "✖", c.getPriority(), c.getName()), X_GLOBAL_OFFSET + SMALL_OFFSET, y.get(), color);
                        }
                        y.addAndGet(LINE_SPACING);
                    });
        }
        y.addAndGet(SMALL_OFFSET);

        drawConditionList(guiGraphics, biomeConditions, font, y, X_GLOBAL_OFFSET, maxPriority);
        drawConditionList(guiGraphics, biomeTagConditions, font, y, X_GLOBAL_OFFSET + SMALL_OFFSET, maxPriority);

        guiGraphics.drawString(font, "✖ %d inactive Biome Conditions,".formatted(inactiveBiomeConditions.size()), X_GLOBAL_OFFSET, y.get(), BiomeBeatsColor.WHITE.getHex());
        y.addAndGet(LINE_SPACING);
        guiGraphics.drawString(font, "    %d inactive Tag Conditions".formatted(inactiveBiomeTagConditions.size()), X_GLOBAL_OFFSET, y.get(), BiomeBeatsColor.WHITE.getHex());
        y.addAndGet(LINE_SPACING + SMALL_OFFSET);

        drawConditionList(guiGraphics, otherConditions, font, y, X_GLOBAL_OFFSET, maxPriority);
        guiGraphics.drawString(font, "✖ %d inactive Other Conditions".formatted(inactiveOtherConditions.size()), X_GLOBAL_OFFSET, y.get(), BiomeBeatsColor.WHITE.getHex());
    }

    private static void drawConditionList(GuiGraphics guiGraphics,
                                          Collection<Condition> conditions,
                                          Font font,
                                          AtomicInteger y,
                                          int x, int maxPriority) {
        conditions.stream()
                .sorted(Comparator.comparing(Condition::getPriority).reversed())
                .forEach(condition -> {
                    String priorityString = condition.getPriority() == Integer.MIN_VALUE ? "-∞" : "%d".formatted(condition.getPriority());
                    guiGraphics.drawString(font, "✔ [%s] %s".formatted(priorityString, condition.getName()), x, y.get(), condition.getPriority() == maxPriority ? BiomeBeatsColor.BLUE.getHex() : BiomeBeatsColor.WHITE.getHex());
                    y.addAndGet(LINE_SPACING);
                });
    }
}