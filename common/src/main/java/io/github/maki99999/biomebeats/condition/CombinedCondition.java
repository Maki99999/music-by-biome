package io.github.maki99999.biomebeats.condition;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CombinedCondition extends Condition implements ConditionChangeListener {
    private final UUID uuid;
    private final List<Condition> conditions = new ArrayList<>();
    private final List<Condition> metConditions = new ArrayList<>();

    private String description = "";
    private String name = "";

    public CombinedCondition(UUID uuid, String name, String description) {
        super(name);
        this.name = name;
        this.description = description;
        this.uuid = uuid;
    }

    public CombinedCondition(CombinedCondition condition) {
        super(condition.getName());
        uuid = condition.uuid;
        conditions.addAll(condition.conditions);
        metConditions.addAll(condition.metConditions);
        description = condition.description;
        name = condition.name;
        setPriority(condition.getPriority());
    }

    public CombinedCondition() {
        super("New Combined Condition");
        uuid = UUID.randomUUID();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Condition> getConditions() {
        return Collections.unmodifiableList(conditions);
    }

    @Override
    public String getId() {
        return uuid.toString();
    }

    public void addCondition(Condition condition) {
        if (condition instanceof CombinedCondition) {
            throw new NestedException("CombinedCondition cannot hold another CombinedCondition.");
        }
        conditions.add(condition);
        condition.addListener(this);
        onConditionChanged(condition);
    }

    @Override
    public Component getTypeName() {
        return Component.translatable("menu.biomebeats.combined");
    }

    public void removeCondition(Condition condition) {
        conditions.remove(condition);
        metConditions.remove(condition);
        condition.removeListener(this);
        setConditionMet(!conditions.isEmpty() && metConditions.size() == conditions.size());
    }

    @Override
    public void onConditionChanged(Condition condition) {
        if (condition.isConditionMet()) {
            metConditions.add(condition);
        } else {
            metConditions.remove(condition);
        }
        setConditionMet(!conditions.isEmpty() && metConditions.size() == conditions.size());
    }

    @Override
    public boolean isConditionMet() {
        return super.isConditionMet();
    }

    public boolean isEmpty() {
        return conditions.isEmpty()
                && (name == null || name.isEmpty())
                && (description == null || description.isEmpty());
    }
}
