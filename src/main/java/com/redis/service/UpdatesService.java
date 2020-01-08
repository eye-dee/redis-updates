package com.redis.service;

import com.redis.model.MyMsg;
import com.redis.repository.TimestampRepository;
import com.redis.repository.UpdatesRepository;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class UpdatesService {

    private final UpdatesRepository updatesRepository;

    private final TimestampRepository timestampRepository;

    public UpdatesService(UpdatesRepository updatesRepository, TimestampRepository timestampRepository) {
        this.updatesRepository = updatesRepository;
        this.timestampRepository = timestampRepository;
    }

    public boolean addNewUpdates(String groupId, Long timestamp, List<MyMsg> updates) {
        return updatesRepository.addNewUpdates(groupId, timestamp, updates)
                && timestampRepository.addOrOverride(groupId, timestamp);
    }

    public boolean deleteForGroup(String groupId) {
        return updatesRepository.deleteForGroup(groupId) && timestampRepository.deleteForGroup(groupId);
    }

    public boolean deleteTimestampFromGroup(String groupId, Long timestamp) {
        timestampRepository.deleteTimestamp(timestamp);
        return updatesRepository.deleteTimestamps(groupId, Collections.singletonList(timestamp)) == 1;
    }

    public Boolean deleteOldUpdates(String groupId) {
        Map<Long, List<MyMsg>> byId = updatesRepository.getById(groupId);
        Long max = byId.keySet()
                .stream()
                .max(Comparator.comparingLong(l -> l))
                .orElseThrow(RuntimeException::new);
        List<Long> timestamps = byId.keySet()
                .stream()
                .filter(timestamp -> timestamp < max)
                .collect(Collectors.toList());
        return updatesRepository.deleteTimestamps(groupId, timestamps) == timestamps.size();
    }

    public Optional<String> getOldest() {
        return timestampRepository.getOldest();
    }

    public double getDbAveragePercent() {
        return timestampRepository.info();
    }

}
