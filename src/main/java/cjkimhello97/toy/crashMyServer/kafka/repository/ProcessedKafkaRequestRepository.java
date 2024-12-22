package cjkimhello97.toy.crashMyServer.kafka.repository;

import cjkimhello97.toy.crashMyServer.kafka.domain.ProcessedKafkaRequest;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedKafkaRequestRepository extends MongoRepository<ProcessedKafkaRequest, String> {

    boolean existsByUuid(String uuid);
}
