package com.redis.repository;

public enum InProgressRepoSingleton {
    IN_PROGRESS_REPO_SINGLETON(SingletonFactory.getInProgressRepository());

    private final InProgressRepository inProgressRepository;

    InProgressRepoSingleton(InProgressRepository inProgressRepository) {
        this.inProgressRepository = inProgressRepository;
    }

    public InProgressRepository getInProgressRepository() {
        return inProgressRepository;
    }
}

