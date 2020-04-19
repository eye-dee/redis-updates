package com.redis.repository.jedis;

import com.redis.model.TimestampRecord;
import com.redis.repository.TimestampRepository;
import java.util.Optional;

public class TimestampRepositoryJedis implements TimestampRepository {

    @Override
    public boolean addNewTimestampRecord(TimestampRecord record) {
        return false;
    }

    @Override
    public Optional<TimestampRecord> takeFromHead(String groupId) {
        return Optional.empty();
    }

    @Override
    public Optional<TimestampRecord> getOldest(String groupId) {
        return Optional.empty();
    }
}
