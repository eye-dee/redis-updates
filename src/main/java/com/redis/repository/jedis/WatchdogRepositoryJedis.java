package com.redis.repository.jedis;

import com.redis.repository.AssertionUtil;
import com.redis.repository.WatchdogRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    public List<String> initCluster() {
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        List<String> res = new ArrayList<>();

        boolean hasAnyKey = false;
        Set<String> keys = new HashSet<>();
        for (Map.Entry<String, JedisPool> entry : jedisCluster.getClusterNodes().entrySet()) {
            try {
                Jedis resource = entry.getValue().getResource();
                Set<String> nodeKey = resource.keys("UNIQUE-KEYS-*");
                resource.close();
                if (nodeKey.isEmpty()) {
                    if (!keys.isEmpty()) {
                        keys.clear();
                        break;
                    }
                } else {
                    hasAnyKey = true;
                    keys.addAll(nodeKey);
                }
            } catch (RuntimeException ex) {
                if (!keys.isEmpty()) {
                    keys.clear();
                    break;
                }
            }
        }

        if (keys.isEmpty() && !hasAnyKey) {
            clusterNodes.values()
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
                            String uniqueKey = "UNIQUE-KEYS-" + UUID.randomUUID().toString();
                            long slot = JedisClusterCRC16.getSlot(uniqueKey);
                            while (slot < slots.get(0) || slot > slots.get(1)) {
                                uniqueKey = "UNIQUE-KEYS-" + UUID.randomUUID().toString();
                                slot = JedisClusterCRC16.getSlot(uniqueKey);
                            }
                            jedisCluster.set(uniqueKey, "true");
                            res.add(uniqueKey);
                            AssertionUtil.addKey(uniqueKey);
                        }
                    });
        } else if (keys.isEmpty()) {
            AssertionUtil.openCircuit();
        } else {
            for (String key : keys) {
                AssertionUtil.addKey(key);
                res.add(key);
            }
        }
        return res;
    }

    @Override
    public Optional<String> getKey(String key) {
        return Optional.ofNullable(jedisCluster.get(key));
    }
}
