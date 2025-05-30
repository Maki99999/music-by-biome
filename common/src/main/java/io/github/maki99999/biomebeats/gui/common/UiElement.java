package io.github.maki99999.biomebeats.gui.common;

import io.github.maki99999.biomebeats.gui.util.Point;
import io.github.maki99999.biomebeats.gui.util.PointD;
import io.github.maki99999.biomebeats.gui.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public abstract class UiElement {
    private final Component name;
    private final Component tooltip;
    private final List<UiElement> children = new ArrayList<>();
    private final Minecraft minecraft;

    private boolean visible = true;
    private UiElement focusedElement = null;
    private UiElement draggingElement = null;
    private int x;
    private int y;
    private int width;
    private int height;

    protected UiElement(Component name, Component tooltip, Rect bounds) {
        this.name = name;
        this.tooltip = tooltip;
        this.minecraft = Minecraft.getInstance();

        if (bounds != null) {
            x = bounds.x();
            y = bounds.y();
            width = bounds.w();
            height = bounds.h();
        }
    }

    protected UiElement(Component name) {
        this(name, null, null);
    }

    public UiElement(Component name, Component tooltip) {
        this(name, tooltip, null);
    }

    public UiElement(Component name, Rect bounds) {
        this(name, null, bounds);
    }

    protected final void initAll() {
        init();

        for (UiElement child : children) {
            child.initAll();
        }
    }

    protected void init() {}

    protected final void onCloseAll() {
        for (UiElement child : children) {
            child.onCloseAll();
        }

        onClose();
    }

    protected void onClose() {}

    public final void renderAll(GuiGraphics guiGraphics, Point mousePos, float deltaTime) {
        if (!visible) {
            return;
        }

        render(guiGraphics, mousePos, deltaTime);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0);

        for (UiElement child : children) {
            child.renderAll(guiGraphics, mousePos.translate(-x, -y), deltaTime);
        }

        guiGraphics.pose().popPose();
    }

    protected abstract void render(GuiGraphics guiGraphics, Point mousePos, float deltaTime);

    public void renderTooltips(GuiGraphics guiGraphics, Point mousePos, Point absolutePos) {
        if (!visible) {
            return;
        }

        if (tooltip != null && isMouseOver(mousePos)) {
            guiGraphics.renderTooltip(minecraft.font, tooltip, absolutePos.x(), absolutePos.y());
        }

        for (UiElement child : children) {
            child.renderTooltips(guiGraphics, mousePos.translate(-x, -y), absolutePos);
        }
    }

    public boolean mouseClickedAll(PointD mousePos, int button) {
        for (UiElement child : getChildren()) {
            if (child.isMouseOver(mousePos.translate(-x, -y).toIntPoint())
                    && child.mouseClickedAll(mousePos.translate(-x, -y), button)) {
                setFocusedElement(child);
                setDraggingElement(child);
                return true;
            }
        }

        if (isMouseOver(mousePos) && mouseClicked(mousePos, button)) {
            setFocusedElement(this);
            setDraggingElement(this);
            return true;
        }

        setFocusedElement(null);
        setDraggingElement(null);
        return false;
    }

    protected void setDraggingElement(UiElement draggingElement) {
        this.draggingElement = draggingElement;
    }

    protected final boolean mouseReleasedAll(PointD mousePos, int button) {
        if (draggingElement == this) {
            setDraggingElement(null);
            return mouseReleased(mousePos, button);
        }

        if (draggingElement != null) {
            boolean result = draggingElement.mouseReleasedAll(mousePos, button);
            setDraggingElement(null);
            if (result) {
                return true;
            }
        }

        for (UiElement child : children) {
            if (child.isMouseOver(mousePos) && child.mouseReleasedAll(mousePos, button)) {
                return true;
            }
        }
        return mouseReleased(mousePos, button);
    }

    protected final boolean mouseDraggedAll(PointD mousePos, int button, double deltaX, double deltaY) {
        if (draggingElement != null) {
            return draggingElement == this
                    ? mouseDragged(mousePos, button, deltaX, deltaY)
                    : draggingElement.mouseDraggedAll(mousePos, button, deltaX, deltaY);
        }
        return false;
    }

    protected final boolean mouseScrolledAll(PointD mousePos, double scrollX, double scrollY) {
        for (UiElement child : children) {
            if (child.isMouseOver(mousePos) && child.mouseScrolledAll(mousePos, scrollX, scrollY)) {
                return true;
            }
        }
        return mouseScrolled(mousePos, scrollX, scrollY);
    }

    protected final boolean keyPressedAll(int keyCode, int scanCode, int modifiers) {
        if (focusedElement == null) {
            return false;
        }

        return focusedElement == this
                ? keyPressed(keyCode, scanCode, modifiers)
                : focusedElement.keyPressedAll(keyCode, scanCode, modifiers);
    }

    protected final boolean keyReleasedAll(int keyCode, int scanCode, int modifiers) {
        if (focusedElement == null) {
            return false;
        }

        return focusedElement == this
                ? keyReleased(keyCode, scanCode, modifiers)
                : focusedElement.keyReleasedAll(keyCode, scanCode, modifiers);
    }

    protected final boolean charTypedAll(char codePoint, int modifiers) {
        if (focusedElement == null) {
            return false;
        }

        return focusedElement == this
                ? charTyped(codePoint, modifiers)
                : focusedElement.charTypedAll(codePoint, modifiers);
    }

    protected boolean mouseClicked(PointD mousePos, int button) {
        return getBounds().contains(mousePos);
    }

    protected boolean mouseReleased(PointD mousePos, int button) {
        return getBounds().contains(mousePos);
    }

    protected boolean mouseDragged(PointD mousePos, int button, double deltaX, double deltaY) {
        return getBounds().contains(mousePos);
    }

    protected boolean mouseScrolled(PointD mousePos, double scrollX, double scrollY) {
        return getBounds().contains(mousePos);
    }

    protected boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    protected boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    protected boolean charTyped(char codePoint, int modifiers) {
        return false;
    }

    protected final boolean isMouseOver(PointD mousePosition) {
        return getBounds().containsD(mousePosition.x(), mousePosition.y());
    }

    public final boolean isMouseOver(Point mousePosition) {
        return getBounds().contains(mousePosition.x(), mousePosition.y());
    }

    protected final void setFocusedElement(UiElement element) {
        if (focusedElement == element) {
            return;
        }

        if (focusedElement != null) {
            focusedElement.onFocusLost();
            focusedElement.clearFocus();
        }

        focusedElement = element;

        if (element != null) {
            element.onFocusGained();
        }
    }

    private void clearFocus() {
        if (focusedElement == null) {
            return;
        }

        if (focusedElement != this) {
            focusedElement.clearFocus();
            focusedElement.onFocusLost();
        }
        focusedElement = null;
    }

    protected final boolean isFocused() {
        return focusedElement == this;
    }

    protected void onFocusGained() {
        // Default: do nothing
    }

    protected void onFocusLost() {
        // Default: do nothing
    }

    protected final <T extends UiElement> T addChild(T element) {
        children.add(element);
        return element;
    }

    protected final void removeChild(UiElement element) {
        element.onCloseAll();
        children.remove(element);
    }

    public final List<UiElement> getChildren() {
        return children.stream().toList();
    }

    public final void setVisible(boolean visible) {
        this.visible = visible;
    }

    public final boolean isVisible() {
        return visible;
    }

    protected final void tick() {
        for (UiElement child : children) {
            child.tick();
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    protected int getY() {
        return y;
    }

    protected void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    protected void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Rect getBounds() {
        return new Rect(x, y, width, height);
    }

    protected void setBounds(Rect bounds) {
        setX(bounds.x());
        setY(bounds.y());
        setWidth(bounds.w());
        setHeight(bounds.h());
    }

    protected Minecraft getMinecraft() {
        return minecraft;
    }

    protected Component getName() {
        return name;
    }

    public void clearChildren() {
        children.forEach(UiElement::onCloseAll);
        children.clear();
    }
}
