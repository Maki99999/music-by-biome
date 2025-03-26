package io.github.maki99999.biomebeats.condition;

import io.github.maki99999.biomebeats.biome.BiomeChangeListener;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.maki99999.biomebeats.util.StringUtils.formatToTitleCase;

public class TagCondition extends Condition implements BiomeChangeListener {
    private final Collection<TagKey<Biome>> biomeTagKeys;

    public TagCondition(Collection<TagKey<Biome>> biomeTagKeys) {
        super(getId(biomeTagKeys), ConditionType.TAG, formatToTitleCase(biomeTagKeys.stream().findAny().orElseThrow().location().getPath()));
        this.biomeTagKeys = biomeTagKeys;
    }

    public static @NotNull String getId(Collection<TagKey<Biome>> biomeTagKeys) {
        return "tag:" + biomeTagKeys.stream()
                .map(TagKey::location)
                .map(ResourceLocation::toString)
                .sorted()
                .collect(Collectors.joining(","));
    }

    @Override
    public void onBiomeChanged(Holder<Biome> newBiome) {
        if (newBiome == null) {
            setConditionMet(false);
            return;
        }

        for (TagKey<Biome> tagKey : biomeTagKeys) {
            if (newBiome.is(tagKey)) {
                setConditionMet(true);
                return;
            }
        }

        setConditionMet(false);
    }
}
