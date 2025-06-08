package io.github.maki99999.biomebeats.gui;

import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.gui.common.*;
import io.github.maki99999.biomebeats.gui.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import static io.github.maki99999.biomebeats.gui.util.DrawUtils.*;

public class GeneralConfigScreen extends UiElement {
    private static final int ELEMENT_HEIGHT = 17;
    private static final int SPACING = 4;
    private Rect bounds;
    private Rect dangerBounds;
    private EditBoxWrapper fadeTimeEditBox;
    private EditBoxWrapper breakTimeEditBox;
    private GridLayout topGrid;
    private GridLayout dangerGrid;

    public GeneralConfigScreen() {
        super(Component.literal("BiomeBeats settings config screen"));
    }

    @Override
    protected void init() {
        int heightUpperPart = 2 * (ELEMENT_HEIGHT + SPACING) + SPACING;
        int heightLowerPart = 2 * (ELEMENT_HEIGHT + SPACING) + 3 * SPACING;

        int innerHeight = heightUpperPart + heightLowerPart;
        int innerWidth = 260;
        bounds = new Rect((getWidth() - innerWidth) / 2, (getHeight() - innerHeight) / 2, innerWidth, innerHeight);

        Rect mainAreaBounds = new Rect(bounds.x(), bounds.y(), innerWidth, heightUpperPart);
        dangerBounds = new Rect(mainAreaBounds.x() + SPACING, mainAreaBounds.y2() + SPACING, innerWidth - 2 * SPACING, heightLowerPart - 2 * SPACING);

        topGrid = new GridLayout(mainAreaBounds, 2, 2, SPACING);
        dangerGrid = new GridLayout(dangerBounds, 2, 2, SPACING);

        addChild(new TextButton(Component.translatable("menu.biomebeats.reset-to-default"),
                                Component.translatable("menu.biomebeats.reset-to-default-tooltip"),
                                dangerGrid.getCell(0, 1),
                                this::onResetToDefault));

        addChild(new TextButton(Component.translatable("menu.biomebeats.clear-config"),
                                Component.translatable("menu.biomebeats.clear-config-tooltip"),
                                dangerGrid.getCell(1, 1),
                                this::onClearConfig));

        Rect fadeTimeEditBoxBounds = topGrid.getCell(0, 1);
        fadeTimeEditBox = addChild(new EditBoxWrapper(Component.literal("1 s"), fadeTimeEditBoxBounds));
        fadeTimeEditBox.setHint(Component.literal("1 s"));
        fadeTimeEditBox.setFilter(s -> s.matches("^\\d{0,9}$"));

        Rect breakTimeEditBoxBounds = topGrid.getCell(1, 1);
        breakTimeEditBox = addChild(new EditBoxWrapper(Component.literal("0 s"), breakTimeEditBoxBounds));
        breakTimeEditBox.setHint(Component.literal("0 s"));
        breakTimeEditBox.setFilter(s -> s.matches("^\\d{0,9}$"));

        loadSettings();
    }

    @Override
    public void render(GuiGraphics guiGraphics, Point mousePos, float deltaTime) {
        // Background
        drawContainer(guiGraphics, bounds);

        // Danger Border
        drawTiledNineSliceRect(BaseTextureUv.RL, guiGraphics, dangerBounds, BaseTextureUv.DANGER_BORDER_UV,
                BaseTextureUv.DANGER_BORDER_INNER_UV);

        if (getMinecraft() != null) {
            drawScrollingString(guiGraphics, getMinecraft().font, Component.translatable("menu.biomebeats.fade-time"),
                                topGrid.getCell(0, 0), BiomeBeatsColor.WHITE.getHex());
            drawScrollingString(guiGraphics, getMinecraft().font, Component.translatable("menu.biomebeats.break-time"),
                                topGrid.getCell(1, 0), BiomeBeatsColor.WHITE.getHex());
            drawScrollingString(guiGraphics, getMinecraft().font, Component.translatable("menu.biomebeats.configs"),
                                dangerGrid.getCell(0, 0), BiomeBeatsColor.WHITE.getHex());
        }
    }

    private void onResetToDefault(Button button) {
        Constants.CONFIG_IO.resetConfig();
        loadSettings();
    }

    private void onClearConfig(Button button) {
        Constants.CONFIG_IO.clearConfig();
        loadSettings();
    }

    private void loadSettings() {
        fadeTimeEditBox.setValue("" + Constants.CONFIG_IO.getGeneralConfig().getFadeTime());
        breakTimeEditBox.setValue("" + Constants.CONFIG_IO.getGeneralConfig().getBreakTime());
    }

    @Override
    public void onClose() {
        if (getMinecraft() != null) {
            getMinecraft().setScreen(new ForwardingScreen<>(new ConfigScreen()));
        }

        try {
            Constants.CONFIG_IO.getGeneralConfig().setBreakTime(Integer.parseInt(breakTimeEditBox.getValue()));
        } catch (NumberFormatException e) {
            Constants.CONFIG_IO.getGeneralConfig().setDefaultBreakTime();
        }

        try {
            Constants.CONFIG_IO.getGeneralConfig().setFadeTime(Integer.parseInt(fadeTimeEditBox.getValue()));
        } catch (NumberFormatException e) {
            Constants.CONFIG_IO.getGeneralConfig().setDefaultFadeTime();
        }
    }
}
