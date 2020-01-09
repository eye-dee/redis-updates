package com.redis.runner;

import com.redis.Launcher;
import com.redis.repository.model.Message;
import com.redis.service.UpdateService;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Writer implements Runnable {

    private static final Random random = new Random();

    private final UpdateService updateService;

    public Writer(UpdateService updateService) {
        this.updateService = updateService;
    }

    @Override
    public void run() {
        String group = Launcher.takeRandomFromList(Launcher.ALL_GROUPS);
        String id = Launcher.takeRandomFromList(Launcher.ALL_IDS);

        boolean handled = updateService.handleNewUpdates(group, id, generateMessages());

        if (!handled) {
            System.out.println("group " + group + " with id " + id + " " + " was not added");
        }
    }

    public static List<Message> generateMessages() {
        return Stream.generate(Launcher::generateString)
                .limit(10 + random.nextInt(10))
                .map(str -> new Message(str, str))
                .collect(Collectors.toList());
    }
}
