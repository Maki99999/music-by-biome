package io.github.maki99999.biomebeats.condition;

import io.github.maki99999.biomebeats.BiomeBeatsCommon;
import io.github.maki99999.biomebeats.util.TickListener;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

import static io.github.maki99999.biomebeats.util.StringUtils.formatToTitleCase;

public class InGameModeCondition extends Condition implements TickListener {
    private final GameType gameType;

    public InGameModeCondition(GameType gameType) {
        super(getId(gameType), ConditionType.OTHER, "In " + formatToTitleCase(gameType.getName()) + " Mode");
        this.gameType = gameType;
        BiomeBeatsCommon.addTickListener(this);
    }

    public static String getId(GameType gameType) {
        return "InMode" + gameType.getName();
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
