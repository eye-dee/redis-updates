package com.redis.repository.jedis;

import com.redis.repository.InProgressRepository;
import redis.clients.jedis.JedisCluster;

public class InProgressRepositoryJedis implements InProgressRepository {

    private final JedisCluster jedisCluster;

    public InProgressRepositoryJedis(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    @Override
    public boolean takeToProgress(String groupId, String id) {
        return jedisCluster.setnx(generateInProgressId(groupId, id), "true") == 1;
    }

    @Override
    public boolean releaseFromProgress(String groupId, String id) {
        return jedisCluster.del(generateInProgressId(groupId, id)) == 1;
    }

    private String generateInProgressId(String groupId, String id) {
        return groupId + "_in_progress_" + id;
    }
}
