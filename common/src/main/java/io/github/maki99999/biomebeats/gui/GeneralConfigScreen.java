package io.github.maki99999.biomebeats.gui;

import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.util.BiomeBeatsColor;
import io.github.maki99999.biomebeats.util.GridLayout;
import io.github.maki99999.biomebeats.util.Rect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static io.github.maki99999.biomebeats.util.DrawUtils.*;

public class GeneralConfigScreen extends Screen {
    private static final int ELEMENT_HEIGHT = 17;
    private static final int SPACING = 4;
    private final ConfigScreen configScreen;
    private Rect bounds;
    private Rect dangerBounds;
    private TextButton clearConfigBtn;
    private EditBox fadeTimeEditBox;
    private EditBox breakTimeEditBox;
    private TextButton resetToDefaultBtn;
    private GridLayout topGrid;
    private GridLayout dangerGrid;

    public GeneralConfigScreen(ConfigScreen configScreen) {
        super(Component.literal("BiomeBeats settings config screen"));
        this.configScreen = configScreen;
    }

    @Override
    protected void init() {
        int heightUpperPart = 2 * (ELEMENT_HEIGHT + SPACING) + SPACING;
        int heightLowerPart = 2 * (ELEMENT_HEIGHT + SPACING) + 3 * SPACING;

        int innerHeight = heightUpperPart + heightLowerPart;
        int innerWidth = 260;
        bounds = new Rect((width - innerWidth) / 2, (height - innerHeight) / 2, innerWidth, innerHeight);

        Rect mainAreaBounds = new Rect(bounds.x(), bounds.y(), innerWidth, heightUpperPart);
        dangerBounds = new Rect(mainAreaBounds.x() + SPACING, mainAreaBounds.y2() + SPACING, innerWidth - 2 * SPACING, heightLowerPart - 2 * SPACING);

        topGrid = new GridLayout(mainAreaBounds, 2, 2, SPACING);
        dangerGrid = new GridLayout(dangerBounds, 2, 2, SPACING);

        resetToDefaultBtn = new TextButton(dangerGrid.getCell(0, 1),
                Component.translatable("menu.biomebeats.reset-to-default"), this::onResetToDefault,
                Tooltip.create(Component.translatable("menu.biomebeats.reset-to-default-tooltip")));

        clearConfigBtn = new TextButton(dangerGrid.getCell(1, 1),
                Component.translatable("menu.biomebeats.clear-config"), this::onClearConfig,
                Tooltip.create(Component.translatable("menu.biomebeats.clear-config-tooltip")));

        Rect fadeTimeEditBoxBounds = topGrid.getCell(0, 1);
        fadeTimeEditBox = addWidget(new EditBox(font, fadeTimeEditBoxBounds.x(), fadeTimeEditBoxBounds.y(),
                fadeTimeEditBoxBounds.w(), fadeTimeEditBoxBounds.h(),
                Component.literal("1 s")));
        fadeTimeEditBox.setHint(Component.literal("1 s"));
        fadeTimeEditBox.setFilter(s -> s.matches("^\\d{0,9}$"));
        fadeTimeEditBox.setValue("" + Constants.CONFIG_IO.getGeneralConfig().getFadeTime());

        Rect breakTimeEditBoxBounds = topGrid.getCell(1, 1);
        breakTimeEditBox = addWidget(new EditBox(font, breakTimeEditBoxBounds.x(), breakTimeEditBoxBounds.y(),
                breakTimeEditBoxBounds.w(), breakTimeEditBoxBounds.h(),
                Component.literal("0 s")));
        breakTimeEditBox.setHint(Component.literal("0 s"));
        breakTimeEditBox.setFilter(s -> s.matches("^\\d{0,9}$"));
        breakTimeEditBox.setValue("" + Constants.CONFIG_IO.getGeneralConfig().getBreakTime());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Background
        drawContainer(guiGraphics, bounds);

        // Danger Border
        drawTiledNineSliceRect(BaseTextureUv.RL, guiGraphics, dangerBounds, BaseTextureUv.DANGER_BORDER_UV,
                BaseTextureUv.DANGER_BORDER_INNER_UV);

        clearConfigBtn.render(guiGraphics, mouseX, mouseY, 0);
        resetToDefaultBtn.render(guiGraphics, mouseX, mouseY, 0);
        fadeTimeEditBox.render(guiGraphics, mouseX, mouseY, partialTick);
        breakTimeEditBox.render(guiGraphics, mouseX, mouseY, partialTick);
        if (minecraft != null) {
            drawScrollingString(guiGraphics, minecraft.font, Component.translatable("menu.biomebeats.fade-time"),
                    topGrid.getCell(0, 0), 0, BiomeBeatsColor.WHITE.getHex());
            drawScrollingString(guiGraphics, minecraft.font, Component.translatable("menu.biomebeats.break-time"),
                    topGrid.getCell(1, 0), 0, BiomeBeatsColor.WHITE.getHex());
            drawScrollingString(guiGraphics, minecraft.font, Component.translatable("menu.biomebeats.configs"),
                    dangerGrid.getCell(0, 0), 0, BiomeBeatsColor.WHITE.getHex());
        }
    }

    private void onResetToDefault(Button button) {
        Constants.CONFIG_IO.resetConfig();
        init();
    }

    private void onClearConfig(Button button) {
        Constants.CONFIG_IO.clearConfig();
        init();
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            minecraft.setScreen(configScreen);
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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return resetToDefaultBtn.mouseClicked(mouseX, mouseY, button)
                || clearConfigBtn.mouseClicked(mouseX, mouseY, button)
                || super.mouseClicked(mouseX, mouseY, button);
    }
}
