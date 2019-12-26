package com.redis.repository;

import java.util.List;
import java.util.Optional;

public interface TimestampRepository {

    Boolean addNewTimestamp(String key, Long value);

    Boolean overrideOldValue(String key, Long value);

    Optional<Long> getTimestampForKey(String key);

    List<Long> getAll();

    String info();

}
