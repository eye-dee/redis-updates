package com.redis;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

public class RedisClusterIntegrationTest {

    public static JedisCluster jedis;

    private static Random random = new Random();

    @BeforeAll
    static void setUp() {
        Set<HostAndPort> jedisClusterNode = new HashSet<>();
        jedisClusterNode.add(new HostAndPort("localhost", 7000));
        JedisPoolConfig cfg = new JedisPoolConfig();
        cfg.setMaxTotal(16);
        cfg.setMaxIdle(8);
        cfg.setMaxWaitMillis(10000);
        cfg.setTestOnBorrow(true);
        jedis = new JedisCluster(jedisClusterNode, 10000, 1, cfg);
    }

    @Test
    public void printAllClusters() {
        jedis.getClusterNodes().forEach((host, value) -> System.out.println(String.format("cluster name %s", host)));
    }

    @Test
    public void loadLotsOfDataToCluster() {

        CompletableFuture<Void> all =
                CompletableFuture.allOf(Stream.generate(RedisClusterIntegrationTest::generateRandomString)
                        .parallel()
                        .limit(10000)
                        .map(str -> CompletableFuture.supplyAsync(() -> jedis.set(str, str))
                                .whenComplete((res, ex) -> System.out.println("res = " + res)))
                        .toArray(CompletableFuture[]::new));

        all
                .whenComplete((res, ex) -> System.out.println("all futures were completed"))
                .join();

        jedis.getClusterNodes()
                .forEach((host, pool) -> {
                    Jedis resource = pool.getResource();
                    Set<String> keys = resource.keys("*");
                    System.out.println(String.format("host %s with keys.size() %d", host, keys.size()));
                });

        //example:
        //host 127.0.0.1:7004 with keys.size() 13342
        //host 127.0.0.1:7005 with keys.size() 13377
        //host 127.0.0.1:7002 with keys.size() 13310
        //host 127.0.0.1:7003 with keys.size() 13310
        //host 127.0.0.1:7000 with keys.size() 13342
        //host 127.0.0.1:7001 with keys.size() 13377
        //as we can see we have pairs and 13377, 13310, 13342 (because we have paired nodes one is master, the second
        // is slave
    }

    @Test
    public void readAllKeysFromEachNode() {
        CompletableFuture[] all =
                jedis.getClusterNodes()
                        .values()
                        .stream()
                        .flatMap(pool -> pool.getResource().keys("*").stream())
                        .parallel()
                        .map(key -> CompletableFuture.supplyAsync(() -> jedis.get(key))
                                .whenComplete((res, ex) ->
                                        System.out.println(String.format("res for key %s is %s", key, res)))
                        )
                        .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(all)
                .whenComplete((res, ex) -> System.out.println("all keys were read"))
                .join();
    }

    private static String generateRandomString() {
        int leftLimit = 97;
        int rightLimit = 122;
        int targetStringLength = 10;

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
