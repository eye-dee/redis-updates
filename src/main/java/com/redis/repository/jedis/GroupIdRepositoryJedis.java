package com.redis.repository.jedis;

import com.redis.repository.AssertionUtil;
import com.redis.repository.GroupIdRepository;
import java.util.Optional;
import redis.clients.jedis.JedisCluster;

public class GroupIdRepositoryJedis implements GroupIdRepository {

    private final JedisCluster jedisCluster;

    public GroupIdRepositoryJedis(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    @Override
    public boolean addToTheEndForGroup(String groupId, String id) {
        AssertionUtil.assertAlive(jedisCluster);
        return jedisCluster.rpush(groupId, id) > 0;
    }

    @Override
    public Optional<String> takeFromHead(String groupId) {
        AssertionUtil.assertAlive(jedisCluster);
        return Optional.ofNullable(jedisCluster.lpop(groupId));
    }

    @Override
    public Optional<String> getOldest(String groupId) {
        AssertionUtil.assertAlive(jedisCluster);
        return Optional.ofNullable(jedisCluster.llen(groupId))
                .flatMap(len -> Optional.ofNullable(jedisCluster.lindex(groupId, len - 1)));
    }
}
