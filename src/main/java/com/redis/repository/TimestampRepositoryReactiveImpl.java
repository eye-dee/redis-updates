package com.redis.repository;

import java.util.List;
import java.util.Properties;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class TimestampRepositoryReactiveImpl implements TimestampRepositoryReactive {

    private static final String KEY = "tsMap";

    private final ReactiveRedisTemplate<String, Long> reactiveRedisTemplate;

    private ReactiveZSetOperations<String, Long> reactiveValueOperations;

    @PostConstruct
    private void initOperations() {
        reactiveValueOperations = reactiveRedisTemplate.opsForZSet();
    }

    public Mono<Boolean> addNewTimestamp(Long key, Long value) {
        return reactiveValueOperations.rank(KEY, value)
                .map(it -> false)
                .switchIfEmpty(reactiveValueOperations.add(KEY, value, key));
    }

    public Mono<Boolean> overrideOldValue(Long key, Long value) {
        return reactiveValueOperations.rank(KEY, value)
                .flatMap(it -> reactiveValueOperations.add(KEY, value, key).map(updated -> !updated))
                .switchIfEmpty(Mono.just(false));
    }

    public Mono<Long> getTimestampForKey(Long key) {
        return reactiveValueOperations.range(KEY,
                Range.from(Range.Bound.inclusive(key))
                        .to(Range.Bound.inclusive(key)))
                .next();
    }

    public Mono<Long> getOldest() {
        return reactiveValueOperations.rangeByScore(KEY,
                Range.
                        from(Range.Bound.exclusive((double) Long.MAX_VALUE))
                        .to(Range.Bound.exclusive((double) Long.MIN_VALUE)),
                RedisZSetCommands.Limit.limit().count(1))
                .next();
    }

    public Mono<Properties> info() {
        return reactiveRedisTemplate.execute(connection -> connection.serverCommands().info())
                .next()
                .log();
    }
}
