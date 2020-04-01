package com.redis.runner;

import com.redis.repository.WatchdogRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class UniqueKeysReader implements Runnable {

    private final WatchdogRepository watchdogRepository;

    private final List<UUID> uniqueKeys;

    public UniqueKeysReader(WatchdogRepository watchdogRepository) {
        this.watchdogRepository = watchdogRepository;

        uniqueKeys = watchdogRepository.initCluster();
    }

    @Override
    public void run() {
        for (UUID uuid : uniqueKeys) {
            Optional<String> repositoryKey = watchdogRepository.getKey(uuid.toString());
            if (repositoryKey.isPresent()) {
                System.out.println("unique key = " + uuid + " found");
            } else {
                System.out.println("unique key = " + uuid + " DOESN'T FOUND");
            }
        }
    }
}
