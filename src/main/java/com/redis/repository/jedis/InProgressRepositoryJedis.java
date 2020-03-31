package com.redis.repository.jedis;

import com.redis.repository.InProgressRepository;
import redis.clients.jedis.JedisCluster;

public class InProgressRepositoryJedis implements InProgressRepository {

    private final JedisCluster jedisCluster;

    public InProgressRepositoryJedis(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    @Override
    public boolean takeToProgress(String groupId, String id, int timeout) {
        String key = generateInProgressId(groupId, id);
        if (jedisCluster.setnx(key, "true") == 1) {
            return jedisCluster.expire(key, timeout) == 1;
        } else {
            return false;
        }
    }

    @Override
    public boolean releaseFromProgress(String groupId, String id) {
        return jedisCluster.del(generateInProgressId(groupId, id)) == 1;
    }

    private String generateInProgressId(String groupId, String id) {
        return groupId + "_in_progress_" + id;
    }
}
