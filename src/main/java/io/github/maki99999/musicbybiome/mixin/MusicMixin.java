package io.github.maki99999.musicbybiome.mixin;

import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Music.class)
public class MusicMixin {
    @Shadow
    @Mutable
    @Final
    private int minDelay;

    @Shadow
    @Mutable
    @Final
    private int maxDelay;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void changeDelay(final Holder<SoundEvent> holder, final int p_263377_, final int p_263383_,
                             final boolean p_263387_, final CallbackInfo ci) {
        minDelay *= 0;
        maxDelay *= 0;
    }
}
