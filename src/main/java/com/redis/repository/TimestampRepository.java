package com.redis.repository;

import java.util.Optional;

public interface TimestampRepository {

    Boolean addNewTimestamp(String key, Long value);

    Boolean overrideOldValue(String key, Long value);

    Optional<String> getOldest();

    String info();

}
