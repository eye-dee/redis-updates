package com.redis;

import com.redis.repository.InfoRepository;
import com.redis.repository.JedisSingleton;
import com.redis.repository.jedis.InfoRepositoryJedis;
import com.redis.runner.AllKeysPrinter;
import com.redis.runner.InfoPrinter;
import com.redis.runner.Reader;
import com.redis.runner.Writer;
import com.redis.service.UpdateService;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import static com.redis.repository.InfoRepositorySingleton.INFO_REPOSITORY;
import static com.redis.repository.UpdateServiceSingleton.UPDATE_SERVICE;

public class Launcher {

    private static final Random random = new Random();

    public static final List<String> ALL_GROUPS = Arrays.asList("group A", "group B", "group C", "group D", "group E");

    public static final List<String> ALL_IDS = Arrays.asList("id 1", "id 2", "id 3", "id 4", "id 5");

    public static void main(String[] args) {
        JedisCluster jedis = JedisSingleton.JEDIS.getJedisCluster();
        UpdateService updateService = UPDATE_SERVICE.getUpdateService();
        InfoRepository infoRepositoryJedis = INFO_REPOSITORY.getInfoRepository();

        jedis.getClusterNodes()
                .forEach((name, cluster) -> {
                    Jedis resource = cluster.getResource();
                    Set<String> keys = resource.keys("*");
                    keys.forEach(jedis::del);
                });

        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(5);

        scheduledThreadPoolExecutor.scheduleAtFixedRate(new Writer(updateService), 100, 100, TimeUnit.MILLISECONDS);
        scheduledThreadPoolExecutor.scheduleAtFixedRate(new Reader(updateService), 100, 80, TimeUnit.MILLISECONDS);
        scheduledThreadPoolExecutor.scheduleAtFixedRate(new InfoPrinter(infoRepositoryJedis),
                100, 10_000, TimeUnit.MILLISECONDS);
        scheduledThreadPoolExecutor.scheduleAtFixedRate(new AllKeysPrinter(jedis), 100, 10_000, TimeUnit.MILLISECONDS);
    }

    public static String takeRandomFromList(List<String> list) {
        return list.get(random.nextInt(list.size()));
    }

    public static String generateString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
