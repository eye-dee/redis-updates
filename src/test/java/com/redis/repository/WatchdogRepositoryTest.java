package com.redis.repository;

import com.redis.repository.jedis.WatchdogRepositoryJedis;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import static org.junit.jupiter.api.Assertions.*;

class WatchdogRepositoryTest {

    private final JedisCluster jedisCluster = JedisSingleton.JEDIS.getJedisCluster();

    private final WatchdogRepository watchdogRepository = new WatchdogRepositoryJedis(jedisCluster);

    @Test
    void initCluster() {
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();

        Map<String, UUID> stringUUIDMap = watchdogRepository.initCluster();

        assertTrue(stringUUIDMap.size() > 0);

        for (String ip : stringUUIDMap.keySet()) {
            assertNotNull(stringUUIDMap.get(ip));
        }
    }
}
