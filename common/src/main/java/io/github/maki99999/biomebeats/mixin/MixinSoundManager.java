package io.github.maki99999.biomebeats.mixin;

import io.github.maki99999.biomebeats.Constants;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(SoundManager.class)
public abstract class MixinSoundManager {
    @Inject(method = "updateSourceVolume", at = @At("HEAD"))
    private void updateSourceVolume(SoundSource soundSource, float volume, CallbackInfo ci) {
        if (soundSource == SoundSource.MUSIC || soundSource == SoundSource.MASTER) {
            Constants.MUSIC_MANAGER.updateVolume();
        }
    }
}
