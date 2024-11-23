package io.github.maki99999.biomebeats.util;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class BiomeUtils {
    public static Collection<TagKey<Biome>> getBiomeTagKeys(Level level) {
        if (level.registryAccess().registry(Registries.BIOME).isEmpty())
            return List.of();

        return level.registryAccess().registry(Registries.BIOME).get().getTagNames().toList();
    }

    public static Collection<ResourceLocation> getBiomeRLs(Level level) {
        return level.registryAccess().registry(Registries.BIOME)
                .map(Registry::keySet)
                .orElseGet(Set::of);
    }
}
