package com.redis

import redis.clients.jedis.HostAndPort
import redis.clients.jedis.JedisCluster
import redis.clients.jedis.JedisPoolConfig

Set<HostAndPort> jedisClusterNode = new HashSet<>()
jedisClusterNode.add(new HostAndPort("localhost", 7000))
JedisPoolConfig cfg = new JedisPoolConfig()
cfg.setMaxTotal(16)
cfg.setMaxIdle(8)
cfg.setMaxWaitMillis(10000)
cfg.setTestOnBorrow(true)
jedis = new JedisCluster(jedisClusterNode, 10000, 1, cfg)
println(jedis.get("key"))
println(jedis.get("key1"))
println(jedis.get("key2"))
