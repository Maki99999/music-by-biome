package io.github.maki99999.biomebeats.gui;

import io.github.maki99999.biomebeats.BiomeBeatsCommon;
import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.condition.BiomeCondition;
import io.github.maki99999.biomebeats.condition.CombinedCondition;
import io.github.maki99999.biomebeats.condition.Condition;
import io.github.maki99999.biomebeats.condition.NoOtherMusicCondition;
import io.github.maki99999.biomebeats.config.ConfigChangeListener;
import io.github.maki99999.biomebeats.config.MainConfig;
import io.github.maki99999.biomebeats.gui.common.*;
import io.github.maki99999.biomebeats.gui.conditionlist.ConditionList;
import io.github.maki99999.biomebeats.gui.musiclist.MusicList;
import io.github.maki99999.biomebeats.gui.util.Point;
import io.github.maki99999.biomebeats.music.MusicGroup;
import io.github.maki99999.biomebeats.music.MusicTrack;
import io.github.maki99999.biomebeats.service.Services;
import io.github.maki99999.biomebeats.gui.util.BiomeBeatsColor;
import io.github.maki99999.biomebeats.gui.util.Rect;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.maki99999.biomebeats.gui.util.DrawUtils.*;

public class ConfigScreen extends UiElement implements ConfigChangeListener {
    private static final int MAX_WIDTH = 400;
    private static final int MAX_HEIGHT = 400;
    private static final int SIDES_PADDING = 60;
    private static final int BORDER_PADDING = 4;
    private static final int ELEMENT_HEIGHT = 17;
    private static final int ELEMENT_SPACING = 4;
    private static final Collection<MusicTrack> NO_MUSIC_TRACKS = Set.of();
    private final Map<TabType, Collection<? extends Condition>> sortedFilteredConditions = new HashMap<>();
    private final Set<String> collapsedMusicGroups = new HashSet<>();
    private MainConfig config;
    private Map<TwoStateImageButton, TabType> tabs;
    private ConditionList conditionList;
    private MusicList musicList;
    private EditBoxWrapper conditionSearchBox;
    private EditBoxWrapper musicSearchBox;
    private EditBoxWrapper priorityField;
    private Rect bounds;
    private Rect boundsL;
    private Rect boundsR;
    private Rect addonBounds;
    private LayeredImageButton addCombinedConditionBtn;
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
            Constants.MUSIC_MANAGER.startPreviewMode();
            initData();
        }

        if (getMinecraft() == null) {
            return;
        }

        // Update bounds
        int w = Math.min(getWidth() - 2 * SIDES_PADDING, MAX_WIDTH);
        int h = Math.min(getHeight(), MAX_HEIGHT);
        bounds = new Rect(SIDES_PADDING + (getWidth() - 2 * SIDES_PADDING - w) / 2, (getHeight() - h) / 2, w, h);
        boundsL = new Rect(bounds.x() + BORDER_PADDING, bounds.y() + BORDER_PADDING,
                Mth.floor((bounds.w() - BORDER_PADDING * 2) * 0.4f), bounds.h() - BORDER_PADDING * 2);
        boundsR = new Rect(boundsL.x2() + BORDER_PADDING, bounds.y() + BORDER_PADDING,
                bounds.w() - BORDER_PADDING * 2 - boundsL.w() - BORDER_PADDING, bounds.h() - BORDER_PADDING * 2);
        addonBounds = Rect.fromCoordinates(bounds.x1() - 18, bounds.y2() - 78, bounds.x1() + 1, bounds.y2());

        // Left column
        conditionSearchBox = addChild(new EditBoxWrapper(Component.translatable("menu.biomebeats.search.condition"),
                new Rect(boundsL.x(), boundsL.y(), boundsL.w(), ELEMENT_HEIGHT)));
        conditionSearchBox.setHint(Component.translatable("menu.biomebeats.search.condition"));
        conditionSearchBox.setResponder(this::onConditionSearchUpdate);

        conditionList = addChild(new ConditionList(getMinecraft(), new Rect(boundsL.x(), boundsL.y() + ELEMENT_HEIGHT,
                boundsL.w(), boundsL.h() - ELEMENT_HEIGHT),
                Component.translatable("menu.biomebeats.search.condition"), this::onConditionSelected,
                this::openCombinedConditionScreen));
        updateCurrentConditions("");

        // Right column
        priorityField = addChild(new EditBoxWrapper(Component.translatable("menu.biomebeats.priority"),
                new Rect(boundsR.x2() - 60, boundsR.y(), 60, ELEMENT_HEIGHT)));
        priorityField.setHint(Component.literal("0"));
        priorityField.setResponder(this::onPriorityUpdate);
        priorityField.setFilter(s -> s.matches("^[+-]?\\d{1,9}$"));

        musicSearchBox = addChild(new EditBoxWrapper(Component.translatable("menu.biomebeats.search.music"),
                new Rect(boundsR.x(), boundsR.y() + ELEMENT_HEIGHT + ELEMENT_SPACING, boundsR.w(), ELEMENT_HEIGHT)));
        musicSearchBox.setHint(Component.translatable("menu.biomebeats.search.music"));
        musicSearchBox.setResponder(this::onMusicSearchUpdate);

        addChild(new LayeredImageButton(Component.translatable("menu.biomebeats.open_music_folder"),
                                        Component.translatable("menu.biomebeats.open_music_folder"),
                                         addonBounds.x() + 4,
                                         addonBounds.y() + 4,
                                        BaseTextureUv.FOLDER_UV,
                                        this::onFolderPress));
        addChild(new LayeredImageButton(Component.translatable("menu.biomebeats.reload"),
                                        Component.translatable("menu.biomebeats.reload"),
                                         addonBounds.x() + 4,
                                         addonBounds.y() + 22,
                                        BaseTextureUv.RELOAD_UV,
                                        this::onReloadPress));
        addChild(new LayeredImageButton(Component.translatable("menu.biomebeats.settings"),
                                        Component.translatable("menu.biomebeats.settings"),
                                         addonBounds.x() + 4,
                                         addonBounds.y() + 40,
                                        BaseTextureUv.SETTINGS_UV,
                                        this::onSettingsPress));
        addChild(new LayeredImageButton(Component.translatable("menu.biomebeats.help"),
                                        Component.translatable("menu.biomebeats.help"),
                                         addonBounds.x() + 4,
                                         addonBounds.y() + 58,
                                        BaseTextureUv.HELP_UV,
                                        this::onHelpPress));

        musicList = addChild(new MusicList(
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
        addCombinedConditionBtn = addChild(new LayeredImageButton(Component.translatable("menu.biomebeats.add"),
                                                                  Component.translatable("menu.biomebeats.add"),
                                                                   boundsL.x2() - BaseTextureUv.PLUS_UV.w() - 2,
                                                                   boundsL.y2() - BaseTextureUv.PLUS_UV.h() - 2,
                                                                  BaseTextureUv.PLUS_UV,
                                                                  (btn) -> openCombinedConditionScreen(null)));
        addCombinedConditionBtn.setVisible(false);

        if (currentCondition == null) {
            onTabChanged();
        } else {
            setRightColumnVisibility(true);
            updateCheckedMusicTracks();
            onMusicSearchUpdate("");
        }
    }

    private void openCombinedConditionScreen(CombinedCondition combinedCondition) {
        if (getMinecraft() != null) {
            getMinecraft().setScreen(null);
            getMinecraft().setScreen(
                    new ForwardingScreen<>(
                            new CombinedConditionConfigScreen(this,
                                                              combinedCondition,
                                                              conditions.values()
                                                                        .stream()
                                                                        .flatMap(Collection::stream)
                                                                        .collect(Collectors.toList())
                            )
                    )
            );
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
                                    int index = recentBiomesRLs.indexOf(bc.getBiomeRl());
                                    return index >= 0 ? index : Integer.MAX_VALUE;
                                }
                                return Integer.MAX_VALUE;
                            })
                            .thenComparing((Condition c) ->
                                    musicTracksByCondition.getOrDefault(c, Collections.emptyList()).isEmpty())
                            .thenComparing(Condition::getName))
                    .toList());

        } else {
            sortedFilteredConditions.put(currentTab, conditions.get(currentTab).stream()
                    .filter(condition -> condition.getName().toLowerCase().contains(cleanFilter))
                    .sorted(
                            Comparator.comparing(Condition::isConditionMet).reversed()
                                    .thenComparing((Condition c) ->
                                            musicTracksByCondition.getOrDefault(c, Collections.emptyList()).isEmpty())
                                    .thenComparing(Comparator.comparing(Condition::getPriority).reversed())
                                    .thenComparing(Condition::getName)
                    )
                    .toList());
        }
        conditionList.setConditions(sortedFilteredConditions.get(currentTab), currentCondition);
    }

    private void onReloadPress(Button imageButton) {
        Constants.CONFIG_IO.removeListener(this);
        Constants.CONFIG_IO.saveConfig(config);
        BiomeBeatsCommon.reload();
        if (getMinecraft() != null) {
            getMinecraft().setScreen(new ForwardingScreen<>(new ConfigScreen()));
        }
    }

    private void onSettingsPress(Button button) {
        if (getMinecraft() != null) {
            getMinecraft().setScreen(new ForwardingScreen<>(new GeneralConfigScreen(this)));
        }
    }

    private void onHelpPress(Button button) {
        Util.getPlatform().openUri(Constants.HELP_URL);
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
        if (getMinecraft() == null) {
            return;
        }

        TwoStateImageButton tab = addChild(new TwoStateImageButton(
                text, null, x, y,
                new ImageButton(null, null, x, y, BaseTextureUv.TAB_LEFT_ACTIVE_UV, null),
                new ImageButton(null, null, x, y, BaseTextureUv.TAB_LEFT_INACTIVE_UV, null),
                this::onTabSelected, true
        ));
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
        onTabChanged();
    }

    private void onTabChanged() {
        addCombinedConditionBtn.setVisible(currentTab == TabType.COMBINED);
        updateCurrentConditions("");
        currentCondition = null;
        conditionSearchBox.setValue("");
        conditionList.setHeight(currentTab == TabType.COMBINED
                ? boundsL.h() - 2 * ELEMENT_HEIGHT - ELEMENT_SPACING
                : boundsL.h() - ELEMENT_HEIGHT);
        setRightColumnVisibility(false);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, Point mousePos, float partialTick) {
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

        // Right column
        if (getMinecraft() != null && priorityField.isVisible())
            drawScrollingString(guiGraphics, getMinecraft().font, Component.translatable("menu.biomebeats.priority"),
                    new Rect(boundsR.x() + ELEMENT_SPACING, boundsR.y(), boundsR.w() / 2, ELEMENT_HEIGHT),
                                BiomeBeatsColor.WHITE.getHex());
    }

    @Override
    public void onClose() {
        Constants.MUSIC_MANAGER.stopPreviewMode();
        Constants.CONFIG_IO.removeListener(this);
        Constants.CONFIG_IO.saveConfig(config);
    }

    private void setRightColumnVisibility(boolean visible) {
        musicList.setVisible(visible);
        priorityField.setVisible(visible && !(currentCondition instanceof NoOtherMusicCondition));
        musicSearchBox.setVisible(visible);
    }

    private void updateCheckedMusicTracks() {
        musicList.setCheckedMusicTracks(musicTracksByCondition.getOrDefault(currentCondition, NO_MUSIC_TRACKS));
    }

    private void onMusicTrackToggle(MusicTrack musicTrack, boolean newValue) {
        if (newValue) {
            Constants.CONDITION_MUSIC_MANAGER.addTrackToCondition(currentCondition.getId(), musicTrack);
            musicTracksByCondition.computeIfAbsent(currentCondition, c -> new HashSet<>()).add(musicTrack);
        } else if (musicTracksByCondition.containsKey(currentCondition)) {
            Constants.CONDITION_MUSIC_MANAGER.removeTrackToCondition(currentCondition.getId(), musicTrack);
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
        onMusicSearchUpdate("");
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

    public void returnToThisScreen() {
        currentCondition = null;

        if (this.getMinecraft() != null) {
            this.getMinecraft().setScreen(new ForwardingScreen<>(this));
        }
    }

    private enum TabType {
        BY_BIOME,
        BY_TAG,
        BY_OTHER,
        COMBINED
    }
}
