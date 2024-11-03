package io.github.maki99999.biomebeats.condition;

import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.config.CombinedConditionConfig;
import io.github.maki99999.biomebeats.config.ConditionConfig;
import io.github.maki99999.biomebeats.config.ConfigChangeListener;
import io.github.maki99999.biomebeats.config.MainConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

import static io.github.maki99999.biomebeats.util.BiomeUtils.getBiomeRLs;
import static io.github.maki99999.biomebeats.util.BiomeUtils.getBiomeTagKeys;

public class ConditionManager implements ConditionChangeListener, ConfigChangeListener {
    private final Collection<ActiveConditionsListener> activeConditionsListener = new HashSet<>();
    private final Collection<Condition> activeConditions = new HashSet<>();
    private final Collection<Condition> conditions = new HashSet<>();

    private Collection<BiomeCondition> biomeConditions;
    private Collection<TagCondition> tagConditions;
    private Collection<Condition> otherConditions;
    private Collection<CombinedCondition> combinedConditions = List.of();
    private Map<String, Collection<CombinedCondition>> combinedConditionMappings;

    private boolean firstTickWithLevel = true;

    public Collection<? extends Condition> getTagConditions() {
        return tagConditions;
    }

    public Collection<? extends Condition> getBiomeConditions() {
        return biomeConditions;
    }

    public Collection<Condition> getOtherConditions() {
        return otherConditions;
    }

    public Collection<CombinedCondition> getCombinedConditions() {
        return combinedConditions;
    }

    public void init() {
        Constants.CONFIG_IO.addListener(this);
        initOtherConditions();
    }

    private void initOtherConditions() {
        otherConditions = new ArrayList<>();

        otherConditions.add(new ScreenCondition("MainMenu", "In Main Menu", null));
        otherConditions.add(new ScreenCondition("WinScreen", "In Win Screen", WinScreen.class));
        otherConditions.add(new DayTimeCondition(true));
        otherConditions.add(new DayTimeCondition(false));
        otherConditions.add(new BossOverlayWithMusicCondition());
        otherConditions.add(new IsUnderWaterCondition());
        otherConditions.add(new InGameModeCondition(GameType.CREATIVE));
        otherConditions.add(new InGameModeCondition(GameType.SPECTATOR));
        otherConditions.add(new NoOtherMusicCondition());

        otherConditions.forEach(condition -> condition.addListener(this));
        addConditions(otherConditions);
    }

    private void addConditions(Collection<? extends Condition> conditions) {
        this.conditions.addAll(conditions);
        mapToCombinedCondition(conditions);
        conditions.stream().filter(Condition::isConditionMet).forEach(activeConditions::add);
    }

    private void mapToCombinedCondition(Collection<? extends Condition> conditions) {
        for (Condition condition : conditions) {
            String conditionId = condition.getId();
            if (combinedConditionMappings != null && combinedConditionMappings.containsKey(conditionId)) {
                combinedConditionMappings.get(conditionId).forEach(c -> c.addCondition(condition));
                combinedConditionMappings.remove(conditionId);
            }
        }
    }

    private void initBiomeConditions(@NotNull Level level) {
        biomeConditions = BiomeCondition.toConditions(getBiomeRLs(level), this);
        Constants.BIOME_MANAGER.addBiomeChangeListeners(biomeConditions);
        addConditions(biomeConditions);

        tagConditions = TagCondition.toFilteredConditions(getBiomeTagKeys(level), this);
        Constants.BIOME_MANAGER.addBiomeChangeListeners(tagConditions);
        addConditions(tagConditions);
    }

    public void resetConditions() {
        biomeConditions = null;
        tagConditions = null;

        conditions.clear();
        activeConditions.clear();

        initOtherConditions();
        firstTickWithLevel = true;
        tick();

        for (ActiveConditionsListener listener : activeConditionsListener) {
            listener.onActiveConditionsChanged(activeConditions);
        }
    }

