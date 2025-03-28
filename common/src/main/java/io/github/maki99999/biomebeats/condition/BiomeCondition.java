package io.github.maki99999.biomebeats.condition;

import io.github.maki99999.biomebeats.biome.BiomeChangeListener;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import static io.github.maki99999.biomebeats.util.StringUtils.formatToTitleCase;

public class BiomeCondition extends Condition implements BiomeChangeListener {
    private final ResourceLocation biomeRl;

    public BiomeCondition(ResourceLocation biomeRl) {
        super(getId(biomeRl), ConditionType.BIOME, formatToTitleCase(biomeRl));
        this.biomeRl = biomeRl;
    }

    public ResourceLocation getBiomeRl() {
        return biomeRl;
    }

    public static String getId(ResourceLocation biomeRl) {
        return "biome:" + biomeRl.toString();
    }

    @Override
    public void onBiomeChanged(Holder<Biome> newBiome) {
        setConditionMet(newBiome != null && newBiome.is(biomeRl));
    }
}
