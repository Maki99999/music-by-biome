package io.github.maki99999.biomebeats.condition;

import io.github.maki99999.biomebeats.biome.BiomeChangeListener;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.maki99999.biomebeats.util.StringUtils.formatToTitleCase;

public class TagCondition extends Condition implements BiomeChangeListener {
    public static final String[] TAG_PREFIXES = new String[]{"is_", "plays_"};

    private final Collection<TagKey<Biome>> biomeTagKeys;
    private final String id;

    public TagCondition(Collection<TagKey<Biome>> biomeTagKeys) {
        super(formatToTitleCase(biomeTagKeys.stream().findAny().orElseThrow().location().getPath()));
        this.biomeTagKeys = biomeTagKeys;
        this.id = "tag:" + biomeTagKeys.stream().map(TagKey::location).map(ResourceLocation::toString)
                .sorted().collect(Collectors.joining(","));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Component getTypeName() {
        return Component.translatable("menu.biomebeats.by_tag");
    }

    public static Collection<TagCondition> toFilteredConditions(Collection<TagKey<Biome>> biomeTagKeys,
                                                                ConditionChangeListener listener) {
        Map<String, Collection<TagKey<Biome>>> tagKeysByName = new HashMap<>();

        for (TagKey<Biome> tagKey : biomeTagKeys) {
            String path = tagKey.location().getPath();
            if (Arrays.stream(TAG_PREFIXES).anyMatch(path::startsWith))
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
