package com.redis.repository;

import java.util.List;
import java.util.Properties;

public interface TimestampRepository {

    Boolean addNewTimestamp(Long key, Long value);

    Boolean overrideOldValue(Long key, Long value);

    Long getTimestampForKey(Long key);

    Long getOldest();

    Properties info();

}
