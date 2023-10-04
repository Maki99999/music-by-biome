package io.github.maki99999.musicbybiome.mixin;

import io.github.maki99999.musicbybiome.MusicProvider;
import net.minecraft.sounds.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEvents.class)
public class SoundEventsMixin
{
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void addCustomMusic(final CallbackInfo ci)
    {
        MusicProvider.init();
    }
}
