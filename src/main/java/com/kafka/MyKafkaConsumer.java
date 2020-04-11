package com.kafka;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class MyKafkaConsumer {

    private String topic;

    private String groupId;

    private Consumer<String, String> consumer;

    private volatile boolean assigned;

    private String bootstrapServers;

    public static void main(String... a) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            MyKafkaConsumer myKafkaConsumer1 = new MyKafkaConsumer("localhost:9092", "11", "bbbb");
            final Iterable<Object> iterable1 = myKafkaConsumer1.poll();
            if (iterable1 != null && iterable1.iterator().hasNext()) {
                System.out.println("cons1 msg = " + iterable1.iterator().next());
            }
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final Iterable<Object> iterable11 = myKafkaConsumer1.poll();
            if (iterable11 != null && iterable11.iterator().hasNext()) {
                System.out.println("cons1 msg = " + iterable11.iterator().next());
            }
            myKafkaConsumer1.close();
        });
        MyKafkaConsumer myKafkaConsumer2 = new MyKafkaConsumer("localhost:9092", "11", "cccc");
        System.out.println("polling");
        final Iterable<Object> iterable2 = myKafkaConsumer2.poll();
        if (iterable2 != null && iterable2.iterator().hasNext()) {
            System.out.println("cons2 msg = " + iterable2.iterator().next());
        }

        myKafkaConsumer2.close();
        future.join();
    }

    MyKafkaConsumer(String bootstrapServers, String group, String topic) {
        this.topic = topic;
        groupId = group;
        this.bootstrapServers = bootstrapServers;
        consumer = new KafkaConsumer<>(initProps());
    }

    private Properties initProps() {
        Properties props = new Properties();

        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", groupId);
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "earliest");

        props.put("max.poll.records", 1);

        return props;
    }

    private void assign() {
        System.out.println("Start polling for " + topic + " on " + bootstrapServers);
        consumer.subscribe(Collections.singleton(topic));
        assigned = true;
    }

    public Iterable<Object> poll() {
        if (!assigned) {
            assign();
        }

        List<Object> list = new ArrayList<>();
        ConsumerRecords<String, String> records;
        records = consumer.poll(Duration.ofMillis(5000));
        int count = records.count();
        if (count > 0) {
            list.addAll(StreamSupport.stream(records.records(topic).spliterator(), false)
                    .map(ConsumerRecord::value)
                    .collect(Collectors.toList()));
        }

        consumer.commitSync();

        return list.isEmpty() ? null : list;
    }

    public void close() {
        System.out.println("Closing . . . ");
        consumer.unsubscribe();
        consumer.close();
        System.out.println("Closed . . . ");
    }
}
