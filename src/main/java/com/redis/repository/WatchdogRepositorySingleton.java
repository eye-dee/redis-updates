package com.redis.repository;

public enum WatchdogRepositorySingleton {
    WATCHDOG_REPOSITORY_SINGLETON(SingletonFactory.getWatchdogRepository());

    private final WatchdogRepository watchdogRepository;

    WatchdogRepositorySingleton(WatchdogRepository watchdogRepository) {
        this.watchdogRepository = watchdogRepository;
    }

    public WatchdogRepository getWatchdogRepository() {
        return watchdogRepository;
    }
}
