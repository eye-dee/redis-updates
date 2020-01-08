package com.redis.repository;

import java.util.List;
import java.util.Map;

public interface UpdatesRepository {

    Boolean addNewUpdates(String id, Long timestamp, List<String> updates);

    Map<Long, List<String>> getById(String id);

    Boolean keyExists(String id, Long key);

    Long deleteTimestamps(String id, List<Long> timestamps);
}
