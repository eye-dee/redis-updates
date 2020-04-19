package com.redis.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimestampRecord that = (TimestampRecord) o;
        return kafkaOffset == that.kafkaOffset &&
                Objects.equals(groupId, that.groupId) &&
                Objects.equals(id, that.id) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, id, timestamp, kafkaOffset);
    }

    @Override
    public String toString() {
        return "TimestampRecord{" +
                "groupId='" + groupId + '\'' +
                ", id='" + id + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", kafkaOffset=" + kafkaOffset +
                '}';
    }
}
