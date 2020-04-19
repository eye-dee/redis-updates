package com.redis.runner;

import com.redis.Application;
import com.redis.model.Update;
import com.redis.service.UpdateService;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UpdatesGenerator implements Runnable {

    private final Random random = new Random();

    private final Map<String, Long> offsets = new HashMap<>();

    {
        Application.GROUPS.forEach(gr -> offsets.put(gr, 0L));
    }

    private final UpdateService updateService;

    public UpdatesGenerator(UpdateService updateService) {
        this.updateService = updateService;
    }

    @Override
    public void run() {
        String groupId = Application.GROUPS.get(random.nextInt(Application.GROUPS.size()));
        String id = Application.IDS.get(random.nextInt(Application.GROUPS.size()));
        updateService.handleNewUpdate(
                groupId,
                id,
                new Update(groupId, id, randomString(), offsets.get(groupId)));
        offsets.computeIfPresent(groupId, (gr, of) -> of + 1);
    }

    private String randomString() {
        int leftLimit = 97;
        int rightLimit = 122;
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

}
