package io.github.maki99999.biomebeats.gui;

import io.github.maki99999.biomebeats.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.MenuTooltipPositioner;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class SimpleTooltipHolder {
    private Tooltip tooltip;
    private boolean wasDisplayed;

    public void set(Tooltip tooltip) {
        this.tooltip = tooltip;
    }

    public void refreshTooltipForNextRenderPass(boolean hovering, boolean focused, Rect bounds) {
        if (this.tooltip == null) {
            this.wasDisplayed = false;
        } else {
            boolean flag = hovering || focused && Minecraft.getInstance().getLastInputType().isKeyboard();
            if (flag != this.wasDisplayed) {
                this.wasDisplayed = flag;
            }

            if (flag) {
                Screen screen = Minecraft.getInstance().screen;
                if (screen != null) {
                    screen.setTooltipForNextRenderPass(this.tooltip, new MenuTooltipPositioner(
                            new AbstractWidget(
                                    bounds == null ? 0 : bounds.x(),
                                    bounds == null ? 0 : bounds.y(),
                                    bounds == null ? Integer.MAX_VALUE : bounds.w(),
                                    bounds == null ? Integer.MAX_VALUE : bounds.h(),
                                    Component.empty()
                            ) {
                                @Override
                                public void renderWidget(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {}

                                @Override
                                protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {}
                            }), focused);
                }
            }
        }
    }
}
