package io.github.maki99999.biomebeats.condition;

import io.github.maki99999.biomebeats.BiomeBeatsCommon;
import io.github.maki99999.biomebeats.util.MenuChangeListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ScreenCondition extends Condition implements MenuChangeListener {
    private final String id;
    private final Class<? extends Screen> screen;

    public ScreenCondition(String id, String name, Class<? extends Screen> screen) {
        super(name);
        this.id = id;
        this.screen = screen;
        BiomeBeatsCommon.addMenuChangeListener(this);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Component getTypeName() {
        return Component.translatable("menu.biomebeats.by_other");
    }

    public Class<? extends Screen> getScreen() {
        return screen;
    }

    @Override
    public void onMenuChanged(Screen screen, Player player) {
        setConditionMet(this.screen == null ? player == null :
                (screen != null && this.screen.equals(screen.getClass())));
    }
}
