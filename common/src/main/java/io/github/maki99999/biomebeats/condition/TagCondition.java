package io.github.maki99999.biomebeats.condition;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.maki99999.biomebeats.util.StringUtils.formatToTitleCase;

public class TagCondition extends Condition implements BiomeChangeListener {
    public static String TAG_PREFIX = "is_";

    private final Collection<TagKey<Biome>> biomeTagKeys;
    private final String id;

    public TagCondition(Collection<TagKey<Biome>> biomeTagKeys, int priority) {
        super(formatToTitleCase(biomeTagKeys.stream().findAny().map(TagKey::location).orElseThrow()), priority);
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
                .map(tagKeys -> new TagCondition(tagKeys, 0))
                .peek(condition -> condition.addListener(listener))
                .sorted(Comparator.comparing(TagCondition::getName))
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
