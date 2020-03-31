package com.redis.repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface WatchdogRepository {

    Map<String, UUID> initCluster();

    Optional<String> getKey(String key);
}
