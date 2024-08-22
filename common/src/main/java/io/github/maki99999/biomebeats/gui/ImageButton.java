package io.github.maki99999.biomebeats.gui;

import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import static io.github.maki99999.biomebeats.util.DrawUtils.drawRect;

public class ImageButton {
    public static final Rect DELETE_UV = new Rect(0, 60, 16, 16);
    public static final Rect EDIT_UV = new Rect(18, 60, 16, 16);
    public static final Rect PLAY_UV = new Rect(36, 60, 16, 16);
    public static final Rect PAUSE_UV = new Rect(72, 60, 16, 16);
    public static final Rect ACCORDION_OPEN_UV = new Rect(0, 80, 16, 16);
    public static final Rect ACCORDION_CLOSE_UV = new Rect(18, 80, 16, 16);
    public static final Rect TAB_LEFT_ACTIVE_UV = new Rect(0, 36, 60, 20);
    public static final Rect TAB_LEFT_INACTIVE_UV = new Rect(62, 36, 60, 20);
    public static final Rect CHECKBOX_CHECKED_UV = new Rect(54, 60, 16, 16);
    public static final Rect CHECKBOX_UNCHECKED_UV = new Rect(54, 16, 16, 16);

    protected static final ResourceLocation RL = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/base.png");

    private final WidgetTooltipHolder tooltip = new WidgetTooltipHolder();
    private final int x;

    private OnPress onPress;
    private int y;
    private Rect uv;

    public ImageButton(int x, int y, Rect uv, OnPress onPress, Tooltip tooltip) {
        this.x = x;
        this.y = y;
        this.uv = uv;
        this.onPress = onPress;
        this.tooltip.set(tooltip);
    }

    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, int mouseYScissorOffset) {
        boolean isHovered = isHovering(guiGraphics, mouseX, mouseY, mouseYScissorOffset);

        drawRect(RL, guiGraphics, new Rect(x, y, uv.w(), uv.h()), uv);
        this.tooltip.refreshTooltipForNextRenderPass(isHovered, false, new ScreenRectangle(x, y, uv.w(), uv.h()));
    }

    protected boolean isHovering(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, int mouseYScissorOffset) {
        return guiGraphics.containsPointInScissor(mouseX, mouseY + mouseYScissorOffset)
                && mouseX >= x
                && mouseY >= y
                && mouseX < x + uv.w()
                && mouseY < y + uv.h();
    }

    public boolean mouseClicked(double x, double y, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            boolean clickedThis = x >= this.x
                    && y >= this.y
                    && x < this.x + uv.w()
                    && y < this.y + uv.h();
            if (clickedThis) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance
                        .forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                onPress.onPress(this);
                return true;
            }
        }
        return false;
    }

    public boolean isActive() {
        return true;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rect getUv() {
        return uv;
    }

    protected void setUv(Rect uv) {
        this.uv = uv;
    }

    public int getWidth() {
        return uv.w();
    }

    public void setOnPress(OnPress onPress) {
        this.onPress = onPress;
    }

    public interface OnPress {
        void onPress(ImageButton button);
    }
}