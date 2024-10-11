package io.github.maki99999.biomebeats.gui;

import io.github.maki99999.biomebeats.util.BiomeBeatsColor;
import io.github.maki99999.biomebeats.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static io.github.maki99999.biomebeats.gui.BaseTextureUv.RL;
import static io.github.maki99999.biomebeats.util.DrawUtils.*;

public class TextButton extends Button {
    private final Component text;

    public TextButton(Rect bounds, Component text, OnPress onPress, Tooltip tooltip) {
        super(bounds, onPress, tooltip);
        this.text = text;
    }

    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, int mouseYScissorOffset) {
        super.render(guiGraphics, mouseX, mouseY, mouseYScissorOffset);

        Rect renderingUv;
        if (isActive()) {
            renderingUv = isHovering(guiGraphics, mouseX, mouseY, mouseYScissorOffset)
                    ? BaseTextureUv.BUTTON_BASE_FOCUSED_UV
                    : BaseTextureUv.BUTTON_BASE_UV;
        } else {
            renderingUv = BaseTextureUv.BUTTON_BASE_DISABLED_UV;
        }
        Rect innerUv = new Rect(renderingUv.x() + 1, renderingUv.y() + 1, renderingUv.w() - 2, renderingUv.h() - 2);
        Rect innerBounds = new Rect(getBounds().x(), getBounds().y(), getBounds().w(), getBounds().h());

        drawNineSliceRect(RL, guiGraphics, getBounds(), renderingUv, innerUv);
        drawScrollingString(guiGraphics, Minecraft.getInstance().font, text, innerBounds, mouseYScissorOffset,
                BiomeBeatsColor.WHITE.getHex(), true);
    }
}