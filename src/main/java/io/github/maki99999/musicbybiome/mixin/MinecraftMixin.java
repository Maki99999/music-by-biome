package io.github.maki99999.musicbybiome.mixin;

import io.github.maki99999.musicbybiome.Config;
import io.github.maki99999.musicbybiome.MusicByBiome;
import io.github.maki99999.musicbybiome.MusicProvider;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
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
public class MinecraftMixin {
    @Shadow
    @Nullable
    public Screen screen;

    @Shadow
    @Nullable
    public LocalPlayer player;

    private final Random rand = new Random();

    @Inject(method = "getSituationalMusic", at = @At("HEAD"), cancellable = true)
    private void getMusicByBiome(final CallbackInfoReturnable<Music> cir) {
        if (screen instanceof WinScreen) {
            return;
        }

        Music music = Optionull.map(this.screen, Screen::getBackgroundMusic);
        if (music != null) {
            cir.setReturnValue(music);
            return;
        }

        List<Music> possibleTracks = new ArrayList<>();

        if (this.player != null) {
            Holder<Biome> currentBiome;

            try(var currLevel = this.player.level()) {
                currentBiome = currLevel.getBiome(this.player.blockPosition());
            } catch (IOException e) {
                MusicByBiome.LOGGER.warn("Couldn't find player's biome!");
                return;
            }

            currentBiome.getTagKeys().forEach((tagKey) -> {
                for(var tag : Config.TAGS) {
                    if(tagKey.toString().contains(tag)) {
                        List<String> songNames = Config.songsPerTag.get(tag);
                        if (songNames != null) {
                            MusicByBiome.LOGGER.debug(songNames.size());
                            for (var songName : songNames) {
                                possibleTracks.add(MusicProvider.musicByName.get(songName));
                            }
                        }
                    }
                }
            });

        } else {
            possibleTracks.add(Musics.MENU);
        }

        if (possibleTracks.isEmpty()) {
            return;
        }

        cir.setReturnValue(possibleTracks.get(rand.nextInt(possibleTracks.size())));
    }
}
