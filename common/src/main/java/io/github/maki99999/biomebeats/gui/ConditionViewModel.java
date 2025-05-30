package io.github.maki99999.biomebeats.gui;

import io.github.maki99999.biomebeats.condition.Condition;
import io.github.maki99999.biomebeats.gui.util.BiomeBeatsColor;

public class ConditionViewModel {
    private final Condition condition;
    private final boolean hasMusicTracks;
    private boolean selected = false;

    public ConditionViewModel(Condition condition, boolean hasMusicTracks) {
        this.condition = condition;
        this.hasMusicTracks = hasMusicTracks;
    }

    public Condition getCondition() {
        return condition;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getIndicatorColor() {
        if (hasMusicTracks) {
            return condition.isConditionMet() ? BiomeBeatsColor.GREEN.getHex() : BiomeBeatsColor.RED.getHex();
        } else{
            return condition.isConditionMet() ? BiomeBeatsColor.GREEN_50.getHex() : BiomeBeatsColor.TRANSPARENT.getHex();
        }
    }

    public String getName() {
        return condition.getName();
    }
}
