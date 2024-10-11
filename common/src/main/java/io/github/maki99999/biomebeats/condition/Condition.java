package io.github.maki99999.biomebeats.condition;

import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public abstract class Condition {
    private final String name;
    private int priority;
    private boolean met = false;
    private final Collection<ConditionChangeListener> listeners = new HashSet<>();

    public Condition(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isConditionMet() {
        return met;
    }

    protected void setConditionMet(boolean met) {
        if (this.met != met) {
            this.met = met;
            notifyListeners();
        }
    }

    public void addListener(ConditionChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ConditionChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (ConditionChangeListener listener : listeners) {
            listener.onConditionChanged(this);
        }
    }

    public abstract String getId();

    public abstract Component getTypeName();

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
}
