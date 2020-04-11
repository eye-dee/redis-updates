package com.kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class MyKafkaConsumer2 {

    private String topic;

    private String groupId;

    private Consumer<String, String> consumer;

    private volatile boolean assigned;

    private String bootstrapServers;

    public static void main(String... a) throws InterruptedException, ExecutionException {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "yyyy");
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("cccc"));
        for (int i = 0; i < 40; i++) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records)
                System.out.printf(
                        "FIRST offset = %d, key = %s, value = %s%n",
                        record.offset(), record.key(), record.value());
            consumer.commitSync();
        }

        consumer.unsubscribe();
        consumer.close();
//        executorService.execute(() -> {
//            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
//            consumer.subscribe(Collections.singletonList("bbbb"));
//            while (true) {
//                ConsumerRecords<String, String> records = consumer.poll(100);
//                for (ConsumerRecord<String, String> record : records)
//                    System.out.printf(
//                            "SECOND offset = %d, key = %s, value = %s%n",
//                            record.offset(), record.key(), record.value());
//            }
//        });

    }
}
