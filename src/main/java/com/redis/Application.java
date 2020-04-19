package com.redis;

import com.redis.ioc.BeanContainer;
import com.redis.runner.UpdatesGenerator;
import com.redis.runner.UpdatesReader;
import com.redis.service.UpdateService;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Application {

    public static final List<String> GROUPS = Arrays.asList("GROUP_A", "GROUP_B", "GROUP_C");

    public static final List<String> IDS = Arrays.asList("ID_1", "ID_2", "ID_3");

    private static final ScheduledExecutorService service = Executors.newScheduledThreadPool(10);

    public static void main(String[] args) {
        UpdateService updateService = BeanContainer.getBean("updateService", UpdateService.class);
        service.scheduleAtFixedRate(
                new UpdatesGenerator(updateService), 100, 100, TimeUnit.MILLISECONDS
        );

        service.scheduleAtFixedRate(
                new UpdatesReader(updateService), 100, 100, TimeUnit.MILLISECONDS
        );
    }

}
