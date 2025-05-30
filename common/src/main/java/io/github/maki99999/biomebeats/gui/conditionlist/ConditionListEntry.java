package io.github.maki99999.biomebeats.gui.conditionlist;

import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.condition.CombinedCondition;
import io.github.maki99999.biomebeats.condition.Condition;
import io.github.maki99999.biomebeats.gui.BaseTextureUv;
import io.github.maki99999.biomebeats.gui.ConditionViewModel;
import io.github.maki99999.biomebeats.gui.common.*;
import io.github.maki99999.biomebeats.gui.util.BiomeBeatsColor;
import io.github.maki99999.biomebeats.gui.util.Point;
import io.github.maki99999.biomebeats.gui.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static io.github.maki99999.biomebeats.gui.util.DrawUtils.drawScrollingString;

class ConditionListEntry extends UiElement {
    private final ConditionList conditionList;
    private final Minecraft minecraft;
    private final ConditionViewModel vm;
    private final ImageButton1 editButton;
    private final Component tooltipText;

    public ConditionListEntry(ConditionList conditionList, Minecraft minecraft, Rect bounds, Condition condition) {
        super(Component.literal(condition.getName()), bounds);
        this.conditionList = conditionList;
        this.minecraft = minecraft;
        vm = new ConditionViewModel(condition,
                !Constants.CONDITION_MUSIC_MANAGER.getMusicTracksForCondition(condition.getId()).isEmpty());

        if (condition instanceof CombinedCondition combinedCondition) {
            editButton = addChild(new LayeredImageButton1(Component.translatable("menu.biomebeats.edit"),
                    Component.translatable("menu.biomebeats.edit"), getWidth() - BaseTextureUv.EDIT_UV.w(), 0,
                    BaseTextureUv.EDIT_UV, (click) -> conditionList.onEditPress.onEditPress(combinedCondition)));
            tooltipText = Component.literal(combinedCondition.getDescription());
        } else {
            editButton = null;
            tooltipText = null;
        }
    }

    public Condition getCondition() {
        return vm.getCondition();
    }

    @Override
    protected void render(@NotNull GuiGraphics guiGraphics, Point mousePos, float partialTicks) {
        if (vm.isSelected()) {
            guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(),
                    BiomeBeatsColor.WHITE.getHex());
            guiGraphics.fill(getX() + 1, getY() + 1, getX() - 1 + getWidth(), getY() - 1 + getHeight(),
                    BiomeBeatsColor.LIGHT_GREY.getHex());
        } else {
            guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(),
                    BiomeBeatsColor.LIGHT_GREY.getHex());
        }
        drawIndicator(guiGraphics);

        drawScrollingString(guiGraphics, minecraft.font, getName(), getTextRect(),
                (int) -conditionList.getScrollAmount(), BiomeBeatsColor.WHITE.getHex());
    }

    private @NotNull Rect getTextRect() {
        return new Rect(getX() + 4, getY(), getWidth() - (editButton == null ? 8 :
                (8 + BaseTextureUv.EDIT_UV.w())), getHeight());
    }

    @Override
    public void renderTooltips(GuiGraphics guiGraphics, Point mousePos, Point absolutePos) {
        super.renderTooltips(guiGraphics, mousePos, absolutePos);
        if (tooltipText != null && getTextRect().contains(mousePos)) {
            guiGraphics.renderTooltip(minecraft.font, tooltipText, absolutePos.x(), absolutePos.y());
        }
    }

    private void drawIndicator(@NotNull GuiGraphics guiGraphics) {
        int offset = vm.isSelected() ? 1 : 0;
        guiGraphics.fill(getX() + offset, getY() + offset, getX() + 2, getY() + getHeight() - offset, vm.getIndicatorColor());
    }

    public void setSelected(boolean selected) {
        vm.setSelected(selected);
    }
}
