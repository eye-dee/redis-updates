package com.redis.repository.jedis;

import com.redis.repository.WatchdogRepository;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class WatchdogRepositoryJedis implements WatchdogRepository {

    private final JedisCluster jedisCluster;

    public WatchdogRepositoryJedis(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    @Override
    public Map<String, UUID> initCluster() {
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        Map<String, UUID> res = new HashMap<>();

        for (Map.Entry<String, JedisPool> entry : clusterNodes.entrySet()) {
            Jedis resource = entry.getValue().getResource();
            String role = role(resource);
            if (role.equals("master")) {
                while (true) {
                    UUID uuid = UUID.randomUUID();
                    try {
                        resource.set(uuid.toString(), "true");
                        res.put(entry.getKey(), uuid);
                        break;
                    } catch (RuntimeException ex) {
                        System.out.println(ex.getMessage());
                        System.out.println("uuid = " + uuid);
                    }
                }
                resource.close();
            }

        }
        return res;
    }

    public void assertAlive(Map<String, UUID> uniqueKeys) {
        for (String key : uniqueKeys.keySet()) {
            UUID uuid = uniqueKeys.get(key);
            Optional<String> repositoryKey = getKey(uuid.toString());
            if (!repositoryKey.isPresent()) {
                throw new RuntimeException("ip key for resource = " + key + " unique key = " + uuid + " DOESN'T FOUND");
            }
        }
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
