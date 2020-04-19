package com.redis.repository;

import com.redis.model.TimestampRecord;
import java.util.List;
import java.util.Optional;

public interface TimestampRepository {

    boolean addNewTimestampRecord(TimestampRecord record);

    List<TimestampRecord> getFirst(String groupId, int n);

    long deleteFirstForGroup(String group, int n);
}
