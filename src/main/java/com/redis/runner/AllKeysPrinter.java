package com.redis.runner;

import java.util.Set;
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
                    Set<String> keys = j.getResource().keys("*");
                    System.out.println(keys);
                });

    }
}
