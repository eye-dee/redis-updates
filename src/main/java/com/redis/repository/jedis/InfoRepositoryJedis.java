package com.redis.repository.jedis;

import com.redis.repository.InfoRepository;
import java.util.Arrays;
import redis.clients.jedis.JedisCluster;

public class InfoRepositoryJedis implements InfoRepository {

    private final JedisCluster jedis;

    public InfoRepositoryJedis(JedisCluster jedis) {
        this.jedis = jedis;
    }

    @Override
    public double info() {
        double usedMemory = getForParameter("used_memory");
        double totalSystemMemory = getForParameter("total_system_memory");
        return usedMemory / totalSystemMemory * 100;
    }

    private double getForParameter(String parameter) {
        return jedis.getClusterNodes()
                .entrySet()
                .stream()
                .flatMap(entry -> Arrays.stream(entry.getValue()
                        .getResource()
                        .info()
                        .split("\n"))
                        .map(str -> Pair.of(entry.getKey(), str))
                )
                .filter(pair -> pair.second.contains(parameter + ":"))
                .map(pair -> Pair.of(pair.first, pair.second
                        .replace(parameter + ":", "")
                        .replace("%", ""))
                )
                .mapToDouble(pair -> Double.parseDouble(pair.second))
                .sum();
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
