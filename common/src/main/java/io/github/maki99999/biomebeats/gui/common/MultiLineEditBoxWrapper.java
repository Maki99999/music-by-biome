package io.github.maki99999.biomebeats.gui.common;

import io.github.maki99999.biomebeats.gui.util.Point;
import io.github.maki99999.biomebeats.gui.util.PointD;
import io.github.maki99999.biomebeats.gui.util.Rect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.Component;

public class MultiLineEditBoxWrapper extends UiElement {
    private final MultiLineEditBox editBox;

    public MultiLineEditBoxWrapper(Component name, Rect bounds) {
        super(name, bounds);
        this.editBox = new MultiLineEditBox(getMinecraft().font, bounds.x(), bounds.y(), bounds.w(), bounds.h(), name, name);
    }

    @Override
    protected void render(GuiGraphics guiGraphics, Point mousePos, float deltaTime) {
        editBox.render(guiGraphics, mousePos.x(), mousePos.y(), deltaTime);
    }

    @Override
    protected boolean mouseClicked(PointD mousePos, int button) {
        return editBox.mouseClicked(mousePos.x(), mousePos.y(), button);
    }

    @Override
    protected boolean mouseReleased(PointD mousePos, int button) {
        return editBox.mouseReleased(mousePos.x(), mousePos.y(), button);
    }

    @Override
    protected boolean mouseDragged(PointD mousePos, int button, double dx, double dy) {
        return editBox.mouseDragged(mousePos.x(), mousePos.y(), button, dx, dy);
    }

    @Override
    protected boolean mouseScrolled(PointD mousePos, double scrollX, double scrollY) {
        return editBox.mouseScrolled(mousePos.x(), mousePos.y(), scrollX, scrollY);
    }

    @Override
    protected boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return editBox.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return editBox.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    protected boolean charTyped(char codePoint, int modifiers) {
        return editBox.charTyped(codePoint, modifiers);
    }

    @Override
    protected void onFocusGained() {
        editBox.setFocused(true);
    }

    @Override
    protected void onFocusLost() {
        editBox.setFocused(false);
    }

    public String getValue() {
        return editBox.getValue();
    }

    public void setValue(String value) {
        editBox.setValue(value);
    }

    public void setCharacterLimit(int limit) {
        editBox.setCharacterLimit(limit);
    }

    @Override
    protected void setWidth(int width) {
        super.setWidth(width);
        editBox.setWidth(width);
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        editBox.setHeight(height);
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        editBox.setX(x);
    }

    @Override
    protected void setY(int y) {
        super.setY(y);
        editBox.setY(y);
    }
}
