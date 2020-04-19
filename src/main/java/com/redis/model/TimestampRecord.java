package com.redis.model;

public class TimestampRecord {

    private final String groupId;
    private final String id;
    private final String timestamp;
    private final long kafkaOffset;

    public TimestampRecord(String groupId, String id, String timestamp, long kafkaOffset) {
        this.groupId = groupId;
        this.id = id;
        this.timestamp = timestamp;
        this.kafkaOffset = kafkaOffset;
    }
}
