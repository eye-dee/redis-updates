package com.redis.service;

import com.redis.repository.TimestampRepositoryReactive;
import com.redis.repository.UpdatesRepositoryReactive;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@RequiredArgsConstructor
public class UpdatesService {

    private final UpdatesRepositoryReactive updatesRepository;

    private final TimestampRepositoryReactive timestampRepository;

    public Mono<Boolean> addNewUpdates(String id, Long timestamp, List<String> updates) {
        return Mono.zip(
                updatesRepository.addNewUpdates(id, timestamp, updates),
                timestampRepository.addNewTimestamp(id, timestamp)
        )
                .map(Tuple2::getT1);
    }

    public Mono<Boolean> deleteOldUpdates(String id) {
        return updatesRepository.getById(id)
                .flatMap(map -> {
                    Long max = map.keySet()
                            .stream()
                            .max(Comparator.comparingLong(l -> l))
                            .orElseThrow();
                    List<Long> timestamps = map.keySet()
                            .stream()
                            .filter(timestamp -> timestamp < max)
                            .collect(Collectors.toList());
                    return timestampRepository.overrideOldValue(id, max)
                            .zipWith(updatesRepository.deleteTimestamps(id, timestamps)
                                    .map(deleted -> timestamps.size() == deleted))
                            .map(tuple -> tuple.getT1() && tuple.getT2());
                });
    }

    public Mono<Long> getOldest() {
        return timestampRepository.getAll()
                .map(list -> list.stream().max(Long::compareTo).orElseThrow());
    }

    public Mono<String> getDbState() {
        return timestampRepository.info();
    }

}
