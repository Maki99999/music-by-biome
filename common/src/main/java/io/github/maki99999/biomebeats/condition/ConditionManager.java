package io.github.maki99999.biomebeats.condition;

import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.config.CombinedConditionConfig;
import io.github.maki99999.biomebeats.config.ConditionConfig;
import io.github.maki99999.biomebeats.config.ConfigChangeListener;
import io.github.maki99999.biomebeats.config.MainConfig;
import io.github.maki99999.biomebeats.event.ConditionChangeEvent;
import io.github.maki99999.biomebeats.util.EventBus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static io.github.maki99999.biomebeats.util.BiomeUtils.getBiomeRLs;
import static io.github.maki99999.biomebeats.util.BiomeUtils.getBiomeTagKeys;

public class ConditionManager implements ConfigChangeListener {
    private static final Map<String, Condition> CONDITIONS = new HashMap<>();
    private static final Map<ConditionType, Collection<Condition>> CONDITIONS_BY_TYPE = new HashMap<>();
    private static final Collection<CombinedCondition> COMBINED_CONDITIONS = new ArrayList<>();
    public static final String END_BOSS_COMB_COND = "End Boss";

    public static void createCondition(String id, Supplier<Condition> factory) {
        CONDITIONS.computeIfAbsent(id, key -> {
            Condition condition = factory.get();
            if (condition instanceof CombinedCondition combinedCondition) {
                COMBINED_CONDITIONS.add(combinedCondition);
            } else {
                CONDITIONS_BY_TYPE.computeIfAbsent(condition.getType(), key2 -> new ArrayList<>()).add(condition);
            }
            return condition;
        });
    }

    public static Condition getCondition(String id) {
        return CONDITIONS.getOrDefault(id, null);
    }

    private final Collection<ActiveConditionsListener> activeConditionsListener = new HashSet<>();
    private final Collection<Condition> activeConditions = new HashSet<>();

    private boolean firstTickWithLevel = true;
    private boolean needsToNotifyListeners = true;

    public ConditionManager() {
        EventBus.subscribe(ConditionChangeEvent.class, e -> onConditionChanged(e.condition()));
    }

    public Collection<? extends Condition> getTagConditions() {
        return CONDITIONS_BY_TYPE.get(ConditionType.TAG);
    }

    public Collection<? extends Condition> getBiomeConditions() {
        return CONDITIONS_BY_TYPE.get(ConditionType.BIOME);
    }

    public Collection<? extends Condition> getOtherConditions() {
        return CONDITIONS_BY_TYPE.get(ConditionType.OTHER);
    }

    public Collection<CombinedCondition> getCombinedConditions() {
        return COMBINED_CONDITIONS;
    }

    public void init() {
        initOtherConditions();
        Constants.CONFIG_IO.addListener(this);
    }

    private void initOtherConditions() {
        createCondition(ScreenCondition.MAIN_MENU, () -> new ScreenCondition(ScreenCondition.MAIN_MENU, "In Main Menu", null));
        createCondition(ScreenCondition.WIN_SCREEN, () -> new ScreenCondition(ScreenCondition.WIN_SCREEN, "In Win Screen", WinScreen.class));
        createCondition(DayTimeCondition.IS_DAY, () -> new DayTimeCondition(true));
        createCondition(DayTimeCondition.IS_NIGHT, () -> new DayTimeCondition(false));
        createCondition(BossOverlayWithMusicCondition.ID, BossOverlayWithMusicCondition::new);
        createCondition(IsUnderWaterCondition.ID, IsUnderWaterCondition::new);
        createCondition(InGameModeCondition.getId(GameType.CREATIVE), () -> new InGameModeCondition(GameType.CREATIVE));
        createCondition(InGameModeCondition.getId(GameType.SPECTATOR), () -> new InGameModeCondition(GameType.SPECTATOR));
        createCondition(NoOtherMusicCondition.ID, NoOtherMusicCondition::new);
    }

    private void initBiomeConditions(@NotNull Level level) {
        for (ResourceLocation rl : getBiomeRLs(level)) {
            createCondition(BiomeCondition.getId(rl), () -> {
                BiomeCondition biomeCondition = new BiomeCondition(rl);
                Constants.BIOME_MANAGER.addBiomeChangeListener(biomeCondition);
                return biomeCondition;
            });
        }

        initTagConditions(getBiomeTagKeys(level));
    }

    private static void initTagConditions(Collection<TagKey<Biome>> biomeTagKeys) {
        Map<String, Collection<TagKey<Biome>>> tagKeysByName = new HashMap<>();

        for (TagKey<Biome> tagKey : biomeTagKeys) {
            String path = tagKey.location().getPath();
            if (Arrays.stream(new String[]{"is_", "plays_"}).anyMatch(path::startsWith))
                tagKeysByName.computeIfAbsent(path, k -> new ArrayList<>()).add(tagKey);
        }

        var keys = new HashSet<>(tagKeysByName.keySet());

        for (String key : keys) {
            if (key.contains("/")) {
                String baseKey = key.substring(0, key.indexOf('/'));

                if (tagKeysByName.containsKey(baseKey)) {
                    tagKeysByName.get(baseKey).addAll(tagKeysByName.get(key));
                    tagKeysByName.remove(key);
                }
            }
        }

        for (Collection<TagKey<Biome>> tagKeys : tagKeysByName.values()) {
            createCondition(TagCondition.getId(tagKeys), () -> {
                TagCondition tagCondition = new TagCondition(tagKeys);
                Constants.BIOME_MANAGER.addBiomeChangeListener(tagCondition);
                return tagCondition;
            });
        }
    }

