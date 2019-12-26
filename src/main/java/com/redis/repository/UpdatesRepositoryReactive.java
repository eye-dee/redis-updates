package com.redis.repository;

import java.util.List;
import java.util.Map;
import reactor.core.publisher.Mono;

public interface UpdatesRepositoryReactive {
    Mono<Boolean> addNewUpdates(String id, Long timestamp, List<String> updates);

    Mono<Map<Long, List<String>>> getById(String id);

    Mono<Boolean> keyExists(String id, Long key);

    Mono<Long> deleteTimestamps(String id, List<Long> timestamps);
}
