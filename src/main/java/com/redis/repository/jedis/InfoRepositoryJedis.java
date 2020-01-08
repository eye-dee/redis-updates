package com.redis.repository.jedis;

import com.redis.repository.InfoRepository;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import redis.clients.jedis.JedisCluster;

public class InfoRepositoryJedis implements InfoRepository {

    private final JedisCluster jedis;

    public InfoRepositoryJedis(JedisCluster jedis) {
        this.jedis = jedis;
    }

    @Override
    public Map<String, Double> info() {
        return jedis.getClusterNodes()
                .entrySet()
                .stream()
                .flatMap(entry -> Arrays.stream(entry.getValue()
                        .getResource()
                        .info()
                        .split("\n"))
                        .map(str -> Pair.of(entry.getKey(), str))
                )
                .filter(pair -> pair.second.contains("used_memory_dataset_perc"))
                .map(pair -> Pair.of(pair.first, pair.second
                        .replace("used_memory_dataset_perc:", "")
                        .replace("%", "")
                ))
                .map(pair -> Pair.of(pair.first, Double.parseDouble(pair.second)))
                .collect(Collectors.toMap(pair -> pair.first, pair -> pair.second));
    }

    static class Pair<T1, T2> {

        T1 first;

        T2 second;

        public Pair(T1 first, T2 second) {
            this.first = first;
            this.second = second;
        }

        static <T1, T2> Pair<T1, T2> of(T1 first, T2 second) {
            return new Pair<>(first, second);
        }
    }
}
