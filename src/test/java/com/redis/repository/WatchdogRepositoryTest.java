package com.redis.repository;

import com.redis.repository.jedis.WatchdogRepositoryJedis;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WatchdogRepositoryTest {

    private final JedisCluster jedisCluster = JedisSingleton.JEDIS.getJedisCluster();

    private final WatchdogRepository watchdogRepository = new WatchdogRepositoryJedis(jedisCluster);

    @Test
    void clearAll() {
        jedisCluster.getClusterNodes().forEach((ip, pool) -> {
            Jedis resource = pool.getResource();
            Set<String> keys = resource.keys("*");
            for (String key : keys) {
                try {
                    resource.del(key);
                } catch (RuntimeException ignored) {
                }
            }
            resource.close();
        });
    }

    @Test
    void initClusterAndAssertAlive() {
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();

        jedisCluster.get("asd234234aaasdasda234234234");

        clusterNodes.forEach((ip, node) -> {
            try {
                Jedis resource = node.getResource();

                Set<String> keys = resource.keys("*");
                for (String key : keys) {
                    System.out.println("key = " + key + " ip = " + ip);
                }

                resource.close();
            } catch (RuntimeException ex) {

            }
        });

        clusterNodes.forEach((ip, node) -> System.out.println(ip));

        List<UUID> uuids = watchdogRepository.initCluster();

        assertTrue(uuids.size() > 0);

        for (UUID uuid : uuids) {
            assertNotNull(uuid);
        }

        AssertionUtil.assertAlive(jedisCluster);
    }
}
