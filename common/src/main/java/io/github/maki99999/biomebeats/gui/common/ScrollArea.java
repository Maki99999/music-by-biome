package io.github.maki99999.biomebeats.gui.common;

import io.github.maki99999.biomebeats.gui.util.Rect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public abstract class ScrollArea extends AbstractScrollArea {
    public ScrollArea(Rect bounds, Component message) {
        super(bounds.x(), bounds.y(), bounds.w(), bounds.h(), message);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.visible) {
            renderBackground(guiGraphics);
            guiGraphics.enableScissor(getX(), getY() + 1, getX() + width, getY() - 1 + height);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, -scrollAmount(), 0);
            renderContents(guiGraphics, mouseX, mouseY, partialTick);
            guiGraphics.pose().popPose();
            guiGraphics.disableScissor();
            renderScrollbar(guiGraphics);
        }
    }

    protected abstract void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick);

    protected abstract void renderBackground(GuiGraphics guiGraphics);
}
