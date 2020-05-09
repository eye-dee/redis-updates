package com.redis.repository.jedis;

import com.redis.repository.AssertionUtil;
import com.redis.repository.InfoRepository;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class InfoRepositoryJedis implements InfoRepository {

    private final JedisCluster jedis;

    public InfoRepositoryJedis(JedisCluster jedis) {
        this.jedis = jedis;
    }

    @Override
    public double info() {
        return getForParameter("used_memory_dataset_perc");
    }

    private double getForParameter(String parameter) {
        AssertionUtil.assertAlive(jedis);
        return jedis.getClusterNodes()
                .entrySet()
                .stream()
                .flatMap(entry -> {
                            String info = getInfoAndClose(entry);
                            if (!role(info).equalsIgnoreCase("master")) {
                                return Stream.empty();
                            }
                            return Arrays.stream(info
                                    .split("\n"))
                                    .map(str -> Pair.of(entry.getKey(), str));
                        }
                )
                .filter(pair -> pair.second.contains(parameter + ":"))
                .map(pair -> Pair.of(pair.first, pair.second
                        .replace(parameter + ":", "")
                        .replace("%", ""))
                )
                .mapToDouble(pair -> Double.parseDouble(pair.second))
                .average()
                .orElse(0.0);
    }

    private String role(String info) {
        return Arrays.stream(info.split("\n"))
                .map(str -> str.split(":"))
                .filter(arr -> arr[0].contains("role"))
                .map(arr -> arr[1])
                .findAny()
                .map(String::trim)
                .orElse("slave");
    }

    private String getInfoAndClose(Map.Entry<String, JedisPool> entry) {
        Jedis resource = entry.getValue()
                .getResource();
        String result = resource
                .info();
        resource.close();
        return result;
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
