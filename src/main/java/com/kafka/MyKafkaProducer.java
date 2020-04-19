package com.kafka;

import java.util.Properties;
import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class MyKafkaProducer {

    public static void main(String... a) throws Exception {
        MyKafkaProducer producer = new MyKafkaProducer();
        producer.send("test");
    }

    private Producer<String, String> producer;

    Future<RecordMetadata> future;

    MyKafkaProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9093");
        props.put("linger.ms", 1);
        props.put("max.block.ms", 60000);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
    }

    public void send(String message) throws Exception {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("myTopic", "myKey", message);
        future = producer.send(producerRecord);
// i don't have future until max.block.ms is over.. then I already get the result!!
        future.get();
    }

    public void close() throws Exception {
        producer.close();
    }

}
