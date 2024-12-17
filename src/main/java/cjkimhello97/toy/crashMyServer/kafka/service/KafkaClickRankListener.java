package cjkimhello97.toy.crashMyServer.kafka.service;

import cjkimhello97.toy.crashMyServer.kafka.domain.ProcessedKafkaRequest;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaClickRankRequest;
import cjkimhello97.toy.crashMyServer.kafka.repository.ProcessedKafkaRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.RebalanceInProgressException;
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
            id = "clickRankListener1",
            groupId = "clickRankListener",
            topics = "click-rank",
            containerFactory = "kafkaClickRankRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenClickRankTopic1(ConsumerRecord<String, KafkaClickRankRequest> record, Acknowledgment acknowledgment) {
        listenClickRankTopic(record, acknowledgment);
    }

    @Transactional
    @KafkaListener(
            id = "clickRankListener2",
            groupId = "clickRankListener",
            topics = "click-rank",
            containerFactory = "kafkaClickRankRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenClickRankTopic2(ConsumerRecord<String, KafkaClickRankRequest> record, Acknowledgment acknowledgment) {
        listenClickRankTopic(record, acknowledgment);
    }

    @Transactional
    @KafkaListener(
            id = "clickRankListener3",
            groupId = "clickRankListener",
            topics = "click-rank",
            containerFactory = "kafkaClickRankRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenClickRankTopic3(ConsumerRecord<String, KafkaClickRankRequest> record, Acknowledgment acknowledgment) {
        listenClickRankTopic(record, acknowledgment);
    }

    @Transactional
    @KafkaListener(
            id = "clickRankListener4",
            groupId = "clickRankListener",
            topics = "click-rank",
            containerFactory = "kafkaClickRankRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenClickRankTopic4(ConsumerRecord<String, KafkaClickRankRequest> record, Acknowledgment acknowledgment) {
        listenClickRankTopic(record, acknowledgment);
    }

    @Transactional
    @KafkaListener(
            id = "clickRankListener5",
            groupId = "clickRankListener",
            topics = "click-rank",
            containerFactory = "kafkaClickRankRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenClickRankTopic5(ConsumerRecord<String, KafkaClickRankRequest> record, Acknowledgment acknowledgment) {
        listenClickRankTopic(record, acknowledgment);
    }

    private void listenClickRankTopic(ConsumerRecord<String, KafkaClickRankRequest> record, Acknowledgment acknowledgment) {
        log.info("listen click rank = {}", record);

        String uuid = record.value().getUuid();
        boolean isProcessed = false;
        try {
            if (!processedKafkaRequestRepository.existsByUuid(uuid)) {
                messagingTemplate.convertAndSend("/sub/click-rank", record.value());
                isProcessed = true;
            } else {
                log.info("uuid({}) message has already been processed", uuid);
            }
        } catch (RebalanceInProgressException e) {
            log.error("re-balancing error: {}", e.getMessage());
        } catch (KafkaException e) {
            log.error("processing error: {}", e.getMessage());
        } catch (Exception e) {
            log.error("unknown error: {}", e.getMessage());
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
