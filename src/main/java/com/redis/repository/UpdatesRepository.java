package com.redis.repository;

import java.util.List;
import java.util.Map;

public interface UpdatesRepository {

    Boolean addNewUpdates(Long id, Long timestamp, List<String> updates);

    Map<Long, List<String>> getById(Long id);

    Boolean keyExists(Long id, Long key);

    Long deleteTimestamps(Long id, List<Long> timestamps);
}