    public void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (firstTickWithLevel && minecraft.level != null) {
            firstTickWithLevel = false;
            initBiomeConditions(minecraft.level);
            Constants.CONFIG_IO.updateConfigListeners();
        }

        if (needsToNotifyListeners) {
            needsToNotifyListeners = false;

            for (ActiveConditionsListener listener : activeConditionsListener) {
                listener.onActiveConditionsChanged(activeConditions);
            }
        }
    }

    public void onConditionChanged(Condition condition) {
        if (condition.isConditionMet()) {
            activeConditions.add(condition);
        } else {
            activeConditions.remove(condition);
        }

        needsToNotifyListeners = true;
    }

    public void addListener(ActiveConditionsListener listener) {
        activeConditionsListener.add(listener);
    }

    public Collection<Condition> getConditions() {
        return CONDITIONS.values();
    }

    @Override
    public void beforeConfigChange(MainConfig config) {
        Collection<CombinedConditionConfig> combinedConditionConfigs = new ArrayList<>();
        for (CombinedCondition condition : COMBINED_CONDITIONS) {
            combinedConditionConfigs.add(new CombinedConditionConfig(condition.getId(), condition.getName(),
                    condition.getDescription(), condition.getConditionIds()));
        }
        config.setCombinedConditionConfigs(combinedConditionConfigs);

        Map<String, ConditionConfig> conditionConfigById = config.getConditionConfigById();
        for (Condition condition : CONDITIONS.values()) {
            ConditionConfig conditionConfig = conditionConfigById.computeIfAbsent(condition.getId(),
                    k -> new ConditionConfig());
            conditionConfig.setPriority(condition.getPriority());
        }
    }

    @Override
    public void afterConfigChange(MainConfig config) {
        for (var cond : COMBINED_CONDITIONS) {
            CONDITIONS.remove(cond.getId());
        }

        for (CombinedConditionConfig conditionConfig : config.getCombinedConditionConfigs()) {
            createCondition(conditionConfig.getUuid(), () -> new CombinedCondition(conditionConfig.getUuid(),
                    conditionConfig.getName(), conditionConfig.getDescription(), conditionConfig.getConditionIds()));
        }

        Map<String, ConditionConfig> conditionConfigById = config.getConditionConfigById();
        for (Condition condition : CONDITIONS.values()) {
            if (conditionConfigById.containsKey(condition.getId())) {
                ConditionConfig conditionConfig = conditionConfigById.get(condition.getId());
                condition.setPriority(conditionConfig.getPriority());
            }
        }

        for (ActiveConditionsListener listener : activeConditionsListener) {
            listener.onActiveConditionsChanged(activeConditions);
        }

        if (config.isNewConfig()) {
            addDefaultConfig();
        }
    }

    private void addDefaultConfig() {
        Condition isEnd = findCondition(c -> c instanceof TagCondition tagCondition
                && tagCondition.getName().equals("Is End"));

        if (isEnd != null) {
            CombinedCondition endBoss = new CombinedCondition(END_BOSS_COMB_COND, "Default Configuration", List.of(isEnd.getId(), BossOverlayWithMusicCondition.ID));
            endBoss.setPriority(4);
            addCondition(endBoss);

            isEnd.setPriority(3);
        } else {
            Constants.LOG.warn("Missing 'Is End' condition");
        }

        Condition playsUnderWaterMusic = findCondition(c -> c instanceof TagCondition tagCondition
                && tagCondition.getName().equals("Plays Underwater Music"));

        if (playsUnderWaterMusic != null) {
            playsUnderWaterMusic.setPriority(2);
        } else {
            Constants.LOG.warn("Missing 'Plays Underwater Music' condition");
        }

        findCondition(c -> c instanceof IsUnderWaterCondition).setPriority(2);
        findCondition(c -> c instanceof ScreenCondition screenCondition
                && Objects.equals(screenCondition.getScreen(), WinScreen.class)).setPriority(6);
        findCondition(c -> c instanceof ScreenCondition screenCondition
                && screenCondition.getScreen() == null).setPriority(5);
        findCondition(c -> c instanceof InGameModeCondition inGameModeCondition
                && inGameModeCondition.getName().contains("Creative")).setPriority(1);
    }

    public Condition findCondition(Predicate<? super Condition> predicate) {
        return CONDITIONS.values().stream().filter(predicate).findAny().orElse(null);
    }

    public void addCondition(Condition combinedCondition) {
        createCondition(combinedCondition.getId(), () -> combinedCondition);
    }

    public void updateCombinedCondition(CombinedCondition combinedCondition, String name, String description, Collection<String> conditionIds) {
        combinedCondition.setName(name);
        combinedCondition.setDescription(description);
        combinedCondition.setConditionIds(conditionIds);
    }

    public void removeCombinedCondition(CombinedCondition combinedCondition) {
        combinedCondition.dispose();
        CONDITIONS.remove(combinedCondition.getId());
        COMBINED_CONDITIONS.remove(combinedCondition);
    }

    public boolean isConditionMet(String conditionId) {
        if (CONDITIONS.containsKey(conditionId)) {
            return CONDITIONS.get(conditionId).isConditionMet();
        }

        return false;  // condition doesn't exist
    }
}
