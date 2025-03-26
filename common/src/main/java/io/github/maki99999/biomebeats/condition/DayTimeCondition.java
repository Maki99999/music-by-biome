package io.github.maki99999.biomebeats.condition;

import io.github.maki99999.biomebeats.BiomeBeatsCommon;
import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.util.TickListener;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.io.IOException;

public class DayTimeCondition extends Condition implements TickListener {
    public static final String IS_DAY = "IsDay";
    public static final String IS_NIGHT = "IsNight";
    private final boolean checkForDay;

    public DayTimeCondition(boolean checkForDay) {
        super(checkForDay ? IS_DAY : IS_NIGHT, ConditionType.OTHER, checkForDay ? "Is Day" : "Is Night");
        this.checkForDay = checkForDay;
        BiomeBeatsCommon.addTickListener(this);
    }

    @Override
    public void onTick() {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            try (Level level = player.level()) {
                if (checkForDay) {
                    setConditionMet(!isNight(level.getDayTime()));
                } else {
                    setConditionMet(isNight(level.getDayTime()));
                }
            } catch (IOException e) {
                Constants.LOG.error(e.getMessage(), e);
                setConditionMet(false);
            }
        } else {
            setConditionMet(false);
        }
    }

    private static boolean isNight(long dayTime) {
        return dayTime >= 12786 && dayTime <= 23216;
    }
}
