package com.redis.repository;

import com.redis.model.Update;
import java.util.List;

public interface UpdateRepository {

    long addNewUpdatesForGroup(String group, String id, Update update);

    long deleteElementsFromLeft(String group, String id, int number);

    List<Update> getAllUpdates(String group, String id);
}
