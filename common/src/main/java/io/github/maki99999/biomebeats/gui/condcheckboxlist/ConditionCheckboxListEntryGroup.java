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

import java.util.Collection;

import static io.github.maki99999.biomebeats.gui.util.DrawUtils.drawScrollingString;

class ConditionCheckboxListEntryGroup extends TypedUiContainer<ConditionCheckboxListEntry> {
    private static final int CHILDREN_HEIGHT = 16;
    private static final int GROUP_HEADER_HEIGHT = 16;
    private static final int CHILDREN_SPACING = 4;

    final ConditionCheckboxList conditionCheckboxList;

    public ConditionCheckboxListEntryGroup(ConditionCheckboxList conditionCheckboxList,
                                           int x,
                                           int y,
                                           int w,
                                           Component message,
                                           Collection<Condition> conditions,
                                           Component typeName,
                                           boolean isCollapsed) {
        super(message, new Rect(x, y, w, 0));
        this.conditionCheckboxList = conditionCheckboxList;
        TwoStateImageButton1 collapseButton = addChild(
                new TwoStateImageButton1(Component.translatable("menu.biomebeats.expand_collapse"),
                                         Component.translatable("menu.biomebeats.expand_collapse"),
                                         w - 24,
                                         1,
                                         new ImageButton1(Component.translatable("menu.biomebeats.expand_collapse"),
                                                          null,
                                                          w - 24,
                                                          1,
                                                          BaseTextureUv.ACCORDION_OPEN_UV,
                                                          null),
                                         new ImageButton1(Component.translatable("menu.biomebeats.expand_collapse"),
                                                          null,
                                                          w - 24,
                                                          1,
                                                          BaseTextureUv.ACCORDION_CLOSE_UV,
                                                          null),
                                         (btn, newValue) -> conditionCheckboxList.onGroupToggle.onGroupToggle(typeName,
                                                                                                              newValue),
                                         false));
        collapseButton.setState(isCollapsed);

        for (Condition condition : conditions) {
            addTypedChild(new ConditionCheckboxListEntry(this, condition, new Rect(1, 0, w - 2, CHILDREN_HEIGHT)));
        }
        UpdateHeight();
    }

    public void UpdateHeight() {
        int childY = GROUP_HEADER_HEIGHT + CHILDREN_SPACING;
        for (ConditionCheckboxListEntry group : getTypedChildren()) {
            group.setY(childY);
            childY += group.getHeight() + CHILDREN_SPACING;
        }
        setHeight(getTypedChildren().stream().mapToInt(UiElement::getHeight).sum() + (getTypedChildren().size() + 1) * CHILDREN_SPACING + GROUP_HEADER_HEIGHT);
        conditionCheckboxList.updateY();
    }

    @Override
    protected void render(@NotNull GuiGraphics guiGraphics, Point mousePos, float partialTicks) {
        drawScrollingString(guiGraphics,
                            getMinecraft().font,
                            getName(),
                            new Rect(getX() + 16, getY() + 4, getWidth() - 48, 8),
                            0,
                            BiomeBeatsColor.WHITE.getHex());
    }

    @Override
    public void setY(int y) {
        super.setY(y);
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);

        getTypedChildren().forEach(entry -> entry.setWidth(width - 2));
    }

    public void setCheckedConditions(Collection<? extends Condition> conditions) {
        getTypedChildren().forEach(child -> child.setCheckedState(conditions.contains(child.getCondition())));
    }

}
