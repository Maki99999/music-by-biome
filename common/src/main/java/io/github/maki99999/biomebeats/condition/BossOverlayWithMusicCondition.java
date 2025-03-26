package io.github.maki99999.biomebeats.condition;

import io.github.maki99999.biomebeats.BiomeBeatsCommon;
import io.github.maki99999.biomebeats.util.TickListener;
import net.minecraft.client.Minecraft;

public class BossOverlayWithMusicCondition extends Condition implements TickListener {
    public static final String ID = "BossOverlayWithMusic";

    public BossOverlayWithMusicCondition() {
        super(ID, ConditionType.OTHER, "Near a Boss With Boss Music");
        BiomeBeatsCommon.addTickListener(this);
    }

    @Override
    public void onTick() {
        setConditionMet(Minecraft.getInstance().gui.getBossOverlay().shouldPlayMusic());
    }
}
