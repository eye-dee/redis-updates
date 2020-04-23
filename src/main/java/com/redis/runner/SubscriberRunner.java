package com.redis.runner;

import com.redis.repository.GroupIdRepository;
import com.redis.repository.InProgressRepository;
import com.redis.repository.jedis.JedisPubSubListener;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

public class SubscriberRunner implements Runnable {

    private final GroupIdRepository groupIdRepository;
    private final InProgressRepository inProgressRepository;
    private final JedisCluster jedisCluster;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final JedisPubSubListener jedisPubSub;

    public SubscriberRunner(GroupIdRepository groupIdRepository, InProgressRepository inProgressRepository, JedisCluster jedisCluster) {
        this.groupIdRepository = groupIdRepository;
        this.inProgressRepository = inProgressRepository;
        this.jedisCluster = jedisCluster;
        jedisPubSub = new JedisPubSubListener(groupIdRepository, this.inProgressRepository, jedisCluster);
    }

    @Override
    public void run() {
        jedisCluster.getClusterNodes()
                .forEach((ip, node) -> {
                    Jedis resource = node.getResource();
                    System.out.println("ip = " + ip);
                    if (role(resource).equals("master")) {
                        executorService.execute(() -> {
                            System.out.println("started for " + ip);
                            while (true) {
                                try {
                                    resource.psubscribe(jedisPubSub, "__key*__:expired");
                                } catch (RuntimeException ex) {
                                    ex.printStackTrace();
                                }
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ignored) {
                                }
                            }
                        });
                    } else {
                        resource.close();
                    }
                });
    }

    public String role(Jedis resource) {
        return Arrays.stream(resource.info().split("\n"))
                .map(str -> str.split(":"))
                .filter(arr -> arr[0].contains("role"))
                .map(arr -> arr[1])
                .findAny()
                .map(String::trim)
                .orElse("slave");
    }
}
