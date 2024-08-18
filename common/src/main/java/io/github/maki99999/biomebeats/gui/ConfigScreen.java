package io.github.maki99999.biomebeats.gui;

import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.condition.Condition;
import io.github.maki99999.biomebeats.config.ConfigChangeListener;
import io.github.maki99999.biomebeats.config.MainConfig;
import io.github.maki99999.biomebeats.music.MusicGroup;
import io.github.maki99999.biomebeats.music.MusicTrack;
import io.github.maki99999.biomebeats.util.BiomeBeatsColor;
import io.github.maki99999.biomebeats.util.Rect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static io.github.maki99999.biomebeats.util.DrawUtils.drawRect;
import static io.github.maki99999.biomebeats.util.DrawUtils.drawScrollingString;

public class ConfigScreen extends Screen implements ConfigChangeListener {
    private static final ResourceLocation BASE_RL = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/base.png");
    private static final int SIDES_PADDING = 60;
    private static final int BORDER_PADDING = 4;
    private static final int ELEMENT_HEIGHT = 17;
    private static final int ELEMENT_SPACING = 4;
    private static final Collection<MusicTrack> NO_MUSIC_TRACKS = Set.of();

    private MainConfig config;

    private Map<TwoStateImageButton, TabType> tabs;
    private ConditionList conditionList;
    private MusicList musicList;
    private EditBox conditionSearchBox;
    private EditBox musicSearchBox;
    private EditBox priorityField;
    private Rect bounds;
    private Rect boundsL;
    private Rect boundsR;

    private Collection<MusicGroup> musicGroups;
    private Map<Condition, Collection<MusicTrack>> musicTracksByCondition;
    private Map<TabType, Collection<? extends Condition>> conditions;
    private boolean initialInitCall = true;
    private TabType currentTab = TabType.BY_BIOME;
    private Condition currentCondition = null;

    public ConfigScreen() {
        super(Component.literal("BiomeBeats config screen"));
        Constants.CONFIG_IO.addListener(this);
    }

    @Override
    protected void init() {
        if (initialInitCall) {
            initialInitCall = false;
            initData();
        }

        // Update bounds
        bounds = new Rect(SIDES_PADDING, 0, width - 2 * SIDES_PADDING, height);
        boundsL = new Rect(bounds.x() + BORDER_PADDING, bounds.y() + BORDER_PADDING, Mth.floor((bounds.w() - BORDER_PADDING * 2) * 0.4f), height - BORDER_PADDING * 2);
        boundsR = new Rect(boundsL.x2() + BORDER_PADDING, bounds.y() + BORDER_PADDING, bounds.w() - BORDER_PADDING * 2 - boundsL.w() - BORDER_PADDING, height - BORDER_PADDING * 2);

        // Left column
        conditionSearchBox = new EditBox(font, boundsL.x(), boundsL.y(), boundsL.w(), ELEMENT_HEIGHT,
                Component.translatable("menu.biomebeats.search.condition"));
        conditionSearchBox.setHint(Component.translatable("menu.biomebeats.search.condition"));
        conditionSearchBox.setResponder(this::onConditionSearchUpdate);
        addWidget(conditionSearchBox);

        conditionList = addWidget(new ConditionList(minecraft, new Rect(boundsL.x(), boundsL.y() + ELEMENT_HEIGHT,
                boundsL.w(), boundsL.h() - ELEMENT_HEIGHT),
                Component.translatable("menu.biomebeats.search.condition"), conditions.get(currentTab),
                this::onConditionSelected));

        // Right column
        priorityField = new EditBox(font, boundsR.x2() - 60, boundsR.y(), 60, ELEMENT_HEIGHT,
                Component.translatable("menu.biomebeats.priority"));
        priorityField.setHint(Component.literal("0"));
        priorityField.setResponder(this::onPriorityUpdate);
        priorityField.setFilter(s -> s.matches("^[+-]?\\d{1,9}$"));
        addWidget(priorityField);

        musicSearchBox = new EditBox(font, boundsR.x(), boundsR.y() + ELEMENT_HEIGHT + ELEMENT_SPACING, boundsR.w(),
                ELEMENT_HEIGHT, Component.translatable("menu.biomebeats.search.music"));
        musicSearchBox.setHint(Component.translatable("menu.biomebeats.search.music"));
        musicSearchBox.setResponder(this::onMusicSearchUpdate);
        addWidget(musicSearchBox);

        musicList = addWidget(new MusicList(minecraft,
                new Rect(boundsR.x(), boundsR.y() + ELEMENT_HEIGHT * 2 + ELEMENT_SPACING,
                        boundsR.w(), boundsR.h() - ELEMENT_HEIGHT * 2 - ELEMENT_SPACING),
                Component.translatable("menu.biomebeats.search.music"), musicGroups,
                this::onMusicTrackToggle));

        // Tabs
        tabs = new HashMap<>();
        addTab(TabType.BY_BIOME, Component.translatable("menu.biomebeats.by_biome"), bounds.x() - 57, bounds.y() + 4);
        addTab(TabType.BY_TAG, Component.translatable("menu.biomebeats.by_tag"), bounds.x() - 57, bounds.y() + 26);
        addTab(TabType.BY_TIME, Component.translatable("menu.biomebeats.by_time"), bounds.x() - 57, bounds.y() + 48);
        addTab(TabType.BY_OTHER, Component.translatable("menu.biomebeats.by_other"), bounds.x() - 57, bounds.y() + 70);
        addTab(TabType.COMBINED, Component.translatable("menu.biomebeats.combined"), bounds.x() - 57, bounds.y() + 92);

        if (currentCondition == null) {
            setRightColumnVisibility(false);
        } else {
            conditionList.UpdateSelection(currentCondition);
            setRightColumnVisibility(true);
            updateCheckedMusicTracks();
        }
    }

    private void initData() {
        musicGroups = Constants.MUSIC_MANAGER.getMusicGroups();

        conditions = Map.ofEntries(
                Map.entry(TabType.BY_BIOME, Constants.CONDITION_MANAGER.getBiomeConditions()),
                Map.entry(TabType.BY_TAG, Constants.CONDITION_MANAGER.getTagConditions()),
                Map.entry(TabType.BY_TIME, List.of()),
                Map.entry(TabType.BY_OTHER, List.of()),
                Map.entry(TabType.COMBINED, List.of())
        );

        musicTracksByCondition = Constants.CONDITION_MUSIC_MANAGER.getMusicTracksByCondition();
    }

    private void addTab(TabType tabType, Component text, int x, int y) {
        if (minecraft == null) return;

        var tab = new TwoStateImageButton(x, y, ImageButton.TAB_LEFT_ACTIVE_UV, ImageButton.TAB_LEFT_INACTIVE_UV,
                this::onTabSelected, null, text);
        tabs.put(tab, tabType);
        tab.setState(currentTab == tabType);
    }

    private void onTabSelected(TwoStateImageButton tab, boolean newValue) {
        if (!newValue) {
            tab.toggle();
            return;
        }

        for (TwoStateImageButton otherTab : tabs.keySet()) {
            otherTab.setState(tab == otherTab);
        }

        currentTab = tabs.get(tab);
        conditionList.setConditions(conditions.get(currentTab));
        currentCondition = null;
        setRightColumnVisibility(false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean pressedTab = false;
        for (TwoStateImageButton tab : tabs.keySet()) {
            pressedTab = pressedTab || tab.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button) | pressedTab;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        // Background
        renderContainer(guiGraphics);

        // Left column
        conditionSearchBox.render(guiGraphics, mouseX, mouseY, partialTicks);
        conditionList.render(guiGraphics, mouseX, mouseY, partialTicks);

        // Right column
        if (minecraft != null && priorityField.visible)
            drawScrollingString(guiGraphics, minecraft.font, Component.translatable("menu.biomebeats.priority"),
                    new Rect(boundsR.x() + ELEMENT_SPACING, boundsR.y(), boundsR.w() / 2, ELEMENT_HEIGHT), 0,
                    BiomeBeatsColor.WHITE.getHex());
        priorityField.render(guiGraphics, mouseX, mouseY, partialTicks);
        musicSearchBox.render(guiGraphics, mouseX, mouseY, partialTicks);
        musicList.render(guiGraphics, mouseX, mouseY, partialTicks);
        for (TwoStateImageButton tab : tabs.keySet()) {
            tab.render(guiGraphics, mouseX, mouseY, 0);
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        Constants.CONFIG_IO.removeListener(this);
        Constants.CONFIG_IO.saveConfig(config);
    }

    private void renderContainer(GuiGraphics guiGraphics) {
        // Corners
        drawRect(BASE_RL, guiGraphics, bounds.x(), bounds.x() + 4, bounds.y(), bounds.y() + 4,
                0, 4, 0, 4);
        drawRect(BASE_RL, guiGraphics, bounds.x2() - 4, bounds.x2(), bounds.y(), bounds.y() + 4,
                8, 12, 0, 4);
        drawRect(BASE_RL, guiGraphics, bounds.x(), bounds.x() + 4, bounds.y2() - 4, bounds.y2(),
                0, 4, 8, 12);
        drawRect(BASE_RL, guiGraphics, bounds.x2() - 4, bounds.x2(), bounds.y2() - 4, bounds.y2(),
                8, 12, 8, 12);

        // Sides
        drawRect(BASE_RL, guiGraphics, bounds.x() + 4, bounds.x2() - 4, bounds.y(), bounds.y() + 4,
                4, 8, 0, 4);
        drawRect(BASE_RL, guiGraphics, bounds.x() + 4, bounds.x2() - 4, bounds.y2() - 4, bounds.y2(),
                4, 8, 8, 12);
        drawRect(BASE_RL, guiGraphics, bounds.x(), bounds.x() + 4, bounds.y() + 4, bounds.y2() - 4,
                0, 4, 4, 8);
        drawRect(BASE_RL, guiGraphics, bounds.x2() - 4, bounds.x2(), bounds.y() + 4, bounds.y2() - 4,
                8, 12, 4, 8);

        // Inside
        drawRect(BASE_RL, guiGraphics, bounds.x() + 4, bounds.x2() - 4, bounds.y() + 4, bounds.y2() - 4,
                4, 8, 4, 8);
        drawRect(BASE_RL, guiGraphics, boundsL.x2() + 1, boundsR.x() - 1, bounds.y() + 3, bounds.y2() - 3,
                36, 39, 0, 3);
    }

    private void setRightColumnVisibility(boolean visible) {
        musicList.setVisibility(visible);
        priorityField.setVisible(visible);
        musicSearchBox.setVisible(visible);
    }

    private void updateCheckedMusicTracks() {
        musicList.setCheckedMusicTracks(musicTracksByCondition.getOrDefault(currentCondition, NO_MUSIC_TRACKS));
    }

    private void onMusicTrackToggle(MusicTrack musicTrack, boolean newValue) {
        if (newValue) {
            musicTracksByCondition.computeIfAbsent(currentCondition, c -> new HashSet<>()).add(musicTrack);
        } else if (musicTracksByCondition.containsKey(currentCondition)) {
            musicTracksByCondition.get(currentCondition).remove(musicTrack);
        }

        updateCheckedMusicTracks();
    }

    private void onConditionSelected(Condition condition) {
        currentCondition = condition;
        setRightColumnVisibility(true);
        updateCheckedMusicTracks();
    }

    private void onConditionSearchUpdate(String x) {
        //TODO
        System.out.println("Typed something in the condition search box");
    }

    private void onMusicSearchUpdate(String x) {
        //TODO
        System.out.println("Typed something in the music search box");
    }

    private void onPriorityUpdate(String priorityText) {
        if (currentCondition == null) {
            return;
        }

        int priority;
        try {
            priority = Integer.parseInt(priorityText);
        } catch (NumberFormatException e) {
            Constants.LOG.error(e.getMessage(), e);
            return;
        }

        currentCondition.setPriority(priority);
    }

    @Override
    public void beforeConfigChange(MainConfig config) {}

    @Override
    public void afterConfigChange(MainConfig config) {
        this.config = config;
        init();
    }

    private enum TabType {
        BY_BIOME,
        BY_TAG,
        BY_TIME,
        BY_OTHER,
        COMBINED
    }
}
