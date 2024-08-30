package io.github.maki99999.biomebeats.gui;

import io.github.maki99999.biomebeats.condition.Condition;
import io.github.maki99999.biomebeats.util.BiomeBeatsColor;
import io.github.maki99999.biomebeats.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static io.github.maki99999.biomebeats.util.DrawUtils.drawScrollingString;

public class ConditionList extends AbstractScrollWidget implements Renderable, ContainerEventHandler {
    private static final int SCROLL_BAR_WIDTH = 8;
    private static final int CHILDREN_HEIGHT = 17;
    private static final int CHILDREN_SPACING = 4;

    private final OnSelected onSelected;
    private final Minecraft minecraft;
    private final int widthExclScrollBar;

    private List<Entry> children = new ArrayList<>();
    @Nullable
    private ConditionList.Entry selectedChild = null;
    @Nullable
    private GuiEventListener focusedChild = null;
    private boolean isDragging;

    public ConditionList(Minecraft minecraft, Rect bounds, Component message, OnSelected onSelected) {
        super(bounds.x(), bounds.y(), bounds.w() - SCROLL_BAR_WIDTH, bounds.h(), message);
        this.onSelected = onSelected;
        this.minecraft = minecraft;

        widthExclScrollBar = bounds.w();
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean clickedInArea = super.mouseClicked(x, y, button);
        boolean clickedChild = false;

        for (Entry child : children) {
            if (child.mouseClicked(x, y + scrollAmount(), button)) {
                selectedChild = child;
                onSelected.onSelected(child.getCondition());
                clickedChild = true;
            }
        }
        return clickedInArea || clickedChild;
    }

    @Override
    protected void renderBackground(@NotNull GuiGraphics guiGraphics) {
        guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), BiomeBeatsColor.DARK_GREY.getHex());
        if (scrollbarVisible()) {
            guiGraphics.fill(getX() + getWidth(), getY(), getX() + getWidth() + SCROLL_BAR_WIDTH, getY() + getHeight(),
                    BiomeBeatsColor.DARK_GREY.getHex());
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {}

    @Override
    protected int getInnerHeight() {
        return children.size() * (CHILDREN_HEIGHT + CHILDREN_SPACING) - 4;
    }

    @Override
    protected double scrollRate() {
        return 30d;
    }

    @Override
    protected void renderContents(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        for (int i = 0; i < children.size(); i++) {
            Entry entry = children.get(i);
            entry.setSelected(entry == selectedChild);
            entry.setY(getY() + CHILDREN_SPACING + i * (CHILDREN_HEIGHT + CHILDREN_SPACING));
            entry.render(guiGraphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return children;
    }

    @Override
    public boolean isDragging() {
        return isDragging;
    }

    @Override
    public void setDragging(boolean dragging) {
        isDragging = dragging;
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return focusedChild;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener guiEventListener) {
        if (this.focusedChild != null) {
            this.focusedChild.setFocused(false);
        }

        if (guiEventListener != null) {
            guiEventListener.setFocused(true);
        }

        this.focusedChild = guiEventListener;
    }

    private boolean isScrollbarVisible(int childrenCount) {
        return (childrenCount * (CHILDREN_HEIGHT + CHILDREN_SPACING) - 4) > this.getHeight();
    }

    public void setConditions(Collection<? extends Condition> conditions, Condition currentCondition) {
        width = isScrollbarVisible(conditions.size()) ? widthExclScrollBar - SCROLL_BAR_WIDTH : widthExclScrollBar;

        children = new ArrayList<>();
        for (Condition condition : conditions) {
            var entry = new Entry(minecraft, getX(), 0, width, CHILDREN_HEIGHT, condition);
            children.add(entry);
        }
        selectedChild = currentCondition == null
                ? null
                : children.stream().filter(c -> c.condition == currentCondition).findAny().orElse(null);
        setScrollAmount(0);
    }

    public interface OnSelected {
        void onSelected(Condition condition);
    }

    private class Entry extends AbstractWidget {
        private final Minecraft minecraft;
        private final Condition condition;

        private boolean selected = false;

        public Entry(Minecraft minecraft, int x, int y, int w, int h, Condition condition) {
            super(x, y, w, h, Component.literal(condition.getName()));
            this.minecraft = minecraft;
            this.condition = condition;
        }

        public Condition getCondition() {
            return condition;
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
            if (selected) {
                guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(),
                        BiomeBeatsColor.WHITE.getHex());
                guiGraphics.fill(getX() + 2, getY() + 1, getX() - 2 + getWidth(), getY() - 1 + getHeight(),
                        BiomeBeatsColor.LIGHT_GREY.getHex());
            } else {
                guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(),
                        BiomeBeatsColor.LIGHT_GREY.getHex());
            }
            drawScrollingString(guiGraphics, minecraft.font, getMessage(),
                    Rect.fromCoordinates(getX() + 4, getY(), getX() + getWidth() - 4, getY() + getHeight()),
                    (int) -ConditionList.this.scrollAmount(), BiomeBeatsColor.WHITE.getHex());
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {}

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }
}
