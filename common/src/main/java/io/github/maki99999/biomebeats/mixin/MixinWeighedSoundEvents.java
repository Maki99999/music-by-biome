package io.github.maki99999.biomebeats.mixin;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.client.sounds.Weighted;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(WeighedSoundEvents.class)
public interface MixinWeighedSoundEvents {
    @Accessor("list")
    List<Weighted<Sound>> list();
}
