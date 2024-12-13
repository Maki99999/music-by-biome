package io.github.maki99999.biomebeats.mixin;

import io.github.maki99999.biomebeats.BiomeBeatsCommon;
import io.github.maki99999.biomebeats.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.MusicInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
    @Shadow public LocalPlayer player;

    @Inject(method = "close", at = @At("TAIL"))
    private void close(CallbackInfo ci) {
        BiomeBeatsCommon.close();
    }

    @Inject(method = "getSituationalMusic", at = @At("HEAD"), cancellable = true)
    private void getSituationalMusic(final CallbackInfoReturnable<MusicInfo> cir) {
        // Always play the empty sound event as music
        cir.setReturnValue(new MusicInfo(Constants.EMPTY_MUSIC));
    }

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void setScreen(Screen guiScreen, CallbackInfo ci) {
        BiomeBeatsCommon.notifyMenuChangeListeners(guiScreen, player);
    }
}