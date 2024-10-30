package io.github.maki99999.biomebeats.condition;

import io.github.maki99999.biomebeats.BiomeBeatsCommon;
import io.github.maki99999.biomebeats.util.TickListener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class BossOverlayWithMusicCondition extends Condition implements TickListener {
    public BossOverlayWithMusicCondition() {
        super("Near a Boss With Boss Music");
        BiomeBeatsCommon.addTickListener(this);
    }

    @Override
    public String getId() {
        return "BossOverlayWithMusic";
    }

    @Override
    public Component getTypeName() {
        return Component.translatable("menu.biomebeats.by_other");
    }

    @Override
    public void onTick() {
        setConditionMet(Minecraft.getInstance().gui.getBossOverlay().shouldPlayMusic());
    }
}
