package io.github.maki99999.musicbybiome.mixin;

import io.github.maki99999.musicbybiome.MusicByBiome;
import io.github.maki99999.musicbybiome.music.CustomMusic;
import io.github.maki99999.musicbybiome.music.MusicProvider;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.core.Holder;
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
import java.util.Random;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    @Nullable
    public Screen screen;

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    public abstract MusicManager getMusicManager();

    private final Random rand = new Random();
    private long timeInCurrentBiome = 0;
    private Biome currentBiome = null;
    private boolean isRaining = false;
    private boolean isNight = false;
    private boolean inMenu = false;
    private CustomMusic currentMusic = null;

    @Inject(method = "getSituationalMusic", at = @At("HEAD"), cancellable = true)
    private void getMusicByBiome(final CallbackInfoReturnable<Music> cir) {
        Music winScreenMusic = Optionull.map(this.screen, Screen::getBackgroundMusic);
        if (winScreenMusic != null) {
            cir.setReturnValue(winScreenMusic);
            return;
        }

        var isPlayingMusic = currentMusic != null && getMusicManager().isPlayingMusic(currentMusic.getReplacingMusic());
        boolean replaceMusic;
        timeInCurrentBiome++;
        List<CustomMusic> possibleTracks = new ArrayList<>();

        if (this.player == null) {
            replaceMusic = !inMenu || !isPlayingMusic;
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
            Holder<Biome> currentBiome;

            try (var currLevel = this.player.level()) {
                currentBiome = currLevel.getBiome(this.player.blockPosition());
                inOverworld = currentBiome.is(BiomeTags.IS_OVERWORLD);
                isRaining = currLevel.isRaining();
                isNight = currLevel.isNight();
            } catch (IOException e) {
                MusicByBiome.LOGGER.warn("Couldn't find player's biome!");
                currentBiome = null;
            }

            replaceMusic = !isPlayingMusic ||
                    (currentBiome != null && shouldReplaceCurrentMusic(isRaining, isNight, currentBiome.value()));
            if (!replaceMusic) {
                cir.setReturnValue(currentMusic.getMusic());
                return;
            }

            if (inOverworld && isNight)
                possibleTracks.addAll(MusicProvider.getNightSongs());
            else if (inOverworld && isRaining)
                possibleTracks.addAll(MusicProvider.getRainSongs());
            else if (currentBiome != null)
                possibleTracks.addAll(MusicProvider.getSongsFromTagStream(currentBiome.getTagKeys()));
        }

        if (possibleTracks.isEmpty()) {
            possibleTracks.addAll(MusicProvider.getGenericSongs());
            if (possibleTracks.isEmpty())
                return;
        }

        CustomMusic nextSong = possibleTracks.get(rand.nextInt(possibleTracks.size()));
        currentMusic = nextSong;
        cir.setReturnValue(nextSong.getReplacingMusic());
    }

    private boolean shouldReplaceCurrentMusic(boolean isRaining, boolean isNight, Biome biome) {
        // Check if coming from menu
        if (inMenu) {
            inMenu = false;
            return true;
        }

        // Check if the player is in a new biome
        if (this.currentBiome == null || !this.currentBiome.equals(biome)) {
            this.currentBiome = biome;
            timeInCurrentBiome = 0;
        }

        // Check if the player has been in the current biome for 10 seconds
        if (timeInCurrentBiome == 200) {
            return true;
        }
        // Check if it starts or stops raining
        if (this.isRaining != isRaining) {
            this.isRaining = isRaining;
            return true;
        }
        // Check if it transitions from day to night or night to day
        if (this.isNight != isNight) {
            this.isNight = isNight;
            return true;
        }
        return false;
    }
}
