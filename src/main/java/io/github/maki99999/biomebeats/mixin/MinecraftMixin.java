package io.github.maki99999.biomebeats.mixin;

import io.github.maki99999.biomebeats.BiomeBeats;
import io.github.maki99999.biomebeats.Config;
import io.github.maki99999.biomebeats.music.CustomMusic;
import io.github.maki99999.biomebeats.music.MinecraftMixinAccessor;
import io.github.maki99999.biomebeats.music.MusicProvider;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.Music;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements MinecraftMixinAccessor {
    @Shadow
    @Nullable
    public Screen screen;

    @Shadow
    @Nullable
    public ClientLevel level;

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    public abstract MusicManager getMusicManager();

    private CustomMusic currentMusic = null;
    private Biome currentMusicBiome = null;
    private long currentMusicTicks = 0;
    private long noMusicTicks = 0;
    private long ticksNotInCurrentBiome = Integer.MAX_VALUE;

    private boolean isRaining = false;
    private boolean isNight = false;
    private boolean inMenu = false;

    @Inject(method = "getSituationalMusic", at = @At("HEAD"), cancellable = true)
    private void getMusicByBiome(final CallbackInfoReturnable<Music> cir) {
        Music winScreenMusic = Optionull.map(this.screen, Screen::getBackgroundMusic);
        if (winScreenMusic != null) {
            cir.setReturnValue(winScreenMusic);
            return;
        }

        boolean musicIsPlaying = currentMusic != null;
        if (musicIsPlaying && !getMusicManager().isPlayingMusic(currentMusic.getReplacingMusic())) {
            noMusicTicks++;
            if (noMusicTicks >= 5) {
                musicIsPlaying = false;
                BiomeBeats.debugMsg("No music for " + noMusicTicks + " ticks...");
            }
        } else {
            noMusicTicks = 0;
        }

        boolean replaceMusic;
        Biome currentBiome = null;
        currentMusicTicks++;
        List<CustomMusic> possibleTracks = new ArrayList<>();

        if (player == null) {
            replaceMusic = !inMenu || !musicIsPlaying;
            if (!inMenu)
                BiomeBeats.debugMsg("Player is now in menu. Replacing music...");
            inMenu = true;
            if (!replaceMusic) {
                cir.setReturnValue(currentMusic.getMusic());
                return;
            }
            possibleTracks.addAll(MusicProvider.getMenuSongs());
        } else {
            boolean inOverworld = true;
            boolean isNight = false;
            boolean isRaining = false;
            Holder<Biome> currentBiomeHolder;

            try (var currLevel = player.level()) {
                currentBiomeHolder = currLevel.getBiome(player.blockPosition());
                currentBiome = currentBiomeHolder.value();
                inOverworld = currentBiomeHolder.is(BiomeTags.IS_OVERWORLD);
                isRaining = currLevel.isRaining();
                isNight = currLevel.isNight();
            } catch (IOException e) {
                BiomeBeats.LOGGER.warn("Couldn't find player's biome!");
                currentBiomeHolder = null;
            }

            replaceMusic = shouldReplaceCurrentMusic(isRaining, isNight, currentBiome) || !musicIsPlaying;
            if (!replaceMusic) {
                cir.setReturnValue(currentMusic.getMusic());
                return;
            }

            if (inOverworld && isNight && isRaining) {
                possibleTracks.addAll(MusicProvider.getNightSongs());
                possibleTracks.addAll(MusicProvider.getRainSongs());
            } else if (inOverworld && isNight)
                possibleTracks.addAll(MusicProvider.getNightSongs());
            else if (inOverworld && isRaining)
                possibleTracks.addAll(MusicProvider.getRainSongs());
            else if (currentBiomeHolder != null)
                possibleTracks.addAll(MusicProvider.getSongsFromTagStream(currentBiomeHolder.getTagKeys()));
        }

        // Wants to replace the music

        if (possibleTracks.isEmpty()) {
            possibleTracks.addAll(MusicProvider.getGenericSongs());
            if (possibleTracks.isEmpty())
                return;
        }

        if (currentMusic != null && currentMusicTicks < 15) {
            BiomeBeats.debugMsg("Not replacing music because it was replaced just " + currentMusicTicks + " ticks ago.");
            cir.setReturnValue(currentMusic.getMusic());
            return;
        }
        if (musicIsPlaying && possibleTracks.contains(currentMusic)) {
            BiomeBeats.debugMsg("Not replacing music because it still fits.");
            cir.setReturnValue(currentMusic.getMusic());
            currentMusicBiome = currentBiome;
            ticksNotInCurrentBiome = 0;
            return;
        }

        BiomeBeats.debugMsg("Choosing a new song from " + possibleTracks.size() + " music track(s):");

        int nextSongIndex = BiomeBeats.RANDOM.nextInt(possibleTracks.size());
        CustomMusic nextSong = possibleTracks.get(nextSongIndex);
        if (Objects.equals(currentMusic, nextSong))
            nextSong = possibleTracks.get((nextSongIndex + 1) % possibleTracks.size());

        currentMusic = nextSong;
        currentMusicBiome = currentBiome;
        currentMusicTicks = 0;
        ticksNotInCurrentBiome = 0;
        noMusicTicks = 0;
        BiomeBeats.debugMsg("New song: " + currentMusic.getName());
        cir.setReturnValue(nextSong.getReplacingMusic());
    }

    private boolean shouldReplaceCurrentMusic(boolean isRaining, boolean isNight, Biome currentBiome) {
        // Check if coming from menu
        if (inMenu) {
            inMenu = false;
            BiomeBeats.debugMsg("Player came from menu. Replacing music...");
            return true;
        }

        // Check if the player is not in a new biome
        if (!Objects.equals(currentMusicBiome, currentBiome)) {
            ticksNotInCurrentBiome++;
        } else {
            ticksNotInCurrentBiome = 0;
        }
        // Check if the player has been in another biome for 5 seconds
        if (ticksNotInCurrentBiome >= 5 * 20) {
            BiomeBeats.debugMsg("Player was " + ticksNotInCurrentBiome + " ticks in another biome. Replacing music...");
            debugMsgCurrentAndNextBiomeName(currentBiome);
            return true;
        }

        // Check if it starts or stops raining
        if (this.isRaining != isRaining) {
            this.isRaining = isRaining;
            BiomeBeats.debugMsg("Detected weather change. Replacing music...");
            return true;
        }
        // Check if it transitions from day to night or night to day
        if (this.isNight != isNight) {
            this.isNight = isNight;
            BiomeBeats.debugMsg("Detected daytime changing. Replacing music...");
            return true;
        }
        return false;
    }

    private void debugMsgCurrentAndNextBiomeName(Biome nextBiome) {
        if (!Config.debug || level == null)
            return;

        var biomeRegistry = level.registryAccess().registry(Registries.BIOME).isPresent() ?
                level.registryAccess().registry(Registries.BIOME).get() : null;
        if (biomeRegistry != null) {
            BiomeBeats.debugMsg("[old biome: " + biomeRegistry.getKey(currentMusicBiome) + ", new biome: " +
                    biomeRegistry.getKey(nextBiome) + "]");
        }
    }

    @Override
    @Nullable
    public String getCurrentSongName() {
        if (currentMusic == null)
            return null;
        else
            return currentMusic.getName();
    }

    @Override
    public void skipSong() {
        getMusicManager().stopPlaying();
    }
}
