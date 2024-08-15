package io.github.maki99999.biomebeats.condition;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import java.util.Collection;
import java.util.Comparator;

import static io.github.maki99999.biomebeats.util.StringUtils.formatToTitleCase;

public class BiomeCondition extends Condition implements BiomeChangeListener {
    private final ResourceLocation biomeRL;

    public BiomeCondition(ResourceLocation biomeRL, int priority) {
        super(formatToTitleCase(biomeRL), priority);
        this.biomeRL = biomeRL;
    }

    @Override
    public String getId() {
        return "biome:" + biomeRL.toString();
    }

    public static Collection<BiomeCondition> toConditions(Collection<ResourceLocation> biomes, ConditionChangeListener listener) {
        return biomes
                .stream()
                .map(b -> new BiomeCondition(b, 0))
                .peek(c -> c.addListener(listener))
                .sorted(Comparator.comparing(BiomeCondition::getName))
                .toList();
    }

    @Override
    public void onBiomeChanged(Holder<Biome> newBiome) {
        setConditionMet(newBiome != null && newBiome.is(biomeRL));
    }
}
