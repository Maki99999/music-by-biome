package io.github.maki99999.biomebeats.gui.conditionlist;

import io.github.maki99999.biomebeats.condition.CombinedCondition;
import io.github.maki99999.biomebeats.condition.Condition;
import io.github.maki99999.biomebeats.gui.common.ScrollContainer;
import io.github.maki99999.biomebeats.gui.common.UiElement;
import io.github.maki99999.biomebeats.gui.util.BiomeBeatsColor;
import io.github.maki99999.biomebeats.gui.util.Point;
import io.github.maki99999.biomebeats.gui.util.PointD;
import io.github.maki99999.biomebeats.gui.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class ConditionList extends ScrollContainer {
    private static final int CHILDREN_HEIGHT = 16;
    private static final int CHILDREN_SPACING = 4;

    private final OnSelected onSelected;
    final OnEditPress onEditPress;
    private final Minecraft minecraft;

    private ArrayList<ConditionListEntry> entries = new ArrayList<>();
    @Nullable
    private ConditionListEntry selectedChild = null;

    public ConditionList(Minecraft minecraft, Rect bounds, Component message, OnSelected onSelected,
                         OnEditPress onEditPress) {
        super(message, bounds);
        this.onSelected = onSelected;
        this.onEditPress = onEditPress;
        this.minecraft = minecraft;
    }

    @Override
    protected int getContentHeight() {
        return contentHeight(entries.size());
    }

    private int contentHeight(int childrenCount) {
        return Math.max(childrenCount * (CHILDREN_HEIGHT + CHILDREN_SPACING), 0);
    }

    @Override
    protected void renderBackground(GuiGraphics guiGraphics) {
        guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), BiomeBeatsColor.DARK_GREY.getHex());
    }

    @Override
    protected void renderContent(@NotNull GuiGraphics guiGraphics, Point mousePos, float partialTicks) {
        if (isScrollbarVisible()) {
            guiGraphics.fill(getX() + getWidth(), getY(), getX() + getWidth(), getY() + getHeight(),
                    BiomeBeatsColor.DARK_GREY.getHex());
        }
        for (ConditionListEntry entry : entries) {
            entry.setSelected(entry == selectedChild);
            entry.renderAll(guiGraphics, mousePos, partialTicks);
        }
    }

    public void setConditions(Collection<? extends Condition> conditions, Condition currentCondition) {
        int childrenWidth = contentHeight(conditions.size()) > getHeight() ? getWidth() - SCROLLBAR_WIDTH - 2 : getWidth() - 2;

        entries = new ArrayList<>();
        for (Condition condition : conditions) {
            ConditionListEntry entry = new ConditionListEntry(
                    this,
                    minecraft,
                    new Rect(1, 2 + entries.size() * (CHILDREN_HEIGHT + CHILDREN_SPACING), childrenWidth, CHILDREN_HEIGHT),
                    condition
            );
            entries.add(entry);
        }
        selectedChild = currentCondition == null
                ? null
                : entries.stream().filter(c -> c.getCondition() == currentCondition).findAny().orElse(null);
        setScrollAmount(0);
    }

    @Override
    public boolean mouseClickedAll(PointD mousePos, int button) {
        for (UiElement child : getChildren()) {
            if (child.isMouseOver(mousePos.translate(-getX(), -getY()).toIntPoint())
                    && child.mouseClickedAll(mousePos.translate(-getX(), -getY()), button)) {
                setFocusedElement(child);
                setDraggingElement(child);
                return true;
            }
        }

        for (ConditionListEntry child : entries) {
            if (child.isMouseOver(mousePos.translate(-getX(), -getY()).toIntPoint())
                    && child.mouseClickedAll(mousePos.translate(-getX(), -getY()), button)) {
                selectedChild = child;
                onSelected.onSelected(child.getCondition());
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

    @Override
    public void renderTooltipsInContent(GuiGraphics guiGraphics, Point mousePos, Point absolutePos) {
        for (ConditionListEntry child : entries) {
            child.renderTooltips(guiGraphics, mousePos, absolutePos);
        }
    }

    public interface OnSelected {
        void onSelected(Condition condition);
    }

    public interface OnEditPress {
        void onEditPress(CombinedCondition condition);
    }
}
