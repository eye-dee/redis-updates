package com.redis.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RedisMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RedisMapper redisMapper = new RedisMapper(objectMapper);

    @Test
    public void toEntityTest() {
        SimpleModel model = new SimpleModel("some value");
        String actual = redisMapper.toRedisEntity(model);

        assertEquals("{\"field\":\"some value\"}", actual);
    }

    @Test
    public void fromStringTest() {
        SimpleModel expected = new SimpleModel("some value");
        SimpleModel actual = redisMapper.fromString("{\"field\": \"some value\"}", SimpleModel.class);

        assertEquals(expected, actual);
    }
}

class SimpleModel {
    private final String field;

    @JsonCreator
    SimpleModel(@JsonProperty("field") String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleModel that = (SimpleModel) o;
        return field.equals(that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field);
    }
}
