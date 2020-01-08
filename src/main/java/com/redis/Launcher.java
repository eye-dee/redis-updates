package com.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.repository.TimestampRepositoryJedis;
import com.redis.repository.UpdatesRepositoryJedis;
import com.redis.service.UpdatesService;
import java.util.Set;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

public class Launcher {

    public static void main(String[] args) {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        JedisCluster jedis = connectionFactory.getJedis();

        jedis.getClusterNodes()
                .forEach((name, cluster) -> {
                    Jedis resource = cluster.getResource();
                    Set<String> keys = resource.keys("*");
                    keys.forEach(jedis::del);
                });

        TimestampRepositoryJedis timestampRepositoryJedis = new TimestampRepositoryJedis(jedis);

        final UpdatesRepositoryJedis updatesRepositoryJedis = new UpdatesRepositoryJedis(jedis, new ObjectMapper());

        UpdatesService updatesService = new UpdatesService(updatesRepositoryJedis, timestampRepositoryJedis);
    }
}
