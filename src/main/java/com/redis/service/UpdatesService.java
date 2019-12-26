package com.redis.service;

import com.redis.repository.TimestampRepository;
import com.redis.repository.UpdatesRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdatesService {

    private final UpdatesRepository updatesRepository;

    private final TimestampRepository timestampRepository;

    public boolean addNewUpdates(String id, Long timestamp, List<String> updates) {
        boolean result = updatesRepository.addNewUpdates(id, timestamp, updates);
        timestampRepository.addNewTimestamp(id, timestamp);
        return result;
    }

    public Boolean deleteOldUpdates(String id) {
        Map<Long, List<String>> byId = updatesRepository.getById(id);
        Long max = byId.keySet()
                .stream()
                .max(Comparator.comparingLong(l -> l))
                .orElseThrow();
        List<Long> timestamps = byId.keySet()
                .stream()
                .filter(timestamp -> timestamp < max)
                .collect(Collectors.toList());
        boolean firstResult = timestampRepository.overrideOldValue(id, max);
        boolean secondResult = updatesRepository.deleteTimestamps(id, timestamps) == timestamps.size();
        return firstResult && secondResult;
    }

    public Long getOldest() {
        List<Long> list = timestampRepository.getAll();
        return list.stream().max(Long::compareTo).orElseThrow();
    }

    public String getDbState() {
        return timestampRepository.info();
    }

}
