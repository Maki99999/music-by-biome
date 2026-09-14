package io.github.maki99999.biomebeats.condition;

import io.github.maki99999.biomebeats.BiomeBeatsCommon;
import io.github.maki99999.biomebeats.util.TickListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.WinScreen;

public class ScreenCondition extends Condition implements TickListener {
    private final ScreenType screenType;

    public ScreenCondition(String name, ScreenType screenType) {
        super(screenType.getId(), ConditionType.OTHER, name);
        this.screenType = screenType;
        BiomeBeatsCommon.addTickListener(this);
    }

    @Override
    public void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;

        if (screenType == ScreenType.MAIN_MENU) {
            var screenMusic = screen == null ? null : screen.getBackgroundMusic();
            setConditionMet(screenMusic == null && minecraft.player == null);
        } else if (screenType == ScreenType.WIN_SCREEN) {
            setConditionMet(screen instanceof WinScreen);
        }
    }

    public ScreenType getScreenType() {
        return screenType;
    }

    public enum ScreenType {
        MAIN_MENU("MainMenu"),
        WIN_SCREEN("WinScreen");

        private final String id;

        ScreenType(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return id;
        }
    }
}
