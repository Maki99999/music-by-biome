package io.github.maki99999.biomebeats.util;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

public interface BiomeChangeListener {
    void onBiomeChanged(@Nullable Holder<Biome> newBiome);
}
