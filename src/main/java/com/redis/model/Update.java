package com.redis.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Update update1 = (Update) o;
        return kafkaOffset == update1.kafkaOffset &&
                Objects.equals(groupId, update1.groupId) &&
                Objects.equals(id, update1.id) &&
                Objects.equals(update, update1.update);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, id, update, kafkaOffset);
    }

    @Override
    public String toString() {
        return "Update{" +
                "groupId='" + groupId + '\'' +
                ", id='" + id + '\'' +
                ", update='" + update + '\'' +
                ", kafkaOffset=" + kafkaOffset +
                '}';
    }
}
