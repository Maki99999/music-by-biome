package io.github.maki99999.biomebeats.condition;

public class NoOtherMusicCondition extends Condition {
    public static final String ID = "NoOtherMusic";

    public NoOtherMusicCondition() {
        super(ID, ConditionType.OTHER, "No Other Music Is Playing", Integer.MIN_VALUE);
        setConditionMet(true);
    }

    @Override
    public void setPriority(int priority) {
        // not allowed
    }
}
