package io.github.maki99999.biomebeats.gui.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.maki99999.biomebeats.gui.BaseTextureUv;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class DrawUtils {
    public static void drawRect(ResourceLocation resourceLocation, GuiGraphics guiGraphics, Rect pos, Rect uv) {
        Matrix4f lastPose = guiGraphics.pose().last().pose();
        RenderSystem.setShaderTexture(0, resourceLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
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
        Padding padding = getPadding(uv, innerUv);

        // Corners
        drawNineSliceRectCorners(rl, guiGraphics, bounds, uv, innerUv, padding);

        // Sides
        drawRect(rl, guiGraphics, new Rect(bounds.x1(), bounds.y1() + padding.top(), padding.left(),
                bounds.h() - padding.top() - padding.bottom()), Rect.fromCoordinates(uv.x1(), innerUv.y1(), innerUv.x1(),
                innerUv.y2()));
        drawRect(rl, guiGraphics, new Rect(bounds.x2() - padding.right(), bounds.y1() + padding.top(), padding.right(),
                bounds.h() - padding.top() - padding.bottom()), Rect.fromCoordinates(innerUv.x2(), innerUv.y1(), uv.x2(),
                innerUv.y2()));
        drawRect(rl, guiGraphics, new Rect(bounds.x1() + padding.left(), bounds.y1(),
                bounds.w() - padding.left() - padding.right(), padding.top()), Rect.fromCoordinates(innerUv.x1(), uv.y1(),
                innerUv.x2(), innerUv.y1()));
        drawRect(rl, guiGraphics, new Rect(bounds.x1() + padding.left(), bounds.y2() - padding.bottom(),
                bounds.w() - padding.left() - padding.right(), padding.bottom()), Rect.fromCoordinates(innerUv.x1(),
                innerUv.y2(), innerUv.x2(), uv.y2()));

        // Center
        drawRect(rl, guiGraphics, new Rect(bounds.x1() + padding.left(), bounds.y1() + padding.top(),
                        bounds.w() - padding.left() - padding.right(), bounds.h() - padding.top() - padding.bottom()),
                Rect.fromCoordinates(innerUv.x1(), innerUv.y1(), innerUv.x2(), innerUv.y2()));
    }

    private static void drawNineSliceRectCorners(ResourceLocation rl, GuiGraphics guiGraphics, Rect bounds, Rect uv, Rect innerUv, Padding padding) {
        drawRect(rl, guiGraphics, new Rect(bounds.x1(), bounds.y1(), padding.left(), padding.top()),
                Rect.fromCoordinates(uv.x1(), uv.y1(), innerUv.x1(), innerUv.y1()));
        drawRect(rl, guiGraphics, new Rect(bounds.x2() - padding.right(), bounds.y1(), padding.right(), padding.top()),
                Rect.fromCoordinates(innerUv.x2(), uv.y1(), uv.x2(), innerUv.y1()));
        drawRect(rl, guiGraphics, new Rect(bounds.x1(), bounds.y2() - padding.bottom(), padding.left(), padding.bottom()),
                Rect.fromCoordinates(uv.x1(), innerUv.y2(), innerUv.x1(), uv.y2()));
        drawRect(rl, guiGraphics, new Rect(bounds.x2() - padding.right(), bounds.y2() - padding.bottom(), padding.right(),
                padding.bottom()), Rect.fromCoordinates(innerUv.x2(), innerUv.y2(), uv.x2(), uv.y2()));
    }

    public static void drawTiledNineSliceRect(ResourceLocation rl, GuiGraphics guiGraphics, Rect bounds, Rect uv, Rect innerUv) {
        Padding padding = getPadding(uv, innerUv);

        // Corners
        drawNineSliceRectCorners(rl, guiGraphics, bounds, uv, innerUv, padding);

        int centerWidth = innerUv.w();
        int centerHeight = innerUv.h();

        int innerW = bounds.w() - padding.left() - padding.right();
        int innerH = bounds.h() - padding.top() - padding.bottom();

        // Sides
        for (int x = 0; x < innerW; x += centerWidth) {
            int tileW = Math.min(centerWidth, innerW - x);
            drawRect(rl, guiGraphics, new Rect(bounds.x1() + padding.left() + x, bounds.y1(), tileW, padding.top()),
                    Rect.fromCoordinates(innerUv.x1(), uv.y1(), innerUv.x1() + tileW, innerUv.y1()));
            drawRect(rl, guiGraphics, new Rect(bounds.x1() + padding.left() + x, bounds.y2() - padding.bottom(), tileW, padding.bottom()),
                    Rect.fromCoordinates(innerUv.x1(), innerUv.y2(), innerUv.x1() + tileW, uv.y2()));
        }

        for (int y = 0; y < innerH; y += centerHeight) {
            int tileH = Math.min(centerHeight, innerH - y);
            drawRect(rl, guiGraphics, new Rect(bounds.x1(), bounds.y1() + padding.top() + y, padding.left(), tileH),
                    Rect.fromCoordinates(uv.x1(), innerUv.y1(), innerUv.x1(), innerUv.y1() + tileH));
            drawRect(rl, guiGraphics, new Rect(bounds.x2() - padding.right(), bounds.y1() + padding.top() + y, padding.right(), tileH),
                    Rect.fromCoordinates(innerUv.x2(), innerUv.y1(), uv.x2(), innerUv.y1() + tileH));
        }

        // Center
        for (int x = 0; x < innerW; x += centerWidth) {
            int tileW = Math.min(centerWidth, innerW - x);
            for (int y = 0; y < innerH; y += centerHeight) {
                int tileH = Math.min(centerHeight, innerH - y);
                drawRect(rl, guiGraphics, new Rect(bounds.x1() + padding.left() + x, bounds.y1() + padding.top() + y, tileW, tileH),
                        Rect.fromCoordinates(innerUv.x1(), innerUv.y1(), innerUv.x1() + tileW, innerUv.y1() + tileH));
            }
        }
    }

    private static @NotNull Padding getPadding(Rect uv, Rect innerUv) {
        int left = innerUv.x1() - uv.x1();
        int right = uv.x2() - innerUv.x2();
        int top = innerUv.y1() - uv.y1();
        int bottom = uv.y2() - innerUv.y2();
        return new Padding(left, right, top, bottom);
    }

    public static void drawContainer(GuiGraphics guiGraphics, Rect bounds) {
        drawNineSliceRect(BaseTextureUv.RL, guiGraphics, bounds, BaseTextureUv.CONTAINER_UV,
                BaseTextureUv.CONTAINER_INNER_UV);
    }

    public static void drawScrollingString(GuiGraphics guiGraphics, Font font, Component text, Rect bounds, int color) {
        drawScrollingString(guiGraphics, font, text, bounds, color, false);
    }

    public static void drawScrollingString(GuiGraphics guiGraphics, Font font, Component text, Rect bounds, int color,
                                           boolean centered) {
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
            enableAdjustedScissor(guiGraphics, bounds.x1(), bounds.y1(), bounds.x2() + 1, bounds.y2());
            guiGraphics.drawString(font, text, bounds.x1() - (int) scrollOffset, textPosY, color);
            guiGraphics.disableScissor();
        } else if (centered) {
            guiGraphics.drawString(font, text, bounds.x1() + bounds.w() / 2 - font.width(text) / 2, textPosY, color);
        } else {
            guiGraphics.drawString(font, text, bounds.x1(), textPosY, color);
        }
    }

    public static void enableAdjustedScissor(GuiGraphics guiGraphics, int minX, int minY, int maxX, int maxY) {
        Matrix4f pose = guiGraphics.pose().last().pose();
        float dx = pose.m30();
        float dy = pose.m31();
        guiGraphics.enableScissor((int) (minX + dx), (int) (minY + dy),
                                  (int) (maxX + dx), (int) (maxY + dy));
    }

    private record Padding(int left, int right, int top, int bottom) {}
}
