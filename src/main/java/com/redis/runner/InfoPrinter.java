package com.redis.runner;

import com.redis.repository.InfoRepository;

public class InfoPrinter implements Runnable {

    private final InfoRepository infoRepository;

    public InfoPrinter(InfoRepository infoRepository) {
        this.infoRepository = infoRepository;
    }

    @Override
    public void run() {
        System.out.println(infoRepository.info());
    }
}
