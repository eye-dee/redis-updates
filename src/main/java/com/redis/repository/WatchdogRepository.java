package com.redis.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WatchdogRepository {

    List<UUID> initCluster();

    Optional<String> getKey(String key);
}
