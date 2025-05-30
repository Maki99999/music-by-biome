package io.github.maki99999.biomebeats.gui;

import io.github.maki99999.biomebeats.condition.CombinedCondition;
import io.github.maki99999.biomebeats.condition.Condition;
import io.github.maki99999.biomebeats.gui.common.ImageButton;
import io.github.maki99999.biomebeats.gui.common.LayeredImageButton;
import io.github.maki99999.biomebeats.gui.common.ScrollArea;
import io.github.maki99999.biomebeats.gui.common.TwoStateImageButton;
import io.github.maki99999.biomebeats.gui.util.BiomeBeatsColor;
import io.github.maki99999.biomebeats.gui.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.maki99999.biomebeats.gui.util.DrawUtils.drawScrollingString;

public class ConditionCheckboxList extends ScrollArea implements Renderable, ContainerEventHandler {
    private static final int CHILDREN_HEIGHT = 16;
    private static final int CHILDREN_SPACING = 4;
    private static final List<Component> GROUP_ORDER;
    private static final Comparator<Component> GROUP_ORDER_COMPARATOR;

    static {
        GROUP_ORDER = new ArrayList<>();
        GROUP_ORDER.add(Component.translatable("menu.biomebeats.by_biome"));
        GROUP_ORDER.add(Component.translatable("menu.biomebeats.by_tag"));
        GROUP_ORDER.add(Component.translatable("menu.biomebeats.by_time"));
        GROUP_ORDER.add(Component.translatable("menu.biomebeats.by_other"));
        GROUP_ORDER.add(Component.translatable("menu.biomebeats.combined"));

        GROUP_ORDER_COMPARATOR = Comparator.comparingInt(c -> {
            int index = GROUP_ORDER.indexOf(c);
            return index == -1 ? Integer.MAX_VALUE : index;
        });
    }

    private final Minecraft minecraft;
    private final List<EntryGroup> children = new ArrayList<>();
    private final OnConditionToggle onConditionToggle;
    private final OnGroupToggle onGroupToggle;
    private final Collection<Condition> conditions;
    private final Rect bounds;

    @Nullable
    private GuiEventListener focusedChild = null;
    private boolean isDragging;

    public ConditionCheckboxList(Minecraft minecraft, Rect bounds, Component message,
                                 Collection<Condition> conditions, OnConditionToggle onConditionToggle,
                                 OnGroupToggle onGroupToggle) {
        super(bounds, message);

        this.minecraft = minecraft;
        this.onConditionToggle = onConditionToggle;
        this.conditions = conditions;
        this.bounds = bounds;
        this.onGroupToggle = onGroupToggle;

        sortAndFilterConditions("", List.of(), List.of());
    }

