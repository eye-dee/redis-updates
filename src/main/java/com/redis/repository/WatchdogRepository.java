package com.redis.repository;

import java.util.List;
import java.util.Optional;

public interface WatchdogRepository {

    List<String> initCluster();

    Optional<String> getKey(String key);
}
