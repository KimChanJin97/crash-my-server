package cjkimhello97.toy.crashMyServer.kafka.config;

import cjkimhello97.toy.crashMyServer.kafka.constant.KafkaConstants;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaChatMessageRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaConstants kafkaConstants;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaChatMessageRequest> kafkaChatMessageRequestConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaChatMessageRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaChatMessageRequestConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, KafkaChatMessageRequest> kafkaChatMessageRequestConsumerFactory() {
        Map<String, Object> consumerProps = new HashMap<>();

        JsonDeserializer<KafkaChatMessageRequest> deserializer = new JsonDeserializer<>(KafkaChatMessageRequest.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("cjkimhello97.toy.crashMyServer.kafka.dto.KafkaChatMessageRequest");
        deserializer.addTrustedPackages("cjkimhello97.toy.crashMyServer.kafka.dto.KafkaClickRequest");
        deserializer.addTrustedPackages("cjkimhello97.toy.crashMyServer.kafka.dto.KafkaClickRankRequest");
        deserializer.setUseTypeMapperForKey(true);

        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConstants.KAFKA_BROKER);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), deserializer);
    }
}
