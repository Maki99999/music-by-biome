package io.github.maki99999.biomebeats.gui.common;

import io.github.maki99999.biomebeats.gui.BaseTextureUv;
import io.github.maki99999.biomebeats.gui.util.Point;
import io.github.maki99999.biomebeats.gui.util.Rect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static io.github.maki99999.biomebeats.gui.util.DrawUtils.drawRect;

public class LayeredImageButton extends ImageButton {
    public LayeredImageButton(Component name, Component tooltip, int x, int y, Rect uv, OnPress onPress) {
        super(name, tooltip, x, y, uv, onPress);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, Point mousePos, float deltaTime) {
        Rect renderingUv = isMouseOver(mousePos)
                ? BaseTextureUv.BUTTON_BASE_FOCUSED_UV
                : BaseTextureUv.BUTTON_BASE_UV;

        drawRect(BaseTextureUv.RL, guiGraphics, new Rect(getX(), getY(), getUv().w(), getUv().h()), renderingUv);
        super.render(guiGraphics, mousePos, deltaTime);
    }
}
