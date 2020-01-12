package com.redis.repository;

public enum InfoRepositorySingleton {
    INFO_REPOSITORY(SingletonFactory.getInfoRepository());

    private final InfoRepository infoRepository;

    InfoRepositorySingleton(InfoRepository infoRepository) {
        this.infoRepository = infoRepository;
    }

    public InfoRepository getInfoRepository() {
        return infoRepository;
    }
}
