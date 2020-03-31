package com.redis.repository;

import com.redis.repository.jedis.InfoRepositoryJedis;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisCluster;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InfoRepositoryTest {

    private final JedisCluster jedisCluster = JedisSingleton.JEDIS.getJedisCluster();

    private final InfoRepository progressRepository = new InfoRepositoryJedis(jedisCluster);

    @Test
    public void checkUsedMemoryDatasetPerc() {
        double percent = progressRepository.info();
        assertTrue(percent > 0);
        assertTrue(percent < 100);
    }
}
