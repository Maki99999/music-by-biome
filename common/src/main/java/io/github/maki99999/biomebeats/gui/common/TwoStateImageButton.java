package io.github.maki99999.biomebeats.gui.common;

import io.github.maki99999.biomebeats.gui.util.BiomeBeatsColor;
import io.github.maki99999.biomebeats.gui.util.Point;
import io.github.maki99999.biomebeats.gui.util.PointD;
import io.github.maki99999.biomebeats.gui.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static io.github.maki99999.biomebeats.gui.util.DrawUtils.drawScrollingString;

public class TwoStateImageButton extends ImageButton {
    private final ImageButton positiveButton;
    private final ImageButton negativeButton;
    private final OnValueChange onValueChange;
    private final boolean nameVisible;
    private boolean state = false;

    public TwoStateImageButton(Component name, Component tooltip, int x, int y, ImageButton positiveButton, ImageButton negativeButton,
                               OnValueChange onValueChange, boolean nameVisible) {
        super(name, tooltip, x, y, positiveButton.getUv(), null);
        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
        this.onValueChange = onValueChange;
        this.nameVisible = nameVisible;
        this.setOnPress(this::onValueChange);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, Point mousePos, float deltaTime) {
        if (state) {
            positiveButton.render(guiGraphics, mousePos, deltaTime);
        } else {
            negativeButton.render(guiGraphics, mousePos, deltaTime);
        }

        if (nameVisible) {
            drawScrollingString(guiGraphics, Minecraft.getInstance().font, getName(), new Rect(getX() + 8, getY(),
                    getUv().w() - 12, getUv().h()), BiomeBeatsColor.WHITE.getHex());
        }
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        positiveButton.setY(y);
        negativeButton.setY(y);
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        positiveButton.setX(x);
        negativeButton.setX(x);
    }

    private void onValueChange(Button btn) {
        state = !state;
        onValueChange.onValueChange((TwoStateImageButton) btn, state);
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void toggle() {
        state = !state;
    }

    @Override
    public void renderTooltips(GuiGraphics guiGraphics, Point mousePos, Point absolutePos) {
        super.renderTooltips(guiGraphics, mousePos, absolutePos);

        if (state) {
            positiveButton.renderTooltips(guiGraphics, mousePos, absolutePos);
        } else {
            negativeButton.renderTooltips(guiGraphics, mousePos, absolutePos);
        }
    }

    @Override
    public boolean mouseClicked(PointD mousePos, int button) {
        if (state) {
            if (positiveButton.mouseClicked(mousePos, button)) {
                return true;
            }
        } else {
            if (negativeButton.mouseClicked(mousePos, button)) {
                return true;
            }
        }

        return super.mouseClicked(mousePos, button);
    }

    public interface OnValueChange {
        void onValueChange(TwoStateImageButton btn, boolean newValue);
    }
}
