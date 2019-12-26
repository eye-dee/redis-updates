package com.redis.repository;

import java.util.List;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class TimestampReactiveWrapper implements TimestampRepositoryReactive {

    private final TimestampRepository timestampRepository;

    @Override
    public Mono<Boolean> addNewTimestamp(String key, Long value) {
        return Mono.just(timestampRepository.addNewTimestamp(key, value));
    }

    @Override
    public Mono<Boolean> overrideOldValue(String key, Long value) {
        return Mono.just(timestampRepository.overrideOldValue(key, value));
    }

    @Override
    public Mono<Long> getTimestampForKey(String key) {
        return Mono.justOrEmpty(timestampRepository.getTimestampForKey(key));
    }

    @Override
    public Mono<List<Long>> getAll() {
        return Mono.just(timestampRepository.getAll());
    }

    @Override
    public Mono<String> info() {
        return Mono.just(timestampRepository.info());
    }
}
