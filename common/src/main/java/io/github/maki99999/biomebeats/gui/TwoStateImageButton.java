package io.github.maki99999.biomebeats.gui;

import io.github.maki99999.biomebeats.util.BiomeBeatsColor;
import io.github.maki99999.biomebeats.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.maki99999.biomebeats.util.DrawUtils.drawScrollingString;

public class TwoStateImageButton extends ImageButton {
    private final Rect positiveStateUv;
    private final Rect negativeStateUv;
    private final OnValueChange onValueChange;
    private final Component text;
    private boolean state = false;

    public TwoStateImageButton(int x, int y, Rect positiveStateUv, Rect negativeStateUv, OnValueChange onValueChange,
                               @Nullable Tooltip tooltip, @Nullable Component text) {
        super(x, y, positiveStateUv, null, tooltip);
        this.positiveStateUv = positiveStateUv;
        this.negativeStateUv = negativeStateUv;
        this.onValueChange = onValueChange;
        this.text = text;
        this.setOnPress(this::onValueChange);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, int mouseYScissorOffset) {
        setUv(state ? positiveStateUv : negativeStateUv);
        super.render(guiGraphics, mouseX, mouseY, mouseYScissorOffset);

        if (text != null)
            drawScrollingString(guiGraphics, Minecraft.getInstance().font, text, new Rect(getX() + 8, getY(),
                    getUv().w() - 12, getUv().h()), 0, BiomeBeatsColor.WHITE.getHex());
    }

    private void onValueChange(ImageButton btn) {
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
