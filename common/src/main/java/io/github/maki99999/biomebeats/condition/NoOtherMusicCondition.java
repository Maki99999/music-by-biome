package io.github.maki99999.biomebeats.condition;

import net.minecraft.network.chat.Component;

public class NoOtherMusicCondition extends Condition {
    public NoOtherMusicCondition() {
        super("No Other Music Is Playing");
        setConditionMet(true);
    }

    @Override
    public String getId() {
        return "NoOtherMusic";
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public Component getTypeName() {
        return Component.translatable("menu.biomebeats.by_other");
    }
}
