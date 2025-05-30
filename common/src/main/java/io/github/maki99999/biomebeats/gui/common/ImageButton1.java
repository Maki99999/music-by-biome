package io.github.maki99999.biomebeats.gui.common;

import io.github.maki99999.biomebeats.gui.util.Point;
import io.github.maki99999.biomebeats.gui.util.Rect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static io.github.maki99999.biomebeats.gui.BaseTextureUv.RL;
import static io.github.maki99999.biomebeats.gui.util.DrawUtils.drawRect;

public class ImageButton1 extends Button1 {
    private final Rect uv;

    public ImageButton1(Component name, Component tooltip, int x, int y, Rect uv, OnPress onPress) {
        super(name, tooltip, new Rect(x, y, uv.w(), uv.h()), onPress);
        this.uv = uv;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, Point mousePos, float deltaTime) {
        drawRect(RL, guiGraphics, getBounds(), uv);
    }

    public Rect getUv() {
        return uv;
    }
}
