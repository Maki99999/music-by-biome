package io.github.maki99999.biomebeats.gui;

import io.github.maki99999.biomebeats.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

public abstract class Button {
    private final SimpleTooltipHolder tooltip = new SimpleTooltipHolder();

    private TextButton.OnPress onPress;
    private Rect bounds;

    public Button(Rect bounds, TextButton.OnPress onPress, Tooltip tooltip) {
        this.onPress = onPress;
        this.bounds = bounds;
        this.tooltip.set(tooltip);
    }

    protected void render(Rect scissorBounds, int mouseX, int mouseY, int mouseYScissorOffset) {
        this.tooltip.refreshTooltipForNextRenderPass(isHovering(scissorBounds, mouseX, mouseY, mouseYScissorOffset), false, getBounds());
    }

    protected boolean isHovering(Rect scissorBounds, int mouseX, int mouseY, int mouseYScissorOffset) {
        return scissorBounds.contains(mouseX, mouseY + mouseYScissorOffset)
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

    public boolean mouseClicked(Rect scissorBounds, int mouseYScissorOffset, double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && isHovering(scissorBounds, Mth.ceil(mouseX), (int) mouseY, mouseYScissorOffset) && bounds.contains(Mth.ceil(mouseX), (int) mouseY)) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            onPress.onPress(this);
            return true;
        }
        return false;
    }

    public interface OnPress {
        void onPress(Button button);
    }
}
