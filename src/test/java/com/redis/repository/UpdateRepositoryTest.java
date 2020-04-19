package com.redis.repository;

import com.redis.RedisCleaner;
import com.redis.ioc.BeanContainer;
import com.redis.model.Update;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisCluster;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateRepositoryTest {

    private final UpdateRepository updateRepository = BeanContainer.getBean(
            "updateRepository", UpdateRepository.class
    );

    @BeforeAll
    public static void cleanRedis() {
        JedisCluster jedisCluster = BeanContainer.getBean("jedisCluster", JedisCluster.class);
        RedisCleaner.cleanRedis(jedisCluster);
    }

    @Test
    public void testAddNewUpdates() {
        String group = "group1";
        String id = "id1";
        Update update1 = new Update(group, id, "update1", 1);
        Update update2 = new Update(group, id, "update2", 2);

        List<Update> expected1 = Arrays.asList(update1, update2);

        long res = updateRepository.addNewUpdatesForGroup(group, id, expected1);

        assertEquals(2, res);

        List<Update> allUpdates1 = updateRepository.getAllUpdates(group, id);
        assertEquals(expected1, allUpdates1);

        Update update3 = new Update(group, id, "update1", 1);

        long res2 = updateRepository.addNewUpdatesForGroup(group, id, Collections.singletonList(update3));
        assertEquals(3, res2);

        List<Update> expected2 = Arrays.asList(update1, update2, update3);

        List<Update> allUpdates2 = updateRepository.getAllUpdates(group, id);

        assertEquals(expected2, allUpdates2);
    }

    @Test
    public void testGetAllUpdates() {
        String group = "group2";
        String id = "id2";
        Update update1 = new Update(group, id, "update1", 1);
        Update update2 = new Update(group, id, "update2", 2);

        List<Update> expected = Arrays.asList(update1, update2);

        long res1 = updateRepository.addNewUpdatesForGroup(group, id, Collections.singletonList(update1));
        long res2 = updateRepository.addNewUpdatesForGroup(group, id, Collections.singletonList(update2));

        assertEquals(1, res1);
        assertEquals(2, res2);

        List<Update> allUpdates = updateRepository.getAllUpdates(group, id);
        assertEquals(expected, allUpdates);
    }

    @Test
    public void testDeleteUpdates() {
        String group = "group3";
        String id = "id3";
        Update update1 = new Update(group, id, "update1", 1);
        Update update2 = new Update(group, id, "update2", 2);
        Update update3 = new Update(group, id, "update2", 3);

        List<Update> expected1 = Arrays.asList(update1, update2, update3);

        long res = updateRepository.addNewUpdatesForGroup(group, id, expected1);

        assertEquals(3, res);

        List<Update> allUpdates1 = updateRepository.getAllUpdates(group, id);
        assertEquals(expected1, allUpdates1);

        long res2 = updateRepository.deleteElementsFromLeft(group, id, 2);
        assertEquals(1, res2);

        List<Update> expected2 = Collections.singletonList(update3);

        List<Update> allUpdates2 = updateRepository.getAllUpdates(group, id);

        assertEquals(expected2, allUpdates2);

        long res3 = updateRepository.deleteElementsFromLeft(group, id, 1);
        assertEquals(0, res3);

        List<Update> expected3 = Collections.emptyList();

        List<Update> allUpdates3 = updateRepository.getAllUpdates(group, id);

        assertEquals(expected3, allUpdates3);
    }

    @Test
    public void deleteFromEmptyList() {
        String group = "group4";
        String id = "id4";

        long res = updateRepository.deleteElementsFromLeft(group, id, 5);

        assertEquals(0, res);
    }

    @Test
    public void getFromEmptyList() {
        String group = "group5";
        String id = "id5";

        List<Update> allUpdates = updateRepository.getAllUpdates(group, id);

        assertEquals(Collections.emptyList(), allUpdates);
    }
}
