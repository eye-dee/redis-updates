package com.redis.repository;

import com.redis.service.UpdateService;

public enum UpdateServiceSingleton {
    UPDATE_SERVICE(SingletonFactory.getUpdateService());

    private final UpdateService updateService;

    UpdateServiceSingleton(UpdateService updateService) {
        this.updateService = updateService;
    }

    public UpdateService getUpdateService() {
        return updateService;
    }
}
