package io.github.maki99999.biomebeats.gui.common;

import io.github.maki99999.biomebeats.gui.util.PointD;
import io.github.maki99999.biomebeats.gui.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public abstract class Button extends UiElement {
    private OnPress onPress;

    public Button(Component name, Component tooltip, Rect bounds, OnPress onPress) {
        super(name, tooltip, bounds);
        this.onPress = onPress;
    }

    protected void setOnPress(OnPress onPress) {
        this.onPress = onPress;
    }

    @Override
    public boolean mouseClicked(PointD mousePos, int button) {
        if (onPress != null && super.mouseClicked(mousePos, button)) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            onPress.onPress(this);
        }
        return false;
    }

    public interface OnPress {
        void onPress(Button button);
    }
}
