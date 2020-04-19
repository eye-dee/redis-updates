package com.redis.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TimestampRecord {

    private final String groupId;

    private final String id;

    private final String timestamp;

    private final long kafkaOffset;

    @JsonCreator
    public TimestampRecord(
            @JsonProperty("groupId") String groupId,
            @JsonProperty("id") String id,
            @JsonProperty("timestamp") String timestamp,
            @JsonProperty("kafkaOffset") long kafkaOffset) {
        this.groupId = groupId;
        this.id = id;
        this.timestamp = timestamp;
        this.kafkaOffset = kafkaOffset;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public long getKafkaOffset() {
        return kafkaOffset;
    }
}
