package com.redis.repository.jedis;

import com.redis.repository.InProgressRepository;
import com.redis.repository.JedisSingleton;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisCluster;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InProgressRepositoryJedisTest {

    private final JedisCluster jedisCluster = JedisSingleton.JEDIS.getJedisCluster();

    private final InProgressRepository progressRepository = new InProgressRepositoryJedis(jedisCluster);

    @Test
    public void testTimeout() throws InterruptedException {
        String groupId = "groupId";
        String id = "id";
        assertTrue(progressRepository.takeToProgress(groupId, id, 1));
        assertFalse(progressRepository.takeToProgress(groupId, id, 1));

        Thread.sleep(1_500);
        assertTrue(progressRepository.takeToProgress(groupId, id, 1));
    }
}
