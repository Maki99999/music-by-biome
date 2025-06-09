package io.github.maki99999.biomebeats.gui.condcheckboxlist;

import io.github.maki99999.biomebeats.condition.CombinedCondition;
import io.github.maki99999.biomebeats.condition.Condition;
import io.github.maki99999.biomebeats.gui.common.*;
import io.github.maki99999.biomebeats.gui.util.BiomeBeatsColor;
import io.github.maki99999.biomebeats.gui.util.Point;
import io.github.maki99999.biomebeats.gui.util.PointD;
import io.github.maki99999.biomebeats.gui.util.Rect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ConditionCheckboxList extends ScrollContainer {
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

    private final List<ConditionCheckboxListEntryGroup> entryGroups = new ArrayList<>();
    final OnConditionToggle onConditionToggle;
    final OnGroupToggle onGroupToggle;
    private final Collection<Condition> conditions;

    public ConditionCheckboxList(Rect bounds, Component message,
                                 Collection<Condition> conditions, OnConditionToggle onConditionToggle,
                                 OnGroupToggle onGroupToggle) {
        super(message, bounds);

        this.onConditionToggle = onConditionToggle;
        this.conditions = conditions;
        this.onGroupToggle = onGroupToggle;

        sortAndFilterConditions("", List.of(), List.of());
    }

    @Override
    protected int getContentHeight() {
        int height = -2;
        for (ConditionCheckboxListEntryGroup entryGroup : entryGroups) {
            height += entryGroup.getHeight() + 5;
        }
        return height;
    }

    @Override
    protected void renderBackground(@NotNull GuiGraphics guiGraphics) {
        guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), BiomeBeatsColor.DARK_GREY.getHex());
        if (isScrollbarVisible()) {
            guiGraphics.fill(getX() + getWidth(), getY(), getX() + getWidth(),
                             getY() + getHeight(), BiomeBeatsColor.DARK_GREY.getHex());
        }
    }

    @Override
    protected void renderContent(@NotNull GuiGraphics guiGraphics, Point mousePos, float partialTicks) {
        int height = 3;
        for (int i = 0; i < entryGroups.size(); i++) {
            ConditionCheckboxListEntryGroup entryGroup = entryGroups.get(i);
            entryGroup.renderAll(guiGraphics, mousePos, partialTicks);
            height += entryGroup.getHeight();
            if (i != entryGroups.size() - 1) {
                guiGraphics.fill(0, height, getWidth(), height + 1, BiomeBeatsColor.BLACK.getHex());
                height += 5;
            }
        }
    }

    void updateY() {
        int height = 3;
        for (int i = 0; i < entryGroups.size(); i++) {
            ConditionCheckboxListEntryGroup entryGroup = entryGroups.get(i);
            entryGroup.setY(height);
            height += entryGroup.getHeight();
            if (i != entryGroups.size() - 1) {
                height += 5;
            }
        }
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

        for (ConditionCheckboxListEntryGroup child : entryGroups) {
            if (child.isMouseOver(mousePos.translate(-getX(), -getY() + getScrollAmount()).toIntPoint())
                && child.mouseClickedAll(mousePos.translate(-getX(), -getY() + getScrollAmount()), button)) {
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
        for (ConditionCheckboxListEntryGroup child : entryGroups) {
            child.renderTooltips(guiGraphics, mousePos, absolutePos);
        }
    }

    public void setCheckedConditions(Collection<Condition> conditions) {
        for (var child : entryGroups) {
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

        entryGroups.clear();
        for (Component conditionType : typeNames.stream().sorted(GROUP_ORDER_COMPARATOR).toList()) {
            if (conditionsByType.containsKey(conditionType)) {
                entryGroups.add(new ConditionCheckboxListEntryGroup(this,
                                                                    0, 0, getWidth(), conditionType, conditionsByType.get(conditionType),
                                                                    conditionType, collapsedGroups.contains(conditionType)));
            } else {
                entryGroups.add(new ConditionCheckboxListEntryGroup(this,
                                                                    0, 0, getWidth(), conditionType, List.of(), conditionType,
                                                                    collapsedGroups.contains(conditionType)));
            }
        }

        int childrenWidth = isScrollbarVisible() ? getWidth() - SCROLLBAR_WIDTH : getWidth();
        for (ConditionCheckboxListEntryGroup entry : entryGroups) {
            entry.setWidth(childrenWidth);
        }
        updateY();

        setCheckedConditions(checkedConditions);
        mouseScrolled(new PointD(0, 0), 0, 0);
    }

    public interface OnConditionToggle {
        void onConditionToggle(Condition condition, boolean newValue);
    }

    public interface OnGroupToggle {
        void onGroupToggle(Component group, boolean newValue);
    }
}
