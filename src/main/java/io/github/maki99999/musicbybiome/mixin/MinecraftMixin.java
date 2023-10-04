package io.github.maki99999.musicbybiome.mixin;

import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
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
            //TODO
        } else {
            possibleTracks.add(Musics.MENU);
        }

        if (possibleTracks.isEmpty()) {
            return;
        }

        cir.setReturnValue(possibleTracks.get(rand.nextInt(possibleTracks.size())));
    }
}
