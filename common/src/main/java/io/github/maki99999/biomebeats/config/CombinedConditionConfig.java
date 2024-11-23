package io.github.maki99999.biomebeats.config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CombinedConditionConfig {
    private UUID uuid = new UUID(0L, 0L);
    private String name = "";
    private String description = "";
    private List<String> conditionIds = new ArrayList<>();

    public CombinedConditionConfig() {}

    public CombinedConditionConfig(UUID uuid, String name, String description, List<String> conditionIds) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.conditionIds = conditionIds;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getConditionIds() {
        return conditionIds;
    }

    public void setConditionIds(List<String> conditionIds) {
        this.conditionIds = conditionIds;
    }
}
