package io.github.maki99999.biomebeats.condition;

import io.github.maki99999.biomebeats.BiomeBeatsCommon;
import io.github.maki99999.biomebeats.util.TickListener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

import static io.github.maki99999.biomebeats.util.StringUtils.formatToTitleCase;

public class InGameModeCondition extends Condition implements TickListener {
    private final GameType gameType;

    public InGameModeCondition(GameType gameType) {
        super("In " + formatToTitleCase(gameType.getName()) + " Mode");
        this.gameType = gameType;
        BiomeBeatsCommon.addTickListener(this);
    }

    @Override
    public String getId() {
        return "InMode" + gameType.getName();
    }

    @Override
    public Component getTypeName() {
        return Component.translatable("menu.biomebeats.by_other");
    }

    @Override
    public void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player != null) {
            setConditionMet((gameType == GameType.SPECTATOR && player.isSpectator())
                    || (gameType == GameType.CREATIVE && player.isCreative()));
        } else {
            setConditionMet(false);
        }
    }
}
