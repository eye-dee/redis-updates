package com.redis.repository.jedis;

import com.redis.repository.AssertionUtil;
import com.redis.repository.WatchdogRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.util.JedisClusterCRC16;

public class WatchdogRepositoryJedis implements WatchdogRepository {

    private final JedisCluster jedisCluster;

    public WatchdogRepositoryJedis(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    @Override
    public List<UUID> initCluster() {
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        List<UUID> res = new ArrayList<>();

        jedisCluster.getClusterNodes()
                .values()
                .stream()
                .map(JedisPool::getResource)
                .map(r -> {
                    List<Object> slots = r.clusterSlots();
                    r.close();
                    return slots;
                })
                .findFirst()
                .map(list -> list.stream()
                        .map(l -> (List<Object>) l)
                        .map(l -> l.subList(0, 2))
                        .map(l -> l.stream().map(o -> (Long) o).collect(Collectors.toList()))
                        .collect(Collectors.toList()))
                .ifPresent(allSlots -> {
                    for (List<Long> slots : allSlots) {
                        UUID uuid = UUID.randomUUID();
                        long slot = JedisClusterCRC16.getSlot(uuid.toString());
                        while (slot < slots.get(0) || slot > slots.get(1)) {
                            uuid = UUID.randomUUID();
                            slot = JedisClusterCRC16.getSlot(uuid.toString());
                        }
                        jedisCluster.set(uuid.toString(), "true");
                        res.add(uuid);
                        AssertionUtil.addKey(uuid);
                    }
                });

        return res;
    }

    @Override
    public Optional<String> getKey(String key) {
        return Optional.ofNullable(jedisCluster.get(key));
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
