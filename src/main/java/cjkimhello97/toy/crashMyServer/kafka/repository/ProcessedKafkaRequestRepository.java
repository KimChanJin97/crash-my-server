package cjkimhello97.toy.crashMyServer.kafka.repository;

import cjkimhello97.toy.crashMyServer.kafka.domain.ProcessedKafkaRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProcessedKafkaRequestRepository extends MongoRepository<ProcessedKafkaRequest, String> {

    boolean existsByUuid(String uuid);
}
