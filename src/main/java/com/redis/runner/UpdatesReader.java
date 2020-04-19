package com.redis.runner;

import com.redis.Application;
import com.redis.service.UpdateService;
import java.util.Random;

public class UpdatesReader implements Runnable {

    private final Random random = new Random();
    private final UpdateService updateService;

    public UpdatesReader(UpdateService updateService) {
        this.updateService = updateService;
    }

    @Override
    public void run() {
        String groupId = Application.GROUPS.get(random.nextInt(Application.GROUPS.size()));
        updateService.readUpdates(groupId);
    }
}
