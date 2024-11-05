package io.github.maki99999.biomebeats.gui;

import io.github.maki99999.biomebeats.BiomeBeatsCommon;
import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.condition.BiomeCondition;
import io.github.maki99999.biomebeats.condition.CombinedCondition;
import io.github.maki99999.biomebeats.condition.Condition;
import io.github.maki99999.biomebeats.condition.NoOtherMusicCondition;
import io.github.maki99999.biomebeats.config.ConfigChangeListener;
import io.github.maki99999.biomebeats.config.MainConfig;
import io.github.maki99999.biomebeats.music.MusicGroup;
import io.github.maki99999.biomebeats.music.MusicTrack;
import io.github.maki99999.biomebeats.service.Services;
import io.github.maki99999.biomebeats.util.BiomeBeatsColor;
import io.github.maki99999.biomebeats.util.Rect;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.maki99999.biomebeats.util.DrawUtils.*;

public class ConfigScreen extends Screen implements ConfigChangeListener {
    private static final int MAX_WIDTH = 400;
    private static final int MAX_HEIGHT = 400;
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
    private LayeredImageButton folderButton;
    private LayeredImageButton reloadButton;
    private Rect bounds;
    private Rect boundsL;
    private Rect boundsR;
    private Rect addonBounds;
    private LayeredImageButton addCombinedConditionBtn;

