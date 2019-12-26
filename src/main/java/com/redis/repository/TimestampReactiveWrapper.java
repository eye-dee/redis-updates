package com.redis.repository;

import java.util.Properties;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class TimestampReactiveWrapper implements TimestampRepositoryReactive {

    private final TimestampRepository timestampRepository;

    @Override
    public Mono<Boolean> addNewTimestamp(Long key, Long value) {
        return Mono.just(timestampRepository.addNewTimestamp(key, value));
    }

    @Override
    public Mono<Boolean> overrideOldValue(Long key, Long value) {
        return Mono.just(timestampRepository.overrideOldValue(key, value));
    }

    @Override
    public Mono<Long> getTimestampForKey(Long key) {
        return Mono.just(timestampRepository.getTimestampForKey(key));
    }

    @Override
    public Mono<Long> getOldest() {
        return Mono.just(timestampRepository.getOldest());
    }

    @Override
    public Mono<Properties> info() {
        return Mono.just(timestampRepository.info());
    }
}