    private void UpdateY() {
        int height = 3;
        for (int i = 0; i < children.size(); i++) {
            EntryGroup entryGroup = children.get(i);
            entryGroup.setY(getY() + height);
            height += entryGroup.getHeight();
            if (i != children.size() - 1) {
                height += 5;
            }
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (!visible) return false;

        boolean clickedScrollbar = updateScrolling(x, y, button);
        boolean clickedChild = false;

        var childrenCopy = new ArrayList<>(children);

        for (EntryGroup child : childrenCopy) {
            if (child.mouseClicked(x, y + scrollAmount(), button)) {
                clickedChild = true;
            }
        }
        return clickedScrollbar || clickedChild;
    }

    @Override
    protected void renderBackground(@NotNull GuiGraphics guiGraphics) {
        guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), BiomeBeatsColor.DARK_GREY.getHex());
        if (scrollbarVisible()) {
            guiGraphics.fill(getX() + getWidth(), getY(), getX() + getWidth(),
                    getY() + getHeight(), BiomeBeatsColor.DARK_GREY.getHex());
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {}

    @Override
    protected int contentHeight() {
        int height = -2;
        for (EntryGroup entryGroup : children) {
            height += entryGroup.getHeight() + 5;
        }
        return height;
    }

    @Override
    protected double scrollRate() {
        return 30.0;
    }

    @Override
    protected void renderContents(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int height = 3;
        for (int i = 0; i < children.size(); i++) {
            EntryGroup entryGroup = children.get(i);
            entryGroup.render(guiGraphics, mouseX, mouseY + (int) scrollAmount(), partialTicks);
            height += entryGroup.getHeight();
            if (i != children.size() - 1) {
                guiGraphics.fill(getX(), getY() + height, getX() + getWidth(), getY() + height + 1,
                        BiomeBeatsColor.BLACK.getHex());
                height += 5;
            }
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

    public void setCheckedConditions(Collection<Condition> conditions) {
        for (var child : children) {
            child.setCheckedConditions(conditions);
        }
    }

    public void sortAndFilterConditions(String filter, Collection<Condition> checkedConditions,
                                        Collection<Component> collapsedGroups) {
        Set<Component> typeNames = new HashSet<>();
        var conditionsByType = conditions
                .stream()
                .filter(c -> !(c instanceof CombinedCondition))
                .peek(c -> typeNames.add(c.getType().getComponent()))
                .filter(c -> !collapsedGroups.contains(c.getType().getComponent()) && c.getName().toLowerCase().contains(filter))
                .sorted(Comparator.comparing(Condition::getName))
                .collect(Collectors.groupingBy(x -> x.getType().getComponent()));

        children.clear();
        for (Component conditionType : typeNames.stream().sorted(GROUP_ORDER_COMPARATOR).toList()) {
            if (conditionsByType.containsKey(conditionType)) {
                children.add(new EntryGroup(bounds.x(), 0, width, conditionType, conditionsByType.get(conditionType),
                        conditionType, collapsedGroups.contains(conditionType)));
            } else {
                children.add(new EntryGroup(bounds.x(), 0, width, conditionType, List.of(), conditionType,
                        collapsedGroups.contains(conditionType)));
            }
        }

        int childrenWidth = scrollbarVisible() ? width - SCROLLBAR_WIDTH : width;
        for (AbstractWidget entry : children) {
            entry.setWidth(childrenWidth);
        }
        UpdateY();

        setCheckedConditions(checkedConditions);
        mouseScrolled(0, 0, 0, 0);
    }

    private class EntryGroup extends AbstractWidget {
        private final List<Entry> children = new ArrayList<>();
        private final TwoStateImageButton collapseButton;

        public EntryGroup(int x, int y, int w, Component message, Collection<Condition> conditions,
                          Component typeName, boolean isCollapsed) {
            super(x, y, w, 0, message);
            collapseButton = new TwoStateImageButton(x + w - 24, y + 1, new ImageButton(x + w - 24, y + 1,
                    BaseTextureUv.ACCORDION_OPEN_UV, null, null), new io.github.maki99999.biomebeats.gui.common.ImageButton(x + w - 24, y + 1,
                    BaseTextureUv.ACCORDION_CLOSE_UV, null, null),
                    (btn, newValue) -> onGroupToggle.onGroupToggle(typeName, newValue),
                    Tooltip.create(Component.translatable("menu.biomebeats.expand_collapse")), null);
            collapseButton.setState(isCollapsed);

            for (Condition condition : conditions) {
                children.add(new Entry(condition, new Rect(x + 1, 0, width - 2, CHILDREN_HEIGHT)));
            }

            setHeight((children.size() + 1) * CHILDREN_HEIGHT + (children.size() - 1) * CHILDREN_SPACING + 8);
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
            drawScrollingString(guiGraphics, ConditionCheckboxList.this.minecraft.font, getMessage(),
                    new Rect(getX() + 16, getY() + 4, getWidth() - 48, 8),
                    (int) -ConditionCheckboxList.this.scrollAmount(), BiomeBeatsColor.WHITE.getHex());
            collapseButton.render(guiGraphics, mouseX, mouseY, (int) -ConditionCheckboxList.this.scrollAmount());

            for (Entry c : children) {
                c.render(guiGraphics, mouseX, mouseY, partialTicks);
            }
        }

        @Override
        public void setY(int y) {
            super.setY(y);
            collapseButton.setY(y);

            for (int i = 0; i < children.size(); i++) {
                children.get(i).setY(y + (i + 1) * (CHILDREN_HEIGHT + CHILDREN_SPACING));
            }
        }

        @Override
        public void setWidth(int width) {
            super.setWidth(width);
            collapseButton.setX(getX() + width - 24);

            for (AbstractWidget entry : children) {
                entry.setWidth(width - 2);
            }
        }

        @Override
        public boolean mouseClicked(double x, double y, int button) {
            if (collapseButton.mouseClicked(x, y, button)) {
                return true;
            }

            for (Entry child : children) {
                if (child.mouseClicked(x, y, button)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {}

        public void setCheckedConditions(Collection<? extends Condition> conditions) {
            for (var child : children) {
                child.setCheckedState(conditions.contains(child.getCondition()));
            }
        }

        private class Entry extends AbstractWidget {
            private final Condition condition;
            private final TwoStateImageButton checkbox;

            public Entry(Condition condition, Rect bounds) {
                super(bounds.x(), bounds.y(), bounds.w(), bounds.h(), Component.literal(condition.getName()));
                this.condition = condition;

                checkbox = new TwoStateImageButton(getX(), getY(), new LayeredImageButton(getX(), getY(),
                        BaseTextureUv.CHECKBOX_CHECKED_UV, null, null), new LayeredImageButton(getX(), getY(),
                        BaseTextureUv.BUTTON_BASE_INVERTED_UV, null, null),
                        (c, newValue) -> ConditionCheckboxList.this.onConditionToggle.onConditionToggle(condition,
                                newValue), null, null);
            }

            @Override
            protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
                guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(),
                        BiomeBeatsColor.LIGHT_GREY.getHex());

                Rect textRect = new Rect(getX() + 18, getY(), getWidth() - 22, getHeight());
                drawScrollingString(guiGraphics, ConditionCheckboxList.this.minecraft.font, getMessage(), textRect,
                        (int) -ConditionCheckboxList.this.scrollAmount(), BiomeBeatsColor.WHITE.getHex());

                checkbox.render(guiGraphics, mouseX, mouseY, (int) -ConditionCheckboxList.this.scrollAmount());
            }

            @Override
            public void setY(int y) {
                super.setY(y);
                checkbox.setY(y);
            }

            @Override
            protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {}

            @Override
            public boolean mouseClicked(double x, double y, int button) {
                return checkbox.mouseClicked(x, y, button);
            }

            public Condition getCondition() {
                return condition;
            }

            public void setCheckedState(boolean newValue) {
                checkbox.setState(newValue);
            }
        }
    }

    public interface OnConditionToggle {
        void onConditionToggle(Condition condition, boolean newValue);
    }

    public interface OnGroupToggle {
        void onGroupToggle(Component group, boolean newValue);
    }
}
