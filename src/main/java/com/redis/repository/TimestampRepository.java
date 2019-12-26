package com.redis.repository;

import java.util.List;
import java.util.Properties;

public interface TimestampRepository {

    Boolean addNewTimestamp(String key, Long value);

    Boolean overrideOldValue(String key, Long value);

    Long getTimestampForKey(String key);

    List<Long> getAll();

    Properties info();

}