    public void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (firstTickWithLevel && minecraft.level != null) {
            firstTickWithLevel = false;
            initBiomeConditions(minecraft.level);
            Constants.CONFIG_IO.updateConfigListeners();
        }
    }

    @Override
    public void onConditionChanged(Condition condition) {
        if (condition.isConditionMet()) {
            activeConditions.add(condition);
        } else {
            activeConditions.remove(condition);
        }

        for (ActiveConditionsListener listener : activeConditionsListener) {
            listener.onActiveConditionsChanged(activeConditions);
        }
    }

    public void addListener(ActiveConditionsListener listener) {
        activeConditionsListener.add(listener);
    }

    public Collection<Condition> getConditions() {
        return conditions;
    }

    @Override
    public void beforeConfigChange(MainConfig config) {
        Collection<CombinedConditionConfig> combinedConditionConfigs = new ArrayList<>();
        for (CombinedCondition condition : combinedConditions) {
            combinedConditionConfigs.add(new CombinedConditionConfig(condition.getUuid(), condition.getName(),
                    condition.getDescription(), condition.getConditions().stream().map(Condition::getId).toList()));
        }
        config.setCombinedConditionConfigs(combinedConditionConfigs);

        Map<String, ConditionConfig> conditionConfigById = config.getConditionConfigById();
        for (Condition condition : conditions) {
            ConditionConfig conditionConfig = conditionConfigById.computeIfAbsent(condition.getId(),
                    k -> new ConditionConfig());
            conditionConfig.setPriority(condition.getPriority());
        }
    }

    @Override
    public void afterConfigChange(MainConfig config) {
        conditions.removeAll(combinedConditions);
        combinedConditions = new ArrayList<>();
        combinedConditionMappings = new HashMap<>();
        for (CombinedConditionConfig conditionConfig : config.getCombinedConditionConfigs()) {
            CombinedCondition combinedCondition = new CombinedCondition(conditionConfig.getUuid(),
                    conditionConfig.getName(), conditionConfig.getDescription());
            combinedCondition.addListener(this);
            combinedConditions.add(combinedCondition);
            conditions.add(combinedCondition);

            for (String conditionId : conditionConfig.getConditionIds()) {
                combinedConditionMappings.computeIfAbsent(conditionId, id -> new ArrayList<>()).add(combinedCondition);
            }
            mapToCombinedCondition(conditions);
        }

        Map<String, ConditionConfig> conditionConfigById = config.getConditionConfigById();
        for (Condition condition : conditions) {
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
        Condition boss = findCondition(c -> c instanceof BossOverlayWithMusicCondition);

        CombinedCondition endBoss = new CombinedCondition();
        endBoss.setName("End Boss");
        endBoss.setDescription("Default Configuration");
        endBoss.addCondition(isEnd);
        endBoss.addCondition(boss);
        endBoss.setPriority(4);
        addCombinedCondition(endBoss);

        if (isEnd != null) isEnd.setPriority(3);

        Condition underwater = findCondition(c -> c instanceof IsUnderWaterCondition);
        Condition playsUnderWaterMusic = findCondition(c -> c instanceof TagCondition tagCondition
                && tagCondition.getName().equals("Plays Underwater Music"));

        CombinedCondition underwaterMusicCondition = new CombinedCondition();
        underwaterMusicCondition.setName("Is Under Water in Underwater Biome");
        underwaterMusicCondition.setDescription("Default Configuration");
        underwaterMusicCondition.addCondition(underwater);
        underwaterMusicCondition.addCondition(playsUnderWaterMusic);
        underwaterMusicCondition.setPriority(2);
        addCombinedCondition(underwaterMusicCondition);

        findCondition(c -> c instanceof ScreenCondition screenCondition
                && Objects.equals(screenCondition.getScreen(), WinScreen.class)).setPriority(6);
        findCondition(c -> c instanceof ScreenCondition screenCondition
                && screenCondition.getScreen() == null).setPriority(5);
        findCondition(c -> c instanceof InGameModeCondition inGameModeCondition
                && inGameModeCondition.getName().contains("Creative")).setPriority(1);
    }

    public Condition findCondition(Predicate<? super Condition> predicate) {
        return conditions.stream().filter(predicate).findAny().orElse(null);
    }

    public void addCombinedCondition(CombinedCondition combinedCondition) {
        conditions.add(combinedCondition);
        combinedConditions.add(combinedCondition);
        combinedCondition.addListener(this);
    }

    public void updateCombinedCondition(CombinedCondition oldCombinedCondition, CombinedCondition combinedCondition) {
        conditions.remove(oldCombinedCondition);
        combinedConditions.remove(oldCombinedCondition);
        conditions.add(combinedCondition);
        combinedConditions.add(combinedCondition);
    }

    public void removeCombinedCondition(CombinedCondition combinedCondition) {
        conditions.remove(combinedCondition);
        combinedConditions.remove(combinedCondition);
        combinedCondition.removeListener(this);
    }
}
