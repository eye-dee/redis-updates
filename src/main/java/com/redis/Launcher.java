package com.redis;

import com.redis.repository.ConnectionFactory;
import com.redis.repository.jedis.GroupIdRepositoryJedis;
import com.redis.repository.jedis.InProgressRepositoryJedis;
import com.redis.repository.jedis.InfoRepositoryJedis;
import com.redis.repository.jedis.UpdatesRepositoryJedis;
import com.redis.repository.model.Message;
import com.redis.service.UpdateService;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

public class Launcher {

    private static final Random random = new Random();

    private static final List<String> ALL_GROUPS = Arrays.asList("group A", "group B", "group C", "group D", "group E");

    private static final List<String> ALL_IDS = Arrays.asList("id 1", "id 2", "id 3", "id 4", "id 5");

    public static void main(String[] args) throws InterruptedException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        JedisCluster jedis = connectionFactory.getJedis();
        UpdateService updateService = new UpdateService(new GroupIdRepositoryJedis(jedis),
                new InProgressRepositoryJedis(jedis),
                new UpdatesRepositoryJedis(jedis));
        InfoRepositoryJedis infoRepositoryJedis = new InfoRepositoryJedis(jedis);

        jedis.getClusterNodes()
                .forEach((name, cluster) -> {
                    Jedis resource = cluster.getResource();
                    Set<String> keys = resource.keys("*");
                    keys.forEach(jedis::del);
                });

        new Thread(() -> {
            while (true) {
                String group = takeRandomFromList(ALL_GROUPS);
                String id = takeRandomFromList(ALL_IDS);

                boolean handled = false;
                try {
                    handled = updateService.handleNewUpdates(group, id, generateMessages());
                } catch (InterruptedException e) {
                    System.out.println(e);
                }

                if (!handled) {
                    System.out.println("group " + group + " with id " + id + " " + " was not added");
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        }).start();

        new Thread(() -> {
            System.out.println(jedis);
            while (true) {
                String group = takeRandomFromList(ALL_GROUPS);
                String id = takeRandomFromList(ALL_IDS);

                boolean handled = false;
                try {
                    handled = updateService.handleUpdatesForGroup(group, 5 + random.nextInt(10));
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
                if (!handled) {
                    System.out.println("group " + group + " with id " + id + " " + " was not handled");
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                System.out.println(infoRepositoryJedis.info());

                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException e) {
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                jedis.getClusterNodes()
                        .values()
                        .forEach(j -> {
                            Set<String> keys = j.getResource().keys("*");
                            System.out.println(keys);
                        });

                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }

    private static String takeRandomFromList(List<String> list) {
        return list.get(random.nextInt(list.size()));
    }

    private static List<Message> generateMessages() {
        return Stream.generate(Launcher::generateString)
                .limit(10 + random.nextInt(10))
                .map(str -> new Message(str, str))
                .collect(Collectors.toList());
    }

    private static String generateString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
