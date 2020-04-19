package com.redis.repository.jedis;

import com.redis.repository.LockRepository;
import java.util.Optional;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.params.SetParams;

public class LockRepositoryJedis implements LockRepository {

    private final JedisCluster jedisCluster;

    public LockRepositoryJedis(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    @Override
    public boolean acquireLockForChange(String group, String id) {
        return acquireLockForKey(generateKeyForChanges(group, id));
    }

    @Override
    public boolean releaseLockForChange(String group, String id) {
        String key = generateKeyForChanges(group, id);
        return jedisCluster.del(key) == 1;
    }

    @Override
    public boolean acquireLockForLogic(String group, String id) {
        return acquireLockForKey(generateKeyForProgress(group, id));
    }

    @Override
    public boolean releaseLockForLogic(String group, String id) {
        String key = generateKeyForProgress(group, id);
        return jedisCluster.del(key) == 1;
    }

    private boolean acquireLockForKey(String key) {
        return Optional.ofNullable(jedisCluster.set(key, "true", SetParams.setParams().nx()))
                .filter(str -> str.equalsIgnoreCase("ok"))
                .flatMap(str -> Optional.ofNullable(jedisCluster.expire(key, 5)))
                .filter(res -> res == 1)
                .isPresent();
    }

    private String generateKeyForChanges(String group, String id) {
        return "delete_lock:group=" + group + ":id=" + id;
    }

    private String generateKeyForProgress(String group, String id) {
        return "progress_lock:group=" + group + ":id=" + id;
    }
}
