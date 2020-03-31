package com.redis.repository;

public enum GroupIdRepoSingleton {
    GROUP_ID_REPOSITORY(SingletonFactory.getGroupIdRepository());

    private final GroupIdRepository groupIdRepository;

    GroupIdRepoSingleton(GroupIdRepository groupIdRepository) {
        this.groupIdRepository = groupIdRepository;
    }

    public GroupIdRepository getGroupIdRepository() {
        return groupIdRepository;
    }
}

