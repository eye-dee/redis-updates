package com.redis.repository;

import com.redis.repository.jedis.GroupIdRepositoryJedis;
import com.redis.repository.jedis.InProgressRepositoryJedis;
import com.redis.repository.jedis.InfoRepositoryJedis;
import com.redis.repository.jedis.UpdatesRepositoryJedis;
import com.redis.repository.jedis.WatchdogRepositoryJedis;
import com.redis.service.UpdateService;
import java.util.HashSet;
import java.util.Set;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

public class SingletonFactory {

    private static final JedisCluster jedis;

    private static final UpdateService updateService;

    private static final InfoRepository infoRepository;

    private static final WatchdogRepository watchdogRepository;

    private static final GroupIdRepository groupIdRepository;

    private static final InProgressRepositoryJedis inProgressRepository;

    static {
        Set<HostAndPort> jedisClusterNode = new HashSet<>();
        jedisClusterNode.add(new HostAndPort("localhost", 7000));
        JedisPoolConfig cfg = new JedisPoolConfig();
        cfg.setMaxTotal(16);
        cfg.setMaxIdle(8);
        cfg.setMaxWaitMillis(10000);
        cfg.setTestOnBorrow(true);
        jedis = new JedisCluster(jedisClusterNode, 10000, 1, cfg);
        groupIdRepository = new GroupIdRepositoryJedis(jedis);
        inProgressRepository = new InProgressRepositoryJedis(jedis);
        updateService = new UpdateService(
                groupIdRepository,
                inProgressRepository,
                new UpdatesRepositoryJedis(jedis));

        infoRepository = new InfoRepositoryJedis(jedis);
        watchdogRepository = new WatchdogRepositoryJedis(jedis);
    }

    public static JedisCluster getJedis() {
        return jedis;
    }

    public static UpdateService getUpdateService() {
        return updateService;
    }

    public static InfoRepository getInfoRepository() {
        return infoRepository;
    }

    public static WatchdogRepository getWatchdogRepository() {
        return watchdogRepository;
    }

    public static GroupIdRepository getGroupIdRepository() {
        return groupIdRepository;
    }
    public static InProgressRepositoryJedis getInProgressRepository() {
        return inProgressRepository;
    }
}
