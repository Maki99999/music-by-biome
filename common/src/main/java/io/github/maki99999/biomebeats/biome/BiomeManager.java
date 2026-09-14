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
import java.util.Objects;

public class BiomeManager {
    private static final int MOST_RECENT_BIOMES_COUNT = 5;
    private static final int TICKS_BEFORE_NOTIFYING = 60;

    private final Collection<BiomeChangeListener> biomeChangeListener = new HashSet<>();
    private final List<Holder<Biome>> mostRecentBiomes = new ArrayList<>();

    private Holder<Biome> candidateBiome;
    private Holder<Biome> lastNotifiedBiome;
    private int ticksInCandidateBiome;

    public void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        detectBiomeChange(minecraft.level, minecraft.player);
    }

    private void updateMostRecentBiomes(Holder<Biome> currentBiome) {
        mostRecentBiomes.remove(currentBiome);
        mostRecentBiomes.addFirst(currentBiome);

        if (mostRecentBiomes.size() > MOST_RECENT_BIOMES_COUNT) {
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

            if (lastNotifiedBiome == null) {
                notifyBiomeChanged(currentBiome);
                resetCandidateBiome();
            } else if (Objects.equals(lastNotifiedBiome, currentBiome)) {
                resetCandidateBiome();
            } else if (!Objects.equals(candidateBiome, currentBiome)) {
                candidateBiome = currentBiome;
                ticksInCandidateBiome = 1;
            } else if (++ticksInCandidateBiome >= TICKS_BEFORE_NOTIFYING) {
                notifyBiomeChanged(currentBiome);
                resetCandidateBiome();
            }
        } else {
            resetCandidateBiome();
            if (lastNotifiedBiome != null) {
                notifyBiomeChanged(null);
            }
        }
    }

    private void notifyBiomeChanged(Holder<Biome> biome) {
        lastNotifiedBiome = biome;
        for (BiomeChangeListener listener : biomeChangeListener) {
            listener.onBiomeChanged(biome);
        }
    }

    private void resetCandidateBiome() {
        candidateBiome = null;
        ticksInCandidateBiome = 0;
    }

    public void addBiomeChangeListener(BiomeChangeListener listener) {
        biomeChangeListener.add(listener);
        listener.onBiomeChanged(lastNotifiedBiome);
    }

    public void clearBiomeChangeListeners() {
        biomeChangeListener.clear();
    }
}