    private Collection<MusicGroup> musicGroups;
    private Map<Condition, Collection<MusicTrack>> musicTracksByCondition;
    private Map<TabType, Collection<? extends Condition>> conditions;
    private final Map<TabType, Collection<? extends Condition>> sortedFilteredConditions = new HashMap<>();
    private final Set<String> collapsedMusicGroups = new HashSet<>();
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
            Constants.MUSIC_MANAGER.startPreviewMode();
            initData();
        }

        // Update bounds
        int w = Math.min(width - 2 * SIDES_PADDING, MAX_WIDTH);
        int h = Math.min(height, MAX_HEIGHT);
        bounds = new Rect(SIDES_PADDING + (width - 2 * SIDES_PADDING - w) / 2, (height - h) / 2, w, h);
        boundsL = new Rect(bounds.x() + BORDER_PADDING, bounds.y() + BORDER_PADDING,
                Mth.floor((bounds.w() - BORDER_PADDING * 2) * 0.4f), bounds.h() - BORDER_PADDING * 2);
        boundsR = new Rect(boundsL.x2() + BORDER_PADDING, bounds.y() + BORDER_PADDING,
                bounds.w() - BORDER_PADDING * 2 - boundsL.w() - BORDER_PADDING, bounds.h() - BORDER_PADDING * 2);
        addonBounds = Rect.fromCoordinates(bounds.x1() - 18, bounds.y2() - 42, bounds.x1() + 1, bounds.y2());

        // Left column
        conditionSearchBox = addWidget(new EditBox(font, boundsL.x(), boundsL.y(), boundsL.w(), ELEMENT_HEIGHT,
                Component.translatable("menu.biomebeats.search.condition")));
        conditionSearchBox.setHint(Component.translatable("menu.biomebeats.search.condition"));
        conditionSearchBox.setResponder(this::onConditionSearchUpdate);

        conditionList = addWidget(new ConditionList(minecraft, new Rect(boundsL.x(), boundsL.y() + ELEMENT_HEIGHT,
                boundsL.w(), boundsL.h() - ELEMENT_HEIGHT),
                Component.translatable("menu.biomebeats.search.condition"), this::onConditionSelected,
                this::openCombinedConditionScreen));
        updateCurrentConditions("");

        // Right column
        priorityField = addWidget(new EditBox(font, boundsR.x2() - 60, boundsR.y(), 60, ELEMENT_HEIGHT,
                Component.translatable("menu.biomebeats.priority")));
        priorityField.setHint(Component.literal("0"));
        priorityField.setResponder(this::onPriorityUpdate);
        priorityField.setFilter(s -> s.matches("^[+-]?\\d{1,9}$"));

        musicSearchBox = addWidget(new EditBox(font, boundsR.x(), boundsR.y() + ELEMENT_HEIGHT + ELEMENT_SPACING,
                boundsR.w(),
                ELEMENT_HEIGHT, Component.translatable("menu.biomebeats.search.music")));
        musicSearchBox.setHint(Component.translatable("menu.biomebeats.search.music"));
        musicSearchBox.setResponder(this::onMusicSearchUpdate);

        folderButton = new LayeredImageButton(addonBounds.x() + 4, addonBounds.y() + 4, BaseTextureUv.FOLDER_UV,
                this::onFolderPress, Tooltip.create(Component.translatable("menu.biomebeats.open_music_folder")));
        reloadButton = new LayeredImageButton(addonBounds.x() + 4, addonBounds.y() + 22, BaseTextureUv.RELOAD_UV,
                this::onReloadPress, Tooltip.create(Component.translatable("menu.biomebeats.reload")));

        musicList = addWidget(new MusicList(minecraft,
                new Rect(boundsR.x(), boundsR.y() + ELEMENT_HEIGHT * 2 + ELEMENT_SPACING,
                        boundsR.w(), boundsR.h() - ELEMENT_HEIGHT * 2 - ELEMENT_SPACING),
                Component.translatable("menu.biomebeats.search.music"), musicGroups,
                this::onMusicTrackToggle, this::onMusicGroupToggle));

        // Tabs
        tabs = new HashMap<>();
        addTab(TabType.BY_BIOME, Component.translatable("menu.biomebeats.by_biome"), bounds.x() - 57, bounds.y() + 4);
        addTab(TabType.BY_TAG, Component.translatable("menu.biomebeats.by_tag"), bounds.x() - 57, bounds.y() + 26);
        addTab(TabType.BY_OTHER, Component.translatable("menu.biomebeats.by_other"), bounds.x() - 57, bounds.y() + 48);
        addTab(TabType.COMBINED, Component.translatable("menu.biomebeats.combined"), bounds.x() - 57, bounds.y() + 70);

        // Combined Condition
        addCombinedConditionBtn = new LayeredImageButton(boundsL.x2() - BaseTextureUv.PLUS_UV.w() - 2,
                boundsL.y2() - BaseTextureUv.PLUS_UV.h() - 2, BaseTextureUv.PLUS_UV,
                (btn) -> openCombinedConditionScreen(null),
                Tooltip.create(Component.translatable("menu.biomebeats.add")));

        if (currentCondition == null) {
            setRightColumnVisibility(false);
        } else {
            setRightColumnVisibility(true);
            updateCheckedMusicTracks();
        }
    }

    private void openCombinedConditionScreen(CombinedCondition combinedCondition) {
        if (minecraft != null) {
            minecraft.setScreen(null);
            minecraft.setScreen(new CombinedConditionConfigScreen(this, combinedCondition,
                    conditions.values().stream().flatMap(Collection::stream).collect(Collectors.toList())));
        }
    }

    private void updateCurrentConditions(String filter) {
        String cleanFilter = filter.trim().toLowerCase();

        if (currentTab == TabType.BY_BIOME) {
            List<ResourceLocation> recentBiomesRLs = Constants.BIOME_MANAGER.getMostRecentBiomes().stream()
                    .map(holder -> holder.unwrapKey().map(ResourceKey::location).orElse(null))
                    .filter(Objects::nonNull)
                    .toList();

            sortedFilteredConditions.put(TabType.BY_BIOME, conditions.get(TabType.BY_BIOME).stream()
                    .filter(condition -> condition.getName().toLowerCase().contains(cleanFilter))
                    .sorted(Comparator
                            .comparingInt((Condition c) -> {
                                if (c instanceof BiomeCondition bc) {
                                    int index = recentBiomesRLs.indexOf(bc.getBiomeRL());
                                    return index >= 0 ? index : Integer.MAX_VALUE;
                                }
                                return Integer.MAX_VALUE;
                            })
                            .thenComparing(Condition::getName))
                    .toList());

        } else {
            sortedFilteredConditions.put(currentTab, conditions.get(currentTab).stream()
                    .filter(condition -> condition.getName().toLowerCase().contains(cleanFilter))
                    .sorted(Comparator.comparing(Condition::getName))
                    .toList());
        }
        conditionList.setConditions(sortedFilteredConditions.get(currentTab), currentCondition);
    }

    private void onReloadPress(Button imageButton) {
        Constants.CONFIG_IO.removeListener(this);
        Constants.CONFIG_IO.saveConfig(config);
        BiomeBeatsCommon.reload();
        if (minecraft != null) {
            minecraft.setScreen(new ConfigScreen());
        }
    }

    private void onFolderPress(Button imageButton) {
        Util.getPlatform().openPath(Services.PLATFORM.getModConfigFolder().resolve(Constants.MUSIC_FOLDER));
    }

    private void initData() {
        musicGroups = Constants.MUSIC_MANAGER.getMusicGroups();

        conditions = Map.ofEntries(
                Map.entry(TabType.BY_BIOME, Constants.CONDITION_MANAGER.getBiomeConditions()),
                Map.entry(TabType.BY_TAG, Constants.CONDITION_MANAGER.getTagConditions()),
                Map.entry(TabType.BY_OTHER, Constants.CONDITION_MANAGER.getOtherConditions()),
                Map.entry(TabType.COMBINED, Constants.CONDITION_MANAGER.getCombinedConditions())
        );

        musicTracksByCondition = Constants.CONDITION_MUSIC_MANAGER.getMusicTracksByCondition();
    }

    private void addTab(TabType tabType, Component text, int x, int y) {
        if (minecraft == null) return;

        var tab = new TwoStateImageButton(x, y,
                new ImageButton(x, y, BaseTextureUv.TAB_LEFT_ACTIVE_UV, null, null),
                new ImageButton(x, y, BaseTextureUv.TAB_LEFT_INACTIVE_UV, null, null),
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
        updateCurrentConditions("");
        currentCondition = null;
        conditionSearchBox.setValue("");
        conditionList.setHeight(currentTab == TabType.COMBINED
                ? boundsL.h() - 2 * ELEMENT_HEIGHT - ELEMENT_SPACING
                : boundsL.h() - ELEMENT_HEIGHT);
        setRightColumnVisibility(false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (currentTab == TabType.COMBINED && addCombinedConditionBtn.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        boolean pressedTab = false;
        for (TwoStateImageButton tab : tabs.keySet()) {
            pressedTab = pressedTab || tab.mouseClicked(mouseX, mouseY, button);
        }
        pressedTab = pressedTab || folderButton.mouseClicked(mouseX, mouseY, button);
        pressedTab = pressedTab || reloadButton.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button) | pressedTab;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        // Background
        drawContainer(guiGraphics, bounds);
        drawRect(BaseTextureUv.RL, guiGraphics,
                Rect.fromCoordinates(boundsL.x2() + 1, bounds.y() + 3, boundsR.x() - 1, bounds.y2() - 3),
                Rect.fromCoordinates(36, 0, 39, 3));

        // Addon
        drawRect(BaseTextureUv.RL, guiGraphics, new Rect(addonBounds.x(), addonBounds.y(), 4, 4),
                BaseTextureUv.CONTAINER_UV_TL);
        drawRect(BaseTextureUv.RL, guiGraphics, new Rect(addonBounds.x(), addonBounds.y2() - 4, 4, 4),
                BaseTextureUv.CONTAINER_UV_BL);
        drawRect(BaseTextureUv.RL, guiGraphics, Rect.fromCoordinates(addonBounds.x(), addonBounds.y() + 4,
                addonBounds.x() + 4, addonBounds.y2() - 4), BaseTextureUv.CONTAINER_UV_L);
        drawRect(BaseTextureUv.RL, guiGraphics, Rect.fromCoordinates(addonBounds.x() + 4, addonBounds.y(),
                addonBounds.x2(), addonBounds.y() + 4), BaseTextureUv.CONTAINER_UV_T);
        drawRect(BaseTextureUv.RL, guiGraphics, Rect.fromCoordinates(addonBounds.x() + 4, addonBounds.y2() - 4,
                addonBounds.x2() + 2, addonBounds.y2()), BaseTextureUv.CONTAINER_UV_B);
        drawRect(BaseTextureUv.RL, guiGraphics, Rect.fromCoordinates(addonBounds.x() + 4, addonBounds.y() + 3,
                addonBounds.x2() + 2, addonBounds.y2() - 4), BaseTextureUv.CONTAINER_UV_C);
        folderButton.render(guiGraphics, mouseX, mouseY, 0);
        reloadButton.render(guiGraphics, mouseX, mouseY, 0);

        // Left column
        conditionSearchBox.render(guiGraphics, mouseX, mouseY, partialTick);
        conditionList.render(guiGraphics, mouseX, mouseY, partialTick);

        // Right column
        if (minecraft != null && priorityField.visible)
            drawScrollingString(guiGraphics, minecraft.font, Component.translatable("menu.biomebeats.priority"),
                    new Rect(boundsR.x() + ELEMENT_SPACING, boundsR.y(), boundsR.w() / 2, ELEMENT_HEIGHT), 0,
                    BiomeBeatsColor.WHITE.getHex());
        priorityField.render(guiGraphics, mouseX, mouseY, partialTick);
        musicSearchBox.render(guiGraphics, mouseX, mouseY, partialTick);
        musicList.render(guiGraphics, mouseX, mouseY, partialTick);
        for (TwoStateImageButton tab : tabs.keySet()) {
            tab.render(guiGraphics, mouseX, mouseY, 0);
        }

        // Combined Conditions
        if (currentTab == TabType.COMBINED) {
            addCombinedConditionBtn.render(guiGraphics, mouseX, mouseY, 0);
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        musicList.onClose();
        Constants.MUSIC_MANAGER.stopPreviewMode();
        Constants.CONFIG_IO.removeListener(this);
        Constants.CONFIG_IO.saveConfig(config);
    }

    private void setRightColumnVisibility(boolean visible) {
        musicList.setVisibility(visible);
        priorityField.setVisible(visible && !(currentCondition instanceof NoOtherMusicCondition));
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

    private void onMusicGroupToggle(String musicGroup, boolean newValue) {
        if (newValue) {
            collapsedMusicGroups.add(musicGroup);
        } else {
            collapsedMusicGroups.remove(musicGroup);
        }

        musicList.sortAndFilterMusicTracks(musicSearchBox.getValue().trim().toLowerCase(),
                musicTracksByCondition.getOrDefault(currentCondition, NO_MUSIC_TRACKS), collapsedMusicGroups);
    }

    private void onConditionSelected(Condition condition) {
        currentCondition = condition;
        priorityField.setVisible(!(condition instanceof NoOtherMusicCondition));
        priorityField.setValue("" + condition.getPriority());
        setRightColumnVisibility(true);
        updateCheckedMusicTracks();
    }

    private void onConditionSearchUpdate(String text) {
        updateCurrentConditions(text);
    }

    private void onMusicSearchUpdate(String text) {
        musicList.sortAndFilterMusicTracks(text.trim().toLowerCase(),
                musicTracksByCondition.getOrDefault(currentCondition, NO_MUSIC_TRACKS), collapsedMusicGroups);
    }

    private void onPriorityUpdate(String priorityText) {
        if (currentCondition == null) {
            return;
        }

        int priority;
        try {
            priority = Integer.parseInt(priorityText);
        } catch (NumberFormatException e) {
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

    public void updateCombinedCondition(CombinedCondition oldCondition, CombinedCondition condition) {
        if (oldCondition == null && condition != null && !condition.isEmpty()) {
            Constants.CONDITION_MANAGER.addCombinedCondition(condition);
        } else if (oldCondition != null && condition != null) {
            Constants.CONDITION_MANAGER.updateCombinedCondition(oldCondition, condition);
        } else if (oldCondition != null) {
            Constants.CONDITION_MANAGER.removeCombinedCondition(oldCondition);
        }
        currentCondition = null;
        init();
    }

    private enum TabType {
        BY_BIOME,
        BY_TAG,
        BY_OTHER,
        COMBINED
    }
}
