package com.redis.runner;

import com.redis.Launcher;
import com.redis.repository.model.Message;
import com.redis.service.UpdateService;
import java.util.List;
import java.util.Random;

public class Reader implements Runnable {

    private final UpdateService updateService;

    public Reader(UpdateService updateService) {
        this.updateService = updateService;
    }

    @Override
    public void run() {
        String group = Launcher.takeRandomFromList(Launcher.ALL_GROUPS);

        updateService.takeOldestId(group)
                .ifPresent(id -> {
                    List<Message> updates = updateService.readAllUpdates(group, id);
                    updates.forEach(System.out::println);
                    if (new Random().nextInt(100) > 92) {
                        System.out.println("simulated error for group = " + group + " with id = " + id);
                        return;
                    }
                    boolean handled = updateService.deleteOldUpdates(group, id, updates.size());

                    if (!handled) {
                        System.out.println("group " + group + " with id " + id + " size = " + updates.size());
                        System.out.println("group " + group + " with id " + id + " " + " was not handled");
                    } else {
                        System.out.println("group " + group + " with id " + id + " " + " handled success");
                    }
                });

    }
}
