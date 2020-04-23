package com.redis.runner;

import java.util.Set;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

public class AllKeysPrinter implements Runnable {

    private final JedisCluster jedis;

    public AllKeysPrinter(JedisCluster jedis) {
        this.jedis = jedis;
    }

    @Override
    public void run() {
        jedis.getClusterNodes()
                .values()
                .forEach(j -> {
                    Jedis resource = j.getResource();
                    Set<String> keys = resource.keys("*");
                    System.out.println(keys);
                    resource.close();
                });

    }
}
