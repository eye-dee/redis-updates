package com.redis.repository;

import java.util.List;
import java.util.Properties;
import reactor.core.publisher.Mono;

public interface TimestampRepositoryReactive {

    Mono<Boolean> addNewTimestamp(Long key, Long value);

    Mono<Boolean> overrideOldValue(Long key, Long value);

    Mono<Long> getTimestampForKey(Long key);

    Mono<Long> getOldest();

    Mono<Properties> info();

}
