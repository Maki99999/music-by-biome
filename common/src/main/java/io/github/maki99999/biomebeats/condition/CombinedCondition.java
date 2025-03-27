package io.github.maki99999.biomebeats.condition;

import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.event.ConditionChangeEvent;
import io.github.maki99999.biomebeats.util.EventBus;

import java.util.*;

public class CombinedCondition extends Condition {
    private final Collection<String> conditionIds = new ArrayList<>();
    private final Collection<String> metConditionIds = new ArrayList<>();

    private String description;

    /**
     * Create from config
     */
    public CombinedCondition(String id, String name, String description, Collection<String> conditionIds) {
        super(id, ConditionType.COMBINED, name);
        this.description = description;
        setConditionIds(conditionIds);
        EventBus.subscribe(ConditionChangeEvent.class, e -> onConditionChanged(e.condition()));
    }

    /**
     * Create from scratch
     */
    public CombinedCondition(String name, String description, Collection<String> conditionIds) {
        super(UUID.randomUUID().toString(), ConditionType.COMBINED, name);
        this.description = description;
        setConditionIds(conditionIds);
        EventBus.subscribe(ConditionChangeEvent.class, e -> onConditionChanged(e.condition()));
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getConditionIds() {
        return conditionIds.stream().toList();
    }

    public void onConditionChanged(Condition condition) {
        if (!conditionIds.contains(condition.getId())) {
            return;
        }

        if (condition.isConditionMet()) {
            metConditionIds.add(condition.getId());
        } else {
            metConditionIds.remove(condition.getId());
        }
        updateConditionMet();
    }

    public boolean isEmpty() {
        return conditionIds.isEmpty()
                && (getName() == null || getName().isEmpty())
                && (description == null || description.isEmpty());
    }

    private void updateConditionMet() {
        setConditionMet(!conditionIds.isEmpty() && metConditionIds.size() == conditionIds.size());
    }

    public void dispose() {
        EventBus.unsubscribe(ConditionChangeEvent.class, e -> onConditionChanged(e.condition()));
    }

    public void setConditionIds(Collection<String> conditionIds) {
        this.conditionIds.clear();
        this.metConditionIds.clear();

        this.conditionIds.addAll(conditionIds);
        this.metConditionIds.addAll(conditionIds.stream().filter(Constants.CONDITION_MANAGER::isConditionMet).toList());
        updateConditionMet();
    }
}
