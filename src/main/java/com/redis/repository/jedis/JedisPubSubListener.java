package com.redis.repository.jedis;

import com.redis.repository.GroupIdRepository;
import com.redis.repository.InProgressRepository;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;

public class JedisPubSubListener extends JedisPubSub {

    private final GroupIdRepository groupIdRepository;
    private final InProgressRepository inProgressRepository;
    private final JedisCluster jedisCluster;

    public JedisPubSubListener(GroupIdRepository groupIdRepository, InProgressRepository inProgressRepository, JedisCluster jedisCluster) {
        this.groupIdRepository = groupIdRepository;
        this.inProgressRepository = inProgressRepository;
        this.jedisCluster = jedisCluster;
    }

    public void onPMessage(String pattern, String channel, String message) {
        System.out.println("onPMessage (pattern)" + pattern + " (channel)" + channel + " (message)" + message);

        String originalKey = message.replace("-expire", "");
        if (jedisCluster.exists(originalKey)) {
            String[] splitted = originalKey.split("_in_progress_");
            String groupId = splitted[0];
            String id = splitted[1];
            System.out.println("ERROR during handling for group = " + groupId + " and id = " + id);
            groupIdRepository.addToTheEndForGroup(groupId, id);
            inProgressRepository.releaseFromProgress(groupId, id);
        }
    }
}
