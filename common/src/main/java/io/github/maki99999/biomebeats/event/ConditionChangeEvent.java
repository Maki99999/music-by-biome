package io.github.maki99999.biomebeats.event;

import io.github.maki99999.biomebeats.condition.Condition;

public record ConditionChangeEvent(Condition condition) implements Event {}
