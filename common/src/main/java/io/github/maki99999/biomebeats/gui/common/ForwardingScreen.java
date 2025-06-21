package io.github.maki99999.biomebeats.gui.common;

import io.github.maki99999.biomebeats.gui.util.Point;
import io.github.maki99999.biomebeats.gui.util.PointD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

public class ForwardingScreen<T extends UiElement> extends Screen implements ContainerEventHandler {
    private final T root;

    public ForwardingScreen(T rootElement) {
        super(rootElement.getName());
        this.root = rootElement;
    }

    @Override
    protected void init() {
        root.setWidth(width);
        root.setHeight(height);
        root.clearChildren();
        root.initAll();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float deltaTime) {
        root.renderAll(guiGraphics, new Point(mouseX, mouseY), deltaTime);
        root.renderTooltips(guiGraphics, new Point(mouseX, mouseY), new Point(mouseX, mouseY));
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        return root.mouseClickedAll(new PointD(x, y), button);
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        return root.mouseReleasedAll(new PointD(x, y), button);
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double deltaX, double deltaY) {
        return root.mouseDraggedAll(new PointD(x, y), button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return root.mouseScrolledAll(new PointD(mouseX, mouseY), scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return root.keyPressedAll(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return root.keyReleasedAll(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return root.charTypedAll(codePoint, modifiers);
    }

    @Override
    public void tick() {
        root.tick();
    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        root.setWidth(width);
        root.setHeight(height);
        root.clearChildren();
        root.initAll();
    }

    @Override
    public void onClose() {
        super.onClose();
        root.onCloseAll();
    }
}
