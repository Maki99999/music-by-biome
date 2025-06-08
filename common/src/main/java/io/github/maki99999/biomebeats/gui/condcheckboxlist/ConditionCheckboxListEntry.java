package io.github.maki99999.biomebeats.gui.condcheckboxlist;

import io.github.maki99999.biomebeats.condition.Condition;
import io.github.maki99999.biomebeats.gui.BaseTextureUv;
import io.github.maki99999.biomebeats.gui.common.*;
import io.github.maki99999.biomebeats.gui.util.BiomeBeatsColor;
import io.github.maki99999.biomebeats.gui.util.Point;
import io.github.maki99999.biomebeats.gui.util.Rect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static io.github.maki99999.biomebeats.gui.util.DrawUtils.drawScrollingString;

class ConditionCheckboxListEntry extends UiElement {
    private final TwoStateImageButton1 checkbox;
    @NotNull
    private final Condition condition;

    public ConditionCheckboxListEntry(ConditionCheckboxListEntryGroup entryGroup, Condition condition, Rect bounds) {
        super(Component.literal(condition.getName()), bounds);
        this.condition = condition;

        checkbox = addChild(new TwoStateImageButton1(Component.literal("Checkbox"),
                                          null,
                                          0,
                                          0,
                                          new LayeredImageButton1(Component.literal("Checkbox"),
                                                                  null,
                                                                  0,
                                                                  0,
                                                                  BaseTextureUv.CHECKBOX_CHECKED_UV,
                                                                  null),
                                          new LayeredImageButton1(Component.literal("Checkbox"),
                                                                  null,
                                                                  0,
                                                                  0,
                                                                  BaseTextureUv.BUTTON_BASE_INVERTED_UV,
                                                                  null),
                                          (c, newValue) -> entryGroup.conditionCheckboxList.onConditionToggle.onConditionToggle(
                                                  condition,
                                                  newValue),
                                          false));

    }

    @Override
    protected void render(@NotNull GuiGraphics guiGraphics, Point mousePos, float partialTicks) {
        guiGraphics.fill(getX(),
                         getY(),
                         getX() + getWidth(),
                         getY() + getHeight(),
                         BiomeBeatsColor.LIGHT_GREY.getHex());

        drawScrollingString(guiGraphics,
                            getMinecraft().font,
                            getName(),
                            getTextRect(),
                            0,
                            BiomeBeatsColor.WHITE.getHex());
    }

    private @NotNull Rect getTextRect() {
        return new Rect(getX() + 18, getY(), getWidth() - 22, getHeight());
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
    }

    @Override
    public void setY(int y) {
        super.setY(y);
    }

    public void setCheckedState(boolean newValue) {
        checkbox.setState(newValue);
    }

    public @NotNull Condition getCondition() {
        return condition;
    }
}
