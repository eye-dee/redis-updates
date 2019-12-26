package com.redis.repository;

import java.util.List;
import java.util.Properties;
import reactor.core.publisher.Mono;

public interface TimestampRepositoryReactive {

    Mono<Boolean> addNewTimestamp(String key, Long value);

    Mono<Boolean> overrideOldValue(String key, Long value);

    Mono<Long> getTimestampForKey(String key);

    Mono<List<Long>> getAll();

    Mono<String> info();

}
