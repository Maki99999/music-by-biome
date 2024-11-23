package io.github.maki99999.biomebeats.condition;

import java.util.Collection;

public interface ActiveConditionsListener {
    void onActiveConditionsChanged(Collection<? extends Condition> activeConditions);
}
