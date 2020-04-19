package com.redis.repository;

import com.redis.model.TimestampRecord;
import java.util.Optional;

public interface TimestampRepository {

    boolean addNewTimestampRecord(TimestampRecord record);

    Optional<TimestampRecord> takeFromHead(String groupId);

    Optional<TimestampRecord> getOldest(String groupId);
}
