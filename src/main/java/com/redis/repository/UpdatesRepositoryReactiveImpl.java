package com.redis.repository;

import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UpdatesRepositoryReactiveImpl implements UpdatesRepositoryReactive {

    @Qualifier("reactiveRedisTemplateUpdatesMap")
    private final ReactiveRedisTemplate<Long, Map<Long, List<String>>> reactiveRedisTemplate;

    private ReactiveHashOperations<Long, Long, List<String>> reactiveHashOperations;

    @PostConstruct
    private void initHashOperations() {
        log.info("reactiveRedisTemplateUpdatesMap {}", reactiveRedisTemplate);
        reactiveHashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<Boolean> addNewUpdates(Long id, Long timestamp, List<String> updates) {
        return reactiveHashOperations
                .put(id, timestamp, updates);
    }

    public Mono<Map<Long, List<String>>> getById(Long id) {
        return reactiveHashOperations.entries(id)
                .collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    public Mono<Boolean> keyExists(Long id, Long key) {
        return reactiveHashOperations.hasKey(id, key);
    }

    public Mono<Long> deleteTimestamps(Long id, List<Long> timestamps) {
        return reactiveHashOperations.remove(id, timestamps.toArray());
    }

}
