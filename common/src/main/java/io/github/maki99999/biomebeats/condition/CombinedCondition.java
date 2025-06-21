package io.github.maki99999.biomebeats.condition;

import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.event.ConditionChangeEvent;
import io.github.maki99999.biomebeats.util.EventBus;

import java.util.*;

public class CombinedCondition extends Condition {
    private final Collection<String> conditionIds = new ArrayList<>();
    private final Collection<String> metConditionIds = new ArrayList<>();

    private String description;

    public CombinedCondition(String id, String name, String description, Collection<String> conditionIds) {
        super(id, ConditionType.COMBINED, name);
        this.description = description;
        setConditionIds(conditionIds);
        EventBus.subscribe(ConditionChangeEvent.class, e -> onConditionChanged(e.condition()));
    }

    /**
     * With random ID.
     */
    public CombinedCondition(String name, String description, List<String> conditionIds) {
        this(UUID.randomUUID().toString(), name, description, conditionIds);
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
            if (!metConditionIds.contains(condition.getId())) {
                metConditionIds.add(condition.getId());
            }
        } else {
            metConditionIds.remove(condition.getId());
        }
        updateConditionMet();
    }

    private void updateConditionMet() {
        if (metConditionIds.size() > conditionIds.size()) {
            Constants.LOG.error("Invalid state: {} conditions met, but only {} possible.",
                                metConditionIds.size(),
                                conditionIds.size());
        }

        setConditionMet(!conditionIds.isEmpty() && metConditionIds.size() >= conditionIds.size());
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
