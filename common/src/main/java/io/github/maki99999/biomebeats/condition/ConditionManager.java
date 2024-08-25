package io.github.maki99999.biomebeats.condition;

import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.config.ConditionConfig;
import io.github.maki99999.biomebeats.config.ConfigChangeListener;
import io.github.maki99999.biomebeats.config.MainConfig;
import io.github.maki99999.biomebeats.util.BiomeChangeListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import static io.github.maki99999.biomebeats.util.BiomeUtils.getBiomeRLs;
import static io.github.maki99999.biomebeats.util.BiomeUtils.getBiomeTagKeys;

public class ConditionManager implements ConditionChangeListener, ConfigChangeListener {
    private final Collection<BiomeChangeListener> biomeChangeListener = new HashSet<>();
    private final Collection<ActiveConditionsListener> activeConditionsListener = new HashSet<>();
    private final Collection<Condition> activeConditions = new HashSet<>();

    private Collection<Condition> conditions;
    private Collection<BiomeCondition> biomeConditions;
    private Collection<TagCondition> tagConditions;
    private Collection<Condition> otherConditions;

    private Holder<Biome> lastBiome;

    public Collection<? extends Condition> getTagConditions() {
        return tagConditions;
    }

    public Collection<? extends Condition> getBiomeConditions() {
        return biomeConditions;
    }

    public Collection<Condition> getOtherConditions() {
        return otherConditions;
    }


    public void init() {
        Constants.CONFIG_IO.addListener(this);

        conditions = new HashSet<>();

        otherConditions = new ArrayList<>();
        otherConditions.add(new MenuCondition("MainMenu", "Main Menu", null));
        otherConditions.forEach(condition -> condition.addListener(this));
        conditions.addAll(otherConditions);
    }

    public void initWithLevel() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            Constants.LOG.error("minecraft.level is null");
            return;
        }

        biomeConditions = BiomeCondition.toConditions(getBiomeRLs(minecraft.level), this);
        biomeChangeListener.addAll(biomeConditions);
        conditions.addAll(biomeConditions);

        tagConditions = TagCondition.toFilteredConditions(getBiomeTagKeys(minecraft.level), this);
        biomeChangeListener.addAll(tagConditions);
        conditions.addAll(tagConditions);
    }

    public void tick() {
        Minecraft minecraft = Minecraft.getInstance();

        checkBiomeChange(minecraft.level, minecraft.player);
    }

    private void checkBiomeChange(ClientLevel level, LocalPlayer player) {
        if (level != null && player != null) {
            var currentBiome = level.getBiome(player.blockPosition());
            if (lastBiome != currentBiome) {
                for (BiomeChangeListener listener : biomeChangeListener) {
                    listener.onBiomeChanged(currentBiome);
                }
                lastBiome = currentBiome;
            }
        } else {
            for (BiomeChangeListener listener : biomeChangeListener) {
                listener.onBiomeChanged(null);
            }
            lastBiome = null;
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
        Map<String, ConditionConfig> conditionConfigById = config.getConditionConfigById();
        for (Condition condition : conditions) {
            ConditionConfig conditionConfig = conditionConfigById.computeIfAbsent(condition.getId(),
                    k -> new ConditionConfig());
            conditionConfig.setPriority(condition.getPriority());
        }
    }

    @Override
    public void afterConfigChange(MainConfig config) {
        if (conditions == null) {
            Constants.LOG.error("Config loaded before conditions initialized!");
            return;
        }

        Map<String, ConditionConfig> conditionConfigById = config.getConditionConfigById();
        for (Condition condition : conditions) {
            if (conditionConfigById.containsKey(condition.getId())) {
                ConditionConfig conditionConfig = conditionConfigById.get(condition.getId());
                condition.setPriority(conditionConfig.getPriority());
            }
        }
    }
}
