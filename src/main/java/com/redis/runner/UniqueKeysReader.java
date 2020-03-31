package com.redis.runner;

import com.redis.repository.WatchdogRepository;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class UniqueKeysReader implements Runnable {

    private final WatchdogRepository watchdogRepository;

    private final Map<String, UUID> uniqueKeys;

    public UniqueKeysReader(WatchdogRepository watchdogRepository) {
        this.watchdogRepository = watchdogRepository;

        uniqueKeys = watchdogRepository.initCluster();
    }

    @Override
    public void run() {
        for (String key : uniqueKeys.keySet()) {
            UUID uuid = uniqueKeys.get(key);
            Optional<String> repositoryKey = watchdogRepository.getKey(uuid.toString());
            if (repositoryKey.isPresent()) {
                System.out.println("ip key for resource = " + key + " unique key = " + uuid + " found");
            } else {
                System.out.println("ip key for resource = " + key + " unique key = " + uuid + " DOESN'T FOUND");
            }
        }
    }
}
