package com.redis.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Update {

    private final String groupId;

    private final String id;

    private final String update;

    private final long kafkaOffset;

    @JsonCreator
    public Update(
            @JsonProperty("groupId") String groupId,
            @JsonProperty("id") String id,
            @JsonProperty("update") String update,
            @JsonProperty("kafkaOffset") long kafkaOffset) {
        this.groupId = groupId;
        this.id = id;
        this.update = update;
        this.kafkaOffset = kafkaOffset;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getId() {
        return id;
    }

    public String getUpdate() {
        return update;
    }

    public long getKafkaOffset() {
        return kafkaOffset;
    }
}
