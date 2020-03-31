package com.redis.runner;

import com.redis.Launcher;
import com.redis.repository.GroupIdRepository;

public class OldestReader implements Runnable {

    private final GroupIdRepository groupIdRepository;

    public OldestReader(GroupIdRepository groupIdRepository) {
        this.groupIdRepository = groupIdRepository;
    }

    @Override
    public void run() {
        for (String group : Launcher.ALL_GROUPS) {
            groupIdRepository.getOldest(group)
                    .ifPresent(oldest -> System.out.println("for group = " + group + " oldest if = " + oldest));
        }
    }
}
