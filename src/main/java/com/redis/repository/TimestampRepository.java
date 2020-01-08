package com.redis.repository;

import java.util.Optional;

public interface TimestampRepository {

    boolean addNewTimestamp(String key, Long value);

    boolean overrideOldValue(String key, Long value);

    boolean addOrOverride(String key, Long value);

    Optional<String> getOldest();

    double info();

    boolean deleteForGroup(String groupId);

    boolean deleteTimestamp(Long timestamp);
}
