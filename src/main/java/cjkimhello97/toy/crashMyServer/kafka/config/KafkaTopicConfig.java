package cjkimhello97.toy.crashMyServer.kafka.config;

import cjkimhello97.toy.crashMyServer.kafka.constant.KafkaConstants;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {

    private final KafkaConstants kafkaConstants;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> adminProps = new HashMap<>();
        adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConstants.KAFKA_BROKER);
        adminProps.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000); // 메타데이터 요청 타임아웃
        adminProps.put(AdminClientConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 60000); // 브로커 연결 유지 최대 시간
        return new KafkaAdmin(adminProps);
    }

    @Bean
    public NewTopic groupChatTopic() {
        return TopicBuilder
                .name("group-chat")
                .partitions(100)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic enterTopic() {
        return TopicBuilder
                .name("enter")
                .partitions(100)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic leaveTopic() {
        return TopicBuilder
                .name("leave")
                .partitions(100)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic clickTopic() {
        return TopicBuilder
                .name("click")
                .partitions(100)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic clickRankTopic() {
        return TopicBuilder
                .name("click-rank")
                .partitions(100)
                .replicas(1)
                .build();
    }
}
