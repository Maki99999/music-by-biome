package io.github.maki99999.biomebeats.condition;

import io.github.maki99999.biomebeats.biome.BiomeChangeListener;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.maki99999.biomebeats.util.StringUtils.formatToTitleCase;

public class TagCondition extends Condition implements BiomeChangeListener {
    public static final String TAG_PREFIX = "is_";

    private final Collection<TagKey<Biome>> biomeTagKeys;
    private final String id;

    public TagCondition(Collection<TagKey<Biome>> biomeTagKeys) {
        super(formatToTitleCase(biomeTagKeys.stream().findAny().map(TagKey::location).orElseThrow()));
        this.biomeTagKeys = biomeTagKeys;
        this.id = "tag:" + biomeTagKeys.stream().map(TagKey::location).map(ResourceLocation::toString)
                .sorted().collect(Collectors.joining(","));
    }

    @Override
    public String getId() {
        return id;
    }

    public static Collection<TagCondition> toFilteredConditions(Collection<TagKey<Biome>> biomeTagKeys, ConditionChangeListener listener) {
        Map<String, Collection<TagKey<Biome>>> tagKeysByName = new HashMap<>();

        for (TagKey<Biome> tagKey : biomeTagKeys) {
            String path = tagKey.location().getPath();
            if (path.startsWith(TAG_PREFIX))
                tagKeysByName.computeIfAbsent(path, k -> new ArrayList<>()).add(tagKey);
        }

        return tagKeysByName
                .values()
                .stream()
                .map(TagCondition::new)
                .peek(condition -> condition.addListener(listener))
                .toList();
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
