package io.github.maki99999.biomebeats.biome;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class BiomeManager {
    private static final int MOST_RECENT_BIOMES_COUNT = 5;

    private final Collection<BiomeChangeListener> biomeChangeListener = new HashSet<>();
    private final List<Holder<Biome>> mostRecentBiomes = new ArrayList<>();

    private Holder<Biome> lastBiome;

    public void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        detectBiomeChange(minecraft.level, minecraft.player);
    }

    private void updateMostRecentBiomes(Holder<Biome> currentBiome) {
        mostRecentBiomes.remove(currentBiome);
        mostRecentBiomes.add(currentBiome);

        if(mostRecentBiomes.size() > MOST_RECENT_BIOMES_COUNT) {
            mostRecentBiomes.removeLast();
        }
    }

    public List<Holder<Biome>> getMostRecentBiomes() {
        return mostRecentBiomes;
    }

    private void detectBiomeChange(ClientLevel level, LocalPlayer player) {
        if (level != null && player != null) {
            var currentBiome = level.getBiome(player.blockPosition());
            updateMostRecentBiomes(currentBiome);
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

    public void addBiomeChangeListeners(Collection<? extends BiomeChangeListener> listeners) {
        biomeChangeListener.addAll(listeners);
    }

    public void clearBiomeChangeListeners() {
        biomeChangeListener.clear();
    }
}