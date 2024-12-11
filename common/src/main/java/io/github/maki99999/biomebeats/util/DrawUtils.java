package io.github.maki99999.biomebeats.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.maki99999.biomebeats.gui.BaseTextureUv;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class DrawUtils {
    public static void drawRect(ResourceLocation resourceLocation, GuiGraphics guiGraphics, Rect pos, Rect uv) {
        Matrix4f lastPose = guiGraphics.pose().last().pose();
        RenderSystem.setShaderTexture(0, resourceLocation);
        RenderSystem.setShader(CoreShaders.POSITION_TEX);
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(lastPose, pos.x1(), pos.y1(), 0f).setUv(uv.x1() / 256f, uv.y1() / 256f);
        bufferBuilder.addVertex(lastPose, pos.x1(), pos.y2(), 0f).setUv(uv.x1() / 256f, uv.y2() / 256f);
        bufferBuilder.addVertex(lastPose, pos.x2(), pos.y2(), 0f).setUv(uv.x2() / 256f, uv.y2() / 256f);
        bufferBuilder.addVertex(lastPose, pos.x2(), pos.y1(), 0f).setUv(uv.x2() / 256f, uv.y1() / 256f);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }

    public static void drawNineSliceRect(ResourceLocation rl, GuiGraphics guiGraphics, Rect bounds, Rect uv,
                                         Rect innerUv) {
        int leftPadding = innerUv.x1() - uv.x1();
        int rightPadding = uv.x2() - innerUv.x2();
        int topPadding = innerUv.y1() - uv.y1();
        int bottomPadding = uv.y2() - innerUv.y2();

        // Corners
        drawRect(rl, guiGraphics, new Rect(bounds.x1(), bounds.y1(), leftPadding, topPadding),
                Rect.fromCoordinates(uv.x1(), uv.y1(), innerUv.x1(), innerUv.y1()));
        drawRect(rl, guiGraphics, new Rect(bounds.x2() - rightPadding, bounds.y(), rightPadding, topPadding),
                Rect.fromCoordinates(innerUv.x2(), uv.y1(), uv.x2(), innerUv.y1()));
        drawRect(rl, guiGraphics, new Rect(bounds.x1(), bounds.y2() - bottomPadding, leftPadding, bottomPadding),
                Rect.fromCoordinates(uv.x1(), innerUv.y2(), innerUv.x1(), uv.y2()));
        drawRect(rl, guiGraphics, new Rect(bounds.x2() - rightPadding, bounds.y2() - bottomPadding, rightPadding,
                bottomPadding), Rect.fromCoordinates(innerUv.x2(), innerUv.y2(), uv.x2(), uv.y2()));

        // Sides
        drawRect(rl, guiGraphics, new Rect(bounds.x1(), bounds.y1() + topPadding, leftPadding,
                bounds.h() - topPadding - bottomPadding), Rect.fromCoordinates(uv.x1(), innerUv.y1(), innerUv.x1(),
                innerUv.y2()));
        drawRect(rl, guiGraphics, new Rect(bounds.x2() - rightPadding, bounds.y1() + topPadding, rightPadding,
                bounds.h() - topPadding - bottomPadding), Rect.fromCoordinates(innerUv.x2(), innerUv.y1(), uv.x2(),
                innerUv.y2()));
        drawRect(rl, guiGraphics, new Rect(bounds.x1() + leftPadding, bounds.y1(),
                bounds.w() - leftPadding - rightPadding, topPadding), Rect.fromCoordinates(innerUv.x1(), uv.y1(),
                innerUv.x2(), innerUv.y1()));
        drawRect(rl, guiGraphics, new Rect(bounds.x1() + leftPadding, bounds.y2() - bottomPadding,
                bounds.w() - leftPadding - rightPadding, bottomPadding), Rect.fromCoordinates(innerUv.x1(),
                innerUv.y2(), innerUv.x2(), uv.y2()));

        // Center
        drawRect(rl, guiGraphics, new Rect(bounds.x1() + leftPadding, bounds.y1() + topPadding,
                        bounds.w() - leftPadding - rightPadding, bounds.h() - topPadding - bottomPadding),
                Rect.fromCoordinates(innerUv.x1(), innerUv.y1(), innerUv.x2(), innerUv.y2()));
    }

    public static void drawContainer(GuiGraphics guiGraphics, Rect bounds) {
        drawNineSliceRect(BaseTextureUv.RL, guiGraphics, bounds, BaseTextureUv.CONTAINER_UV,
                BaseTextureUv.CONTAINER_INNER_UV);
    }

    public static void drawScrollingString(GuiGraphics guiGraphics, Font font, Component text, Rect bounds,
                                           int yScissorOffset, int color) {
        drawScrollingString(guiGraphics, font, text, bounds, yScissorOffset, color, false);
    }

    public static void drawScrollingString(GuiGraphics guiGraphics, Font font, Component text, Rect bounds,
                                           int yScissorOffset, int color, boolean centered) {
        int textWidth = font.width(text);
        int textPosY = (bounds.y1() + bounds.y2() - 9) / 2 + 1;
        int availableWidth = bounds.x2() - bounds.x1();
        if (textWidth > availableWidth) {
            int overflowWidth = textWidth - availableWidth;
            double currentTime = Util.getMillis() / 1000d;
            double scrollSpeed = Math.max(overflowWidth * 0.5d, 3d);
            double scrollFactor =
                    Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * currentTime / scrollSpeed)) / 2.0 + 0.5;
            double scrollOffset = Mth.lerp(scrollFactor, 0.0, overflowWidth);
            guiGraphics.enableScissor(bounds.x1(), bounds.y1() + yScissorOffset, bounds.x2() + 1,
                    bounds.y2() + yScissorOffset);
            guiGraphics.drawString(font, text, bounds.x1() - (int) scrollOffset, textPosY, color);
            guiGraphics.disableScissor();
        } else if (centered) {
            guiGraphics.drawString(font, text, bounds.x1() + bounds.w() / 2 - font.width(text) / 2, textPosY, color);
        } else {
            guiGraphics.drawString(font, text, bounds.x1(), textPosY, color);
        }
    }
}
