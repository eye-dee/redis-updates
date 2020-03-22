package com.redis.builder;

public class ClassFactory {

    public <T> PubSub<T> build(Class<T> tClass) {
        return new Consumer<T>();
    }

    public <T> PubSub<T> build(Class<T> tClass, T object) {
        return new Consumer<T>(object);
    }

    public <T> PubSub<T> build(Class<T> tClass, T object, Props props) {
        if (props == Props.CONSUMER) {
            return new Consumer<T>(object);
        } else {
            return new Producer<T>(object);
        }
    }

    public <T> PubSub<T> build(String className) {
        return new Consumer<T>();
    }

}
