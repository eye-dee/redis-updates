package com.redis.repository;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdatesReactiveWrapper implements UpdatesRepositoryReactive {

    private final UpdatesRepository updatesRepository;

    @Override
    public Mono<Boolean> addNewUpdates(String id, Long timestamp, List<String> updates) {
        return Mono.just(updatesRepository.addNewUpdates(id, timestamp, updates));
    }

    @Override
    public Mono<Map<Long, List<String>>> getById(String id) {
        return Mono.just(updatesRepository.getById(id));
    }

    @Override
    public Mono<Boolean> keyExists(String id, Long key) {
        return Mono.just(updatesRepository.keyExists(id, key));
    }

    @Override
    public Mono<Long> deleteTimestamps(String id, List<Long> timestamps) {
        return Mono.just(updatesRepository.deleteTimestamps(id, timestamps));
    }
}
