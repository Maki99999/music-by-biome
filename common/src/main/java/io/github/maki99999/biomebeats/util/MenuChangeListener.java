package io.github.maki99999.biomebeats.util;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;

public interface MenuChangeListener {
    void onMenuChanged(Screen screen, Player player);
}
