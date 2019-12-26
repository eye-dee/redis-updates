package com.redis.repository;

import java.util.List;
import java.util.Map;
import reactor.core.publisher.Mono;

public interface UpdatesRepositoryReactive {
    Mono<Boolean> addNewUpdates(Long id, Long timestamp, List<String> updates);

    Mono<Map<Long, List<String>>> getById(Long id);

    Mono<Boolean> keyExists(Long id, Long key);

    Mono<Long> deleteTimestamps(Long id, List<Long> timestamps);
}
