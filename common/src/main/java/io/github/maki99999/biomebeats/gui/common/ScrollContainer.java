package io.github.maki99999.biomebeats.gui.common;

import io.github.maki99999.biomebeats.gui.util.Point;
import io.github.maki99999.biomebeats.gui.util.PointD;
import io.github.maki99999.biomebeats.gui.util.Rect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public abstract class ScrollContainer extends UiElement {
    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("widget/scroller");
    private static final ResourceLocation SCROLLER_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("widget/scroller_background");
    protected static final int SCROLLBAR_WIDTH = 6;
    private static final double SCROLL_RATE = 30d;

    private double scrollAmount;
    private boolean scrolling;

    protected ScrollContainer(Component name, Rect bounds) {
        super(name, bounds);
    }

    protected abstract int getContentHeight();

    @Override
    protected final void render(@NotNull GuiGraphics guiGraphics, Point mousePos, float partialTick) {
        if (!isVisible()) {
            return;
        }
        renderBackground(guiGraphics);

        guiGraphics.enableScissor(getX(), getY() + 1, getX() + getWidth(), getY() - 1 + getHeight());
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(getX(), getY() - scrollAmount, 0);

        renderContent(guiGraphics, mousePos.translate(-getX(), -getY() + (int) scrollAmount), partialTick);

        guiGraphics.pose().popPose();
        guiGraphics.disableScissor();
        renderScrollbar(guiGraphics);
    }

    protected abstract void renderBackground(GuiGraphics guiGraphics);

    protected abstract void renderContent(@NotNull GuiGraphics guiGraphics, Point mousePos, float partialTick);

    protected void renderScrollbar(GuiGraphics guiGraphics) {
        if (isScrollbarVisible()) {
            int i = scrollBarX();
            int j = getScrollerHeight();
            int k = scrollBarY();
            guiGraphics.blitSprite(RenderType::guiTextured, SCROLLER_BACKGROUND_SPRITE, i, getY(), SCROLLBAR_WIDTH, getHeight());
            guiGraphics.blitSprite(RenderType::guiTextured, SCROLLER_SPRITE, i, k, SCROLLBAR_WIDTH, j);
        }

    }

    @Override
    public void renderTooltips(GuiGraphics guiGraphics, Point mousePos, Point absolutePos) {
        if (!isVisible()) {
            return;
        }
        super.renderTooltips(guiGraphics, mousePos, absolutePos);
        renderTooltipsInContent(guiGraphics, mousePos.translate(-getX(), -getY()), absolutePos);
    }

    protected abstract void renderTooltipsInContent(GuiGraphics guiGraphics, Point mousePos, Point absolutePos);

    protected int scrollBarX() {
        return getBounds().x2() - SCROLLBAR_WIDTH;
    }

    protected int scrollBarY() {
        return Math.max(getY(), (int) scrollAmount * (getHeight() - getScrollerHeight()) / getMaxScrollAmount() + getY());
    }

    public int getMaxScrollAmount() {
        return Math.max(0, getContentHeight() - getHeight());
    }

    protected boolean isScrollbarVisible() {
        return getMaxScrollAmount() > 0;
    }

    private int getScrollerHeight() {
        return Mth.clamp((int) ((float) (getHeight() * getHeight()) / (float) getContentHeight()), 32, getHeight() - 8);
    }

    @Override
    public final boolean mouseScrolled(PointD mousePos, double scrollX, double scrollY) {
        if (!isVisible()) {
            return false;
        }
        setScrollAmount(scrollAmount - scrollY * SCROLL_RATE);
        return true;
    }

    @Override
    public final boolean mouseDragged(PointD mousePos, int button, double deltaX, double deltaY) {
        if (!isVisible()) {
            return false;
        }
        if (scrolling) {
            if (mousePos.y() < (double) getY()) {
                setScrollAmount(0.0F);
            } else if (mousePos.y() > (double) getBounds().y2()) {
                setScrollAmount(getMaxScrollAmount());
            } else {
                double d0 = Math.max(1, getMaxScrollAmount());
                int i = getScrollerHeight();
                double d1 = Math.max(1.0F, d0 / (double) (getHeight() - i));
                setScrollAmount(scrollAmount + deltaY * d1);
            }

            return true;
        }
        return false;
    }

    @Override
    protected final boolean mouseClicked(PointD mousePos, int button) {
        if (!isVisible()) {
            return false;
        }

        return updateScrolling(mousePos, button) || super.mouseClicked(mousePos, button);
    }

    @Override
    public boolean mouseReleased(PointD mousePos, int button) {
        if (!isVisible()) {
            return false;
        }
        scrolling = false;
        return true;
    }

    public void setScrollAmount(double scrollAmount) {
        if (!isVisible()) {
            return;
        }
        this.scrollAmount = Mth.clamp(scrollAmount, 0, getMaxScrollAmount());
    }

    public boolean updateScrolling(PointD mousePos, int button) {
        if (!isVisible()) {
            return false;
        }
        scrolling = isScrollbarVisible()
                && button == GLFW.GLFW_MOUSE_BUTTON_1
                && mousePos.x() >= (double) scrollBarX()
                && mousePos.x() <= (double) (scrollBarX() + SCROLLBAR_WIDTH)
                && mousePos.y() >= (double) getY()
                && mousePos.y() < (double) getBounds().y2();
        return scrolling;
    }

    public double getScrollAmount() {
        return scrollAmount;
    }
}
