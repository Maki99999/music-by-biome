package io.github.maki99999.biomebeats.gui.common;

import io.github.maki99999.biomebeats.gui.BaseTextureUv;
import io.github.maki99999.biomebeats.gui.util.BiomeBeatsColor;
import io.github.maki99999.biomebeats.gui.util.Point;
import io.github.maki99999.biomebeats.gui.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import static io.github.maki99999.biomebeats.gui.BaseTextureUv.RL;
import static io.github.maki99999.biomebeats.gui.util.DrawUtils.drawNineSliceRect;
import static io.github.maki99999.biomebeats.gui.util.DrawUtils.drawScrollingString;

public class TextButton extends Button {
    public TextButton(Component name, Component tooltip, Rect bounds, OnPress onPress) {
        super(name, tooltip, bounds, onPress);
    }

    @Override
    protected void render(GuiGraphics guiGraphics, Point mousePos, float deltaTime) {
        Rect renderingUv = isMouseOver(mousePos) ? BaseTextureUv.BUTTON_BASE_FOCUSED_UV : BaseTextureUv.BUTTON_BASE_UV;

        Rect innerUv = new Rect(renderingUv.x() + 1, renderingUv.y() + 1, renderingUv.w() - 2, renderingUv.h() - 2);
        Rect innerBounds = new Rect(getBounds().x(), getBounds().y(), getBounds().w(), getBounds().h());

        drawNineSliceRect(RL, guiGraphics, getBounds(), renderingUv, innerUv);
        drawScrollingString(guiGraphics, Minecraft.getInstance().font, getName(), innerBounds,
                            BiomeBeatsColor.WHITE.getHex(), true);
    }
}