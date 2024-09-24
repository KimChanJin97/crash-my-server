package cjkimhello97.toy.crashMyServer.kafka.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class KafkaConstants {

    @Value("${kafka.broker}")
    public String KAFKA_BROKER;

    @Bean
    public String KAFKA_BROKER() {
        return KAFKA_BROKER;
    }
}
