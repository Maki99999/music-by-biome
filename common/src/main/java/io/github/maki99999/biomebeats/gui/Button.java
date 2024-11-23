package io.github.maki99999.biomebeats.gui;

import io.github.maki99999.biomebeats.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public abstract class Button {
    private final WidgetTooltipHolder tooltip = new WidgetTooltipHolder();

    private TextButton.OnPress onPress;
    private Rect bounds;

    public Button(Rect bounds, TextButton.OnPress onPress, Tooltip tooltip) {
        this.onPress = onPress;
        this.bounds = bounds;
        this.tooltip.set(tooltip);
    }

    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, int mouseYScissorOffset) {
        this.tooltip.refreshTooltipForNextRenderPass(isHovering(guiGraphics, mouseX, mouseY, mouseYScissorOffset),
                false, new ScreenRectangle(bounds.x(), bounds.y(), bounds.w(), bounds.h()));
    }

    protected boolean isHovering(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, int mouseYScissorOffset) {
        return guiGraphics.containsPointInScissor(mouseX, mouseY + mouseYScissorOffset)
                && bounds.contains(mouseX, mouseY);
    }

    public int getX() {
        return bounds.x();
    }

    public void setY(int y) {
        bounds = new Rect(bounds.x(), y, bounds.w(), bounds.h());
    }

    public int getY() {
        return bounds.y();
    }

    public void setX(int x) {
        bounds = new Rect(x, bounds.y(), bounds.w(), bounds.h());
    }

    public int getWidth() {
        return bounds.w();
    }

    public Rect getBounds() {
        return bounds;
    }

    public boolean isActive() {
        return true;
    }

    public void setOnPress(TextButton.OnPress onPress) {
        this.onPress = onPress;
    }

    public boolean mouseClicked(double x, double y, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            if (bounds.contains(Mth.ceil(x), (int) y)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,
                        1.0F));
                onPress.onPress(this);
                return true;
            }
        }
        return false;
    }

    public interface OnPress {
        void onPress(Button button);
    }
}
