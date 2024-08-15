package io.github.maki99999.biomebeats.condition;

import java.util.Collection;
import java.util.HashSet;

public abstract class Condition {
    private final String name;
    private int priority;
    private boolean met = false;
    private final Collection<ConditionChangeListener> listeners = new HashSet<>();

    public Condition(String name, int priority) {
        this.name = name;
        this.priority = priority;
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

    private void notifyListeners() {
        for (ConditionChangeListener listener : listeners) {
            listener.onConditionChanged(this);
        }
    }

    public abstract String getId();
}
