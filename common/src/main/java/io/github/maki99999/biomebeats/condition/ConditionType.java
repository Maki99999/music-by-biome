package io.github.maki99999.biomebeats.condition;

import net.minecraft.network.chat.Component;

public enum ConditionType {
    BIOME(Component.translatable("menu.biomebeats.by_biome")),
    TAG(Component.translatable("menu.biomebeats.by_tag")),
    OTHER(Component.translatable("menu.biomebeats.by_other")),
    COMBINED(Component.translatable("menu.biomebeats.combined"));

    private final Component component;

    ConditionType(Component component) {
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }
}
