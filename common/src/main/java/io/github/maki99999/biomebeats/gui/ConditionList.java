package io.github.maki99999.biomebeats.gui;

import io.github.maki99999.biomebeats.condition.CombinedCondition;
import io.github.maki99999.biomebeats.condition.Condition;
import io.github.maki99999.biomebeats.util.BiomeBeatsColor;
import io.github.maki99999.biomebeats.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static io.github.maki99999.biomebeats.util.DrawUtils.drawScrollingString;

public class ConditionList extends ScrollArea implements Renderable, ContainerEventHandler {
    private static final int CHILDREN_HEIGHT = 16;
    private static final int CHILDREN_SPACING = 4;

    private final OnSelected onSelected;
    private final OnEditPress onEditPress;
    private final Minecraft minecraft;

    private List<Entry> children = new ArrayList<>();
    @Nullable
    private ConditionList.Entry selectedChild = null;
    @Nullable
    private GuiEventListener focusedChild = null;
    private boolean isDragging;

    public ConditionList(Minecraft minecraft, Rect bounds, Component message, OnSelected onSelected,
                         OnEditPress onEditPress) {
        super(bounds, message);
        this.onSelected = onSelected;
        this.onEditPress = onEditPress;
        this.minecraft = minecraft;
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean clickedScrollbar = updateScrolling(x, y, button);
        boolean clickedChild = false;

        for (Entry child : children) {
            if (child.mouseClicked(x, y + scrollAmount(), button)) {
                selectedChild = child;
                onSelected.onSelected(child.getCondition());
                clickedChild = true;
            }
        }
        return clickedScrollbar || clickedChild;
    }

    @Override
    protected void renderBackground(@NotNull GuiGraphics guiGraphics) {
        guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), BiomeBeatsColor.DARK_GREY.getHex());
        if (scrollbarVisible()) {
            guiGraphics.fill(getX() + getWidth(), getY(), getX() + getWidth(), getY() + getHeight(),
                    BiomeBeatsColor.DARK_GREY.getHex());
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {}

    @Override
    protected int contentHeight() {
        return contentHeight(children.size());
    }

    private int contentHeight(int childrenCount) {
        return Math.max(childrenCount * (CHILDREN_HEIGHT + CHILDREN_SPACING), 0);
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
            entry.setY(2 + getY() + i * (CHILDREN_HEIGHT + CHILDREN_SPACING));
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

    public void setConditions(Collection<? extends Condition> conditions, Condition currentCondition) {
        int childrenWidth = contentHeight(conditions.size()) > getHeight() ? width - SCROLLBAR_WIDTH - 2 : width - 2;

        children = new ArrayList<>();
        for (Condition condition : conditions) {
            Entry entry = new Entry(minecraft, getX() + 1, 0, childrenWidth, CHILDREN_HEIGHT, condition);
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

    public interface OnEditPress {
        void onEditPress(CombinedCondition condition);
    }

    private class Entry extends AbstractWidget {
        private final Minecraft minecraft;
        private final Condition condition;
        private final ImageButton editButton;
        private final WidgetTooltipHolder tooltip = new WidgetTooltipHolder();

        private boolean selected = false;

        public Entry(Minecraft minecraft, int x, int y, int w, int h, Condition condition) {
            super(x, y, w, h, Component.literal(condition.getName()));
            this.minecraft = minecraft;
            this.condition = condition;

            if (condition instanceof CombinedCondition combinedCondition) {
                editButton = new LayeredImageButton(getX() + width - BaseTextureUv.EDIT_UV.w(), getY(),
                        BaseTextureUv.EDIT_UV, (click) -> onEditPress.onEditPress(combinedCondition),
                        Tooltip.create(Component.translatable("menu.biomebeats.edit")));
                tooltip.set(Tooltip.create(Component.literal(combinedCondition.getDescription())));
            } else {
                editButton = null;
            }
        }

        public Condition getCondition() {
            return condition;
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
            if (selected) {
                guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(),
                        BiomeBeatsColor.WHITE.getHex());
                guiGraphics.fill(getX() + 1, getY() + 1, getX() - 1 + getWidth(), getY() - 1 + getHeight(),
                        BiomeBeatsColor.LIGHT_GREY.getHex());
            } else {
                guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(),
                        BiomeBeatsColor.LIGHT_GREY.getHex());
            }
            Rect textRect = new Rect(getX() + 4, getY(), getWidth() - (editButton == null ? 8 :
                    (8 + BaseTextureUv.EDIT_UV.w())), getHeight());
            drawScrollingString(guiGraphics, minecraft.font, getMessage(), textRect,
                    (int) -ConditionList.this.scrollAmount(), BiomeBeatsColor.WHITE.getHex());

            if (editButton != null) {
                editButton.render(guiGraphics, mouseX, mouseY, (int) -ConditionList.this.scrollAmount());
                tooltip.refreshTooltipForNextRenderPass(guiGraphics.containsPointInScissor(mouseX,
                                mouseY + (int) -ConditionList.this.scrollAmount()) && textRect.contains(mouseX, mouseY),
                        false, new ScreenRectangle(textRect.x(), textRect.y(), textRect.w(), textRect.h()));
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return (editButton != null && editButton.mouseClicked(mouseX, mouseY, button))
                    || super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void setY(int y) {
            super.setY(y);
            if (editButton != null) {
                editButton.setY(y);
            }
        }

        @Override
        public void setWidth(int x) {
            super.setWidth(x);
            if (editButton != null) {
                editButton.setX(getX() + width - BaseTextureUv.EDIT_UV.w());
            }
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {}

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }
}
