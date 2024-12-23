package cjkimhello97.toy.crashMyServer.kafka.service;

import static cjkimhello97.toy.crashMyServer.kafka.exception.ProcessedKafkaRequestExceptionType.ALREADY_PROCESSED_MESSAGE;

import cjkimhello97.toy.crashMyServer.kafka.domain.ProcessedKafkaRequest;
import cjkimhello97.toy.crashMyServer.click.dto.KafkaClickRankRequest;
import cjkimhello97.toy.crashMyServer.kafka.exception.ProcessedKafkaRequestException;
import cjkimhello97.toy.crashMyServer.kafka.repository.ProcessedKafkaRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaClickRankListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ProcessedKafkaRequestRepository processedKafkaRequestRepository;

    @Transactional
    @KafkaListener(
            id = "clickRankListener0",
            groupId = "clickRankListener",
            topics = "click-rank",
            containerFactory = "kafkaClickRankRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenClickRankTopic(
            KafkaClickRankRequest request,
            Acknowledgment acknowledgment
    ) {
        String uuid = request.getUuid();
        boolean isProcessed = false;
        try {
            if (processedKafkaRequestRepository.existsByUuid(uuid)) {
                throw new ProcessedKafkaRequestException(ALREADY_PROCESSED_MESSAGE);
            }
            messagingTemplate.convertAndSend("/sub/click-rank", request);
            isProcessed = true;
        } finally {
            if (isProcessed) {
                ProcessedKafkaRequest processedKafkaRequest = new ProcessedKafkaRequest();
                processedKafkaRequest.setUuid(uuid);
                processedKafkaRequestRepository.save(processedKafkaRequest);
                acknowledgment.acknowledge();
            }
        }
    }
}
