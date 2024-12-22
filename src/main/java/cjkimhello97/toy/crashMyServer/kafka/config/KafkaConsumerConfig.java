package cjkimhello97.toy.crashMyServer.kafka.config;

import static org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL;

import cjkimhello97.toy.crashMyServer.chat.dto.KafkaChatMessageRequest;
import cjkimhello97.toy.crashMyServer.click.dto.KafkaClickRankRequest;
import cjkimhello97.toy.crashMyServer.click.dto.KafkaClickRequest;
import cjkimhello97.toy.crashMyServer.kafka.constant.KafkaConstants;
import cjkimhello97.toy.crashMyServer.kafka.domain.ProcessedKafkaRequest;
import cjkimhello97.toy.crashMyServer.kafka.exception.ProcessedKafkaRequestException;
import com.amazonaws.retry.v2.SimpleRetryPolicy;
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
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.backoff.FixedBackOff;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaConstants kafkaConstants;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaClickRequest> kafkaClickRequestConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaClickRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setAckMode(MANUAL);
        factory.setConsumerFactory(kafkaClickRequestConsumerFactory());
        factory.setConcurrency(5);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, KafkaClickRequest> kafkaClickRequestConsumerFactory() {
        Map<String, Object> consumerProps = new HashMap<>();

        JsonDeserializer<KafkaClickRequest> deserializer = new JsonDeserializer<>(KafkaClickRequest.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConstants.KAFKA_BROKER);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // 오토 커밋 false
        consumerProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000); // 하트비트 3초
        consumerProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000); // 세션 타임아웃 10초
        consumerProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 600000); // 폴 인터벌 10분
        consumerProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100); // 폴 레코드 100개
        return new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaClickRankRequest> kafkaClickRankRequestConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaClickRankRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setAckMode(MANUAL);
        factory.setConsumerFactory(kafkaClickRankRequestConsumerFactory());
        factory.setConcurrency(5);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, KafkaClickRankRequest> kafkaClickRankRequestConsumerFactory() {
        Map<String, Object> consumerProps = new HashMap<>();

        JsonDeserializer<KafkaClickRankRequest> deserializer = new JsonDeserializer<>(KafkaClickRankRequest.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConstants.KAFKA_BROKER);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // 오토 커밋 false
        consumerProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000); // 하트비트 3초
        consumerProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000); // 세션 타임아웃 10초
        consumerProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 600000); // 폴 인터벌 10분
        consumerProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100); // 폴 레코드 100개
        return new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaChatMessageRequest> kafkaChatMessageRequestConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaChatMessageRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setAckMode(MANUAL);
        factory.setConsumerFactory(kafkaChatMessageRequestConsumerFactory());
        factory.setConcurrency(5);

        FixedBackOff fixedBackOff = new FixedBackOff(0L, 0);
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(fixedBackOff);
        errorHandler.addNotRetryableExceptions(ProcessedKafkaRequestException.class);
        errorHandler.setRetryListeners((record, exception, deliveryAttempt) -> {
            System.out.println("\n\n\n재시도 : " + deliveryAttempt + " 레코드 : " + record.value() + "\n\n\n");
        });
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

    @Bean
    public ConsumerFactory<String, KafkaChatMessageRequest> kafkaChatMessageRequestConsumerFactory() {
        Map<String, Object> consumerProps = new HashMap<>();

        JsonDeserializer<KafkaChatMessageRequest> deserializer = new JsonDeserializer<>(KafkaChatMessageRequest.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConstants.KAFKA_BROKER);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // 오토 커밋 false
        consumerProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000); // 하트비트 3초
        consumerProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000); // 세션 타임아웃 10초
        consumerProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 600000); // 폴 인터벌 10분
        consumerProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100); // 폴 레코드 100개
        return new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), deserializer);
    }
}
