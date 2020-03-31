package com.redis.repository;

import java.util.Map;
import java.util.UUID;

public interface WatchdogRepository {

    Map<String, UUID> initCluster();
}
