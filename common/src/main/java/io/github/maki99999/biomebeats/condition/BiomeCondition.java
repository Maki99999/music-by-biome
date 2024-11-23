package io.github.maki99999.biomebeats.condition;

import io.github.maki99999.biomebeats.biome.BiomeChangeListener;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import java.util.Collection;

import static io.github.maki99999.biomebeats.util.StringUtils.formatToTitleCase;

public class BiomeCondition extends Condition implements BiomeChangeListener {
    private final ResourceLocation biomeRL;

    public BiomeCondition(ResourceLocation biomeRL) {
        super(formatToTitleCase(biomeRL));
        this.biomeRL = biomeRL;
    }

    public ResourceLocation getBiomeRL() {
        return biomeRL;
    }

    @Override
    public String getId() {
        return "biome:" + biomeRL.toString();
    }

    @Override
    public Component getTypeName() {
        return Component.translatable("menu.biomebeats.by_biome");
    }

    public static Collection<BiomeCondition> toConditions(Collection<ResourceLocation> biomes, ConditionChangeListener listener) {
        return biomes
                .stream()
                .map(BiomeCondition::new)
                .peek(c -> c.addListener(listener))
                .toList();
    }

    @Override
    public void onBiomeChanged(Holder<Biome> newBiome) {
        setConditionMet(newBiome != null && newBiome.is(biomeRL));
    }
}
