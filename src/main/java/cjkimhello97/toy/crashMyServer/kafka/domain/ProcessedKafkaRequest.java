package cjkimhello97.toy.crashMyServer.kafka.domain;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Document(collection = "processedKafkaRequest")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ProcessedKafkaRequest {

    @Id
    private String id;
    private String uuid;
}
