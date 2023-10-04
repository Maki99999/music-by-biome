package io.github.maki99999.musicbybiome.mixin;

import io.github.maki99999.musicbybiome.MusicByBiome;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    @Inject(method = "play", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/sounds/SoundInstance;getSound()Lnet/minecraft/client/resources/sounds/Sound;"))
    private void displayCurrentMusic(final SoundInstance sound, final CallbackInfo ci) {
        if (sound.getSource() != SoundSource.MUSIC)
            return;

        MusicByBiome.LOGGER.info("playing: " + sound.getLocation() + " sound:" + sound.getSound().getLocation());
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(
                    Component.literal("playing: " + sound.getSound().getLocation()),
                    true
            );
        }
    }
}
