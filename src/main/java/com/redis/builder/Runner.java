package com.redis.builder;

public class Runner {

    public static void main(String[] args) {
        ClassFactory classFactory = new ClassFactory();
        PubSub<String> build = classFactory.build(String.class);
        PubSub<String> build2 = classFactory.build(String.class, "srting");

    }

}
