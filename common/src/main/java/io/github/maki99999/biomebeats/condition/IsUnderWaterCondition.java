package io.github.maki99999.biomebeats.condition;

import io.github.maki99999.biomebeats.BiomeBeatsCommon;
import io.github.maki99999.biomebeats.util.TickListener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class IsUnderWaterCondition extends Condition implements TickListener {
    public IsUnderWaterCondition() {
        super("Is Under Water");
        BiomeBeatsCommon.addTickListener(this);
    }

    @Override
    public String getId() {
        return "IsUnderWater";
    }

    @Override
    public Component getTypeName() {
        return Component.translatable("menu.biomebeats.by_other");
    }

    @Override
    public void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        setConditionMet(player != null && player.isUnderWater());
    }
}
