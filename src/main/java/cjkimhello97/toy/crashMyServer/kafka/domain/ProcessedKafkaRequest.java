package cjkimhello97.toy.crashMyServer.kafka.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Setter
@Getter
@Document(collection = "processed_kafka_request")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ProcessedKafkaRequest {

    @MongoId
    private String id;
    private String uuid;
}
