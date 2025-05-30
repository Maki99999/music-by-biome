package io.github.maki99999.biomebeats.gui.common;

import io.github.maki99999.biomebeats.gui.util.BiomeBeatsColor;
import io.github.maki99999.biomebeats.gui.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.maki99999.biomebeats.gui.util.DrawUtils.drawScrollingString;

public class TwoStateImageButton extends ImageButton {
    private final ImageButton positiveButton;
    private final ImageButton negativeButton;
    private final OnValueChange onValueChange;
    private final Component text;
    private boolean state = false;

    public TwoStateImageButton(int x, int y, ImageButton positiveButton, ImageButton negativeButton,
                               OnValueChange onValueChange,
                               @Nullable Tooltip tooltip, @Nullable Component text) {
        super(x, y, positiveButton.getUv(), null, tooltip);
        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
        this.onValueChange = onValueChange;
        this.text = text;
        this.setOnPress(this::onValueChange);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, int mouseYScissorOffset) {
        if (state)
            positiveButton.render(guiGraphics, mouseX, mouseY, mouseYScissorOffset);
        else
            negativeButton.render(guiGraphics, mouseX, mouseY, mouseYScissorOffset);

        if (text != null)
            drawScrollingString(guiGraphics, Minecraft.getInstance().font, text, new Rect(getX() + 8, getY(),
                    getUv().w() - 12, getUv().h()), 0, BiomeBeatsColor.WHITE.getHex());
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

    public interface OnValueChange {
        void onValueChange(TwoStateImageButton btn, boolean newValue);
    }
}
