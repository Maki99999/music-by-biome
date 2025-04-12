package io.github.maki99999.biomebeats.condition;

import io.github.maki99999.biomebeats.event.ConditionChangeEvent;
import io.github.maki99999.biomebeats.util.EventBus;

import java.util.Objects;

public abstract class Condition {
    private final String id;
    private final ConditionType type;
    private String name;
    private int priority;
    private boolean met = false;

    public Condition(String id, ConditionType type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public Condition(String id, ConditionType type, String name, int priority) {
        this(id, type, name);
        this.priority = priority;
    }

    public final String getId() {
        return id;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
        EventBus.publish(new ConditionChangeEvent(this));
    }

    public final int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        if(this.priority != priority) {
            this.priority = priority;
            EventBus.publish(new ConditionChangeEvent(this));
        }
    }

    public final boolean isConditionMet() {
        return met;
    }

    protected final void setConditionMet(boolean met) {
        if (this.met != met) {
            this.met = met;
            EventBus.publish(new ConditionChangeEvent(this));
        }
    }

    public final ConditionType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(getId(), ((Condition) o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return name;
    }
}
