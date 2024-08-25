package io.github.maki99999.biomebeats.condition;

import io.github.maki99999.biomebeats.BiomeBeatsCommon;
import io.github.maki99999.biomebeats.util.MenuChangeListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;

public class MenuCondition extends Condition implements MenuChangeListener {
    private final String id;
    private final Class<? extends Screen> screen;

    public MenuCondition(String id, String name, Class<? extends Screen> screen) {
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
    public void onMenuChanged(Screen screen, Player player) {
        setConditionMet(this.screen == null ? player == null : this.screen.equals(screen.getClass()));
    }
}
