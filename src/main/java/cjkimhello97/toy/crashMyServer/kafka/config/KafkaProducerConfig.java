package cjkimhello97.toy.crashMyServer.kafka.config;

import cjkimhello97.toy.crashMyServer.kafka.constant.KafkaConstants;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaChatMessageRequest;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaClickRankRequest;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaClickRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaConstants kafkaConstants;

    @Bean
    public ProducerFactory<String, KafkaChatMessageRequest> kafkaChatMessageRequestProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, KafkaChatMessageRequest> kafkaChatMessageRequestTemplate() {
        return new KafkaTemplate<>(kafkaChatMessageRequestProducerFactory());
    }

    @Bean
    public ProducerFactory<String, KafkaClickRequest> kafkaClickRequestProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, KafkaClickRequest> kafkaClickRequestKafkaTemplate() {
        return new KafkaTemplate<>(kafkaClickRequestProducerFactory());
    }

    @Bean
    public ProducerFactory<String, KafkaClickRankRequest> kafkaClickRankRequestProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, KafkaClickRankRequest> kafkaClickRankRequestKafkaTemplate() {
        return new KafkaTemplate<>(kafkaClickRankRequestProducerFactory());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConstants.KAFKA_BROKER);
        producerProps.put(ProducerConfig.ACKS_CONFIG, "all");
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return producerProps;
    }
}
