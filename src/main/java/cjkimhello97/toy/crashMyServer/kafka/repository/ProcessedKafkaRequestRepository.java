package cjkimhello97.toy.crashMyServer.kafka.repository;

import cjkimhello97.toy.crashMyServer.kafka.domain.ProcessedKafkaRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedKafkaRequestRepository extends MongoRepository<ProcessedKafkaRequest, Long> {

    ProcessedKafkaRequest save(ProcessedKafkaRequest processedKafkaRequest);
    boolean existsByUuid(String uuid);
}
