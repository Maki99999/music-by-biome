package io.github.maki99999.musicbybiome.mixin;

import io.github.maki99999.musicbybiome.MusicByBiome;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundManager.class)
public class SoundManagerMixin
{
    @Inject(method = "validateSoundResource", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", remap = false))
    private static void ignoreWarning(
      final Sound sound,
      final ResourceLocation resourceLocation,
      final ResourceProvider resourceProvider,
      final CallbackInfoReturnable<Boolean> cir)
    {
        if (sound.getPath().getNamespace().equals(MusicByBiome.MOD_ID))
        {
            MusicByBiome.LOGGER.warn("Some warning here that can be ignored i think...");//TODO
            //cir.setReturnValue(false);
        }
    }
}
