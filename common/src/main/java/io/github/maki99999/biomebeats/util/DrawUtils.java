package io.github.maki99999.biomebeats.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class DrawUtils {
    public static void drawRect(ResourceLocation resourceLocation, GuiGraphics guiGraphics,
                                int x1, int x2, int y1, int y2, int u1, int u2, int v1, int v2) {
        drawRect(resourceLocation, guiGraphics, Rect.fromCoordinates(x1, y1, x2, y2),
                Rect.fromCoordinates(u1, v1, u2, v2));
    }

    public static void drawRect(ResourceLocation resourceLocation, GuiGraphics guiGraphics, Rect pos, Rect uv) {
        Matrix4f lastPose = guiGraphics.pose().last().pose();
        RenderSystem.setShaderTexture(0, resourceLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(lastPose, pos.x1(), pos.y1(), 0f).uv(uv.x1() / 256f, uv.y1() / 256f).endVertex();
        bufferBuilder.vertex(lastPose, pos.x1(), pos.y2(), 0f).uv(uv.x1() / 256f, uv.y2() / 256f).endVertex();
        bufferBuilder.vertex(lastPose, pos.x2(), pos.y2(), 0f).uv(uv.x2() / 256f, uv.y2() / 256f).endVertex();
        bufferBuilder.vertex(lastPose, pos.x2(), pos.y1(), 0f).uv(uv.x2() / 256f, uv.y1() / 256f).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());
    }

    public static void drawScrollingString(GuiGraphics guiGraphics, Font font, Component text,
                                           Rect bounds, int yScissorOffset, int color) {
        int textWidth = font.width(text);
        int textPosY = (bounds.y1() + bounds.y2() - 9) / 2 + 1;
        int availableWidth = bounds.x2() - bounds.x1();
        if (textWidth > availableWidth) {
            int overflowWidth = textWidth - availableWidth;
            double currentTime = Util.getMillis() / 1000d;
            double scrollSpeed = Math.max(overflowWidth * 0.5d, 3d);
            double scrollFactor = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * currentTime / scrollSpeed)) / 2.0
                    + 0.5;
            double scrollOffset = Mth.lerp(scrollFactor, 0.0, overflowWidth);
            guiGraphics.enableScissor(bounds.x1(), bounds.y1() + yScissorOffset, bounds.x2() + 1,
                    bounds.y2() + yScissorOffset);
            guiGraphics.drawString(font, text, bounds.x1() - (int) scrollOffset, textPosY, color);
            guiGraphics.disableScissor();
        } else {
            guiGraphics.drawString(font, text, bounds.x1(), textPosY, color);
        }
    }
}
