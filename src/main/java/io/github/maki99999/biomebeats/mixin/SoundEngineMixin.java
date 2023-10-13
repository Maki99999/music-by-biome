package io.github.maki99999.biomebeats.mixin;

import io.github.maki99999.biomebeats.BiomeBeats;
import io.github.maki99999.biomebeats.Config;
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
        if (!Config.debug || sound.getSource() != SoundSource.MUSIC)
            return;

        BiomeBeats.LOGGER.info("playing: " + sound.getLocation() + " [sound: " + sound.getSound().getLocation() + "]");
        var player = Minecraft.getInstance().player;
        if (player != null)
            player.displayClientMessage(Component.literal("playing: " + sound.getSound().getLocation()), true);
    }
}
