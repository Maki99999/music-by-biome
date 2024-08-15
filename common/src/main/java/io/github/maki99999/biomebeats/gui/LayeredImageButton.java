package io.github.maki99999.biomebeats.gui;

import io.github.maki99999.biomebeats.util.Rect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import org.jetbrains.annotations.NotNull;

import static io.github.maki99999.biomebeats.util.DrawUtils.drawRect;

public class LayeredImageButton extends ImageButton {
    private static final Rect BASE_UV = new Rect(0, 16, 16, 16);
    private static final Rect BASE_FOCUSED_UV = new Rect(18, 16, 16, 16);
    private static final Rect BASE_DISABLED_UV = new Rect(36, 16, 16, 16);

    public LayeredImageButton(int x, int y, Rect uv, OnPress onPress, Tooltip tooltip) {
        super(x, y, uv, onPress, tooltip);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, int mouseYScissorOffset) {
        if (!isActive()) return;

        Rect renderingUv;
        if (isActive()) {
            renderingUv = isHovering(guiGraphics, mouseX, mouseY, mouseYScissorOffset) ? BASE_FOCUSED_UV : BASE_UV;
        } else {
            renderingUv = BASE_DISABLED_UV;
        }

        drawRect(RL, guiGraphics, new Rect(getX(), getY(), getUv().w(), getUv().h()), renderingUv);
        super.render(guiGraphics, mouseX, mouseY, mouseYScissorOffset);
    }
}
