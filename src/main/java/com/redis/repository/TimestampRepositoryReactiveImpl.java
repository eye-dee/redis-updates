package com.redis.repository;

import java.util.List;
import java.util.Properties;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class TimestampRepositoryReactiveImpl implements TimestampRepositoryReactive {

    private final ReactiveRedisTemplate<String, Long> reactiveRedisTemplate;

    private ReactiveValueOperations<String, Long> reactiveValueOperations;

    @PostConstruct
    private void initOperations() {
        reactiveValueOperations = reactiveRedisTemplate.opsForValue();
    }

    public Mono<Boolean> addNewTimestamp(String key, Long value) {
        return reactiveValueOperations.setIfAbsent("id-" + key, value);
    }

    public Mono<Boolean> overrideOldValue(String key, Long value) {
        return reactiveValueOperations.setIfPresent("id-" + key, value);
    }

    public Mono<Long> getTimestampForKey(String key) {
        return reactiveValueOperations.get("id-" + key);
    }

    public Mono<List<Long>> getAll() {
        return reactiveRedisTemplate.keys("id-*")
                .collectList()
                .flatMap(keys -> reactiveValueOperations.multiGet(keys));
    }

    public Mono<Properties> info() {
        return reactiveRedisTemplate.execute(connection -> connection.serverCommands().info())
                .next()
                .log();
    }
}
