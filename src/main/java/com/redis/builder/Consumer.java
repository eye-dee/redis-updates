package com.redis.builder;

public class Consumer<T> implements PubSub<T> {

    private T value;


    public Consumer(T object) {
        value = object;
    }

    public Consumer() {
    }

    public T getValue() {
        return value;
    }
}
