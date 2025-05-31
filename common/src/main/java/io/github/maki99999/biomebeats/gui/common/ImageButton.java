package io.github.maki99999.biomebeats.gui.common;

import io.github.maki99999.biomebeats.gui.util.Rect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import org.jetbrains.annotations.NotNull;

import static io.github.maki99999.biomebeats.gui.BaseTextureUv.RL;
import static io.github.maki99999.biomebeats.gui.util.DrawUtils.drawRect;

//TODO completely replace with other class
public class ImageButton extends Button {
    private final Rect uv;

    public ImageButton(int x, int y, Rect uv, OnPress onPress, Tooltip tooltip) {
        super(new Rect(x, y, uv.w(), uv.h()), onPress, tooltip);
        this.uv = uv;
    }

    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, int mouseYScissorOffset) {
        super.render(guiGraphics, mouseX, mouseY, mouseYScissorOffset);
        drawRect(RL, guiGraphics, getBounds(), uv);
    }

    public Rect getUv() {
        return uv;
    }
}