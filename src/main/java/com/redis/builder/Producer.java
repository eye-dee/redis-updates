package com.redis.builder;

public class Producer<T> implements PubSub<T> {

    private T value;

    public Producer(T object) {
        value = object;
    }

    public Producer() {
    }

    public T getValue() {
        return value;
    }
}
