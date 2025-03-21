package io.github.maki99999.biomebeats.gui;

import io.github.maki99999.biomebeats.condition.CombinedCondition;
import io.github.maki99999.biomebeats.condition.Condition;
import io.github.maki99999.biomebeats.util.BiomeBeatsColor;
import io.github.maki99999.biomebeats.util.Rect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static io.github.maki99999.biomebeats.util.DrawUtils.drawContainer;
import static io.github.maki99999.biomebeats.util.DrawUtils.drawScrollingString;

public class CombinedConditionConfigScreen extends Screen {
    private static final int MAX_WIDTH = 250;
    private static final int MAX_HEIGHT = 400;
    private static final int ELEMENT_HEIGHT = 16;
    private static final int ELEMENT_SPACING = 4;

    private final ConfigScreen configScreen;
    private final CombinedCondition oldCondition;
    private final Collection<Condition> allConditions;
    private final Collection<Component> collapsedConditionTypes = new ArrayList<>();

    private CombinedCondition condition;
    private Rect bounds;
    private Rect innerBounds;
    private EditBox nameField;
    private MultiLineEditBox descriptionField;
    private EditBox conditionSearchBox;
    private ConditionCheckboxList conditionList;
    private TextButton deleteBtn;
    private TextButton confirmBtn;

    public CombinedConditionConfigScreen(ConfigScreen configScreen, CombinedCondition condition,
                                         Collection<Condition> allConditions) {
        super(Component.literal("BiomeBeats combined condition config screen"));
        this.configScreen = configScreen;
        this.oldCondition = condition;
        this.condition = oldCondition == null ? new CombinedCondition() : new CombinedCondition(oldCondition);
        this.allConditions = allConditions;
    }

    @Override
    public void onClose() {
        if (condition != null && condition.getName().isBlank()) {
            condition.setName("Combined Condition");
        }

        configScreen.updateCombinedCondition(oldCondition, condition);
        if (this.minecraft != null) {
            this.minecraft.setScreen(configScreen);
        }
    }

    @Override
    protected void init() {
        int w = Math.min(width, MAX_WIDTH);
        int h = Math.min(height, MAX_HEIGHT);
        bounds = new Rect((width - w) / 2, (height - h) / 2, w, h);
        bounds = new Rect(bounds.x() + ELEMENT_SPACING, bounds.y() + ELEMENT_SPACING,
                bounds.w() - 2 * ELEMENT_SPACING, bounds.h() - 2 * ELEMENT_SPACING);
        innerBounds = new Rect(bounds.x() + ELEMENT_SPACING, bounds.y() + ELEMENT_SPACING,
                bounds.w() - 2 * ELEMENT_SPACING, bounds.h() - 2 * ELEMENT_SPACING);

        nameField = addWidget(new EditBox(font, innerBounds.x() + 80, innerBounds.y(), innerBounds.w() - 80,
                ELEMENT_HEIGHT, Component.translatable("menu.biomebeats.name")));
        if (oldCondition != null) nameField.setValue(oldCondition.getName());
        nameField.setHint(Component.translatable("menu.biomebeats.name"));
        nameField.setResponder(condition::setName);

        descriptionField = addWidget(new MultiLineEditBox(font, innerBounds.x() + 80,
                innerBounds.y() + ELEMENT_HEIGHT + ELEMENT_SPACING, innerBounds.w() - 80, 2 * ELEMENT_HEIGHT,
                Component.translatable("menu.biomebeats.description"), Component.translatable("menu.biomebeats" +
                ".description")));
        if (oldCondition != null) descriptionField.setValue(oldCondition.getDescription());
        descriptionField.setValueListener(condition::setDescription);
        descriptionField.setCharacterLimit(42);

        conditionSearchBox = addWidget(new EditBox(font, innerBounds.x(),
                innerBounds.y() + 3 * (ELEMENT_HEIGHT + ELEMENT_SPACING) + 2 * ELEMENT_SPACING, innerBounds.w(),
                ELEMENT_HEIGHT, Component.translatable("menu.biomebeats.search.condition")));
        conditionSearchBox.setHint(Component.translatable("menu.biomebeats.search.condition"));
        conditionSearchBox.setResponder(this::onConditionSearchUpdate);

        deleteBtn = new TextButton(new Rect(innerBounds.x1(), innerBounds.y2() - ELEMENT_HEIGHT, 50, ELEMENT_HEIGHT),
                Component.translatable("menu.biomebeats.delete"), (btn) -> {
            condition = null;
            onClose();
        }, null);

        confirmBtn = new TextButton(new Rect(innerBounds.x2() - 100, innerBounds.y2() - ELEMENT_HEIGHT, 100,
                ELEMENT_HEIGHT), Component.translatable("menu.biomebeats.confirm"), (btn) -> onClose(), null);

        conditionList = addWidget(new ConditionCheckboxList(minecraft, Rect.fromCoordinates(innerBounds.x1(),
                conditionSearchBox.getY() + conditionSearchBox.getHeight(), innerBounds.x2(),
                confirmBtn.getY() - ELEMENT_SPACING), Component.translatable("menu" + ".biomebeats.search.music"),
                allConditions, this::onConditionToggle, this::onGroupToggle));
        conditionList.setCheckedConditions(condition.getConditions());
    }

    private void onConditionSearchUpdate(String text) {
        conditionList.sortAndFilterConditions(text.trim().toLowerCase(), condition.getConditions(),
                collapsedConditionTypes);
    }

    private void onGroupToggle(Component typeName, boolean newValue) {
        if (newValue) {
            collapsedConditionTypes.add(typeName);
        } else {
            collapsedConditionTypes.remove(typeName);
        }

        conditionList.sortAndFilterConditions(conditionSearchBox.getValue().trim().toLowerCase(),
                condition.getConditions(), collapsedConditionTypes);
    }

    private void onConditionToggle(Condition condition, boolean newValue) {
        if (newValue) {
            this.condition.addCondition(condition);
        } else {
            this.condition.removeCondition(condition);
        }

        conditionList.setCheckedConditions(this.condition.getConditions());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        drawContainer(guiGraphics, bounds);
        nameField.render(guiGraphics, mouseX, mouseY, partialTick);
        drawScrollingString(guiGraphics, font, Component.translatable("menu.biomebeats.name"),
                new Rect(innerBounds.x(), innerBounds.y(), 80, ELEMENT_HEIGHT), 0, BiomeBeatsColor.WHITE.getHex());
        descriptionField.render(guiGraphics, mouseX, mouseY, partialTick);
        drawScrollingString(guiGraphics, font, Component.translatable("menu.biomebeats.description"),
                new Rect(innerBounds.x(), innerBounds.y() + ELEMENT_HEIGHT + ELEMENT_SPACING, 80, ELEMENT_HEIGHT), 0,
                BiomeBeatsColor.WHITE.getHex());
        conditionSearchBox.render(guiGraphics, mouseX, mouseY, partialTick);
        conditionList.render(guiGraphics, mouseX, mouseY, partialTick);
        if (oldCondition != null) deleteBtn.render(guiGraphics, mouseX, mouseY, 0);
        confirmBtn.render(guiGraphics, mouseX, mouseY, 0);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return (oldCondition != null && deleteBtn.mouseClicked(mouseX, mouseY, button))
                || confirmBtn.mouseClicked(mouseX, mouseY, button)
                || super.mouseClicked(mouseX, mouseY, button);
    }
}
