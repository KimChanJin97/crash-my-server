package cjkimhello97.toy.crashMyServer.kafka.service;

import cjkimhello97.toy.crashMyServer.kafka.domain.ProcessedKafkaRequest;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaChatMessageRequest;
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
public class KafkaLeaveListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ProcessedKafkaRequestRepository processedKafkaRequestRepository;

    @Transactional
    @KafkaListener(
            id = "leaveListener1",
            topics = "leave",
            groupId = "leaveListener",
            containerFactory = "kafkaChatMessageRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenLeaveTopic1(ConsumerRecord<String, KafkaChatMessageRequest> record, Acknowledgment acknowledgment) {
        listenLeaveTopic(record, acknowledgment);
    }

    @Transactional
    @KafkaListener(
            id = "leaveListener2",
            topics = "leave",
            groupId = "leaveListener",
            containerFactory = "kafkaChatMessageRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenLeaveTopic2(ConsumerRecord<String, KafkaChatMessageRequest> record, Acknowledgment acknowledgment) {
        listenLeaveTopic(record, acknowledgment);
    }

    @Transactional
    @KafkaListener(
            id = "leaveListener3",
            topics = "leave",
            groupId = "leaveListener",
            containerFactory = "kafkaChatMessageRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenLeaveTopic3(ConsumerRecord<String, KafkaChatMessageRequest> record, Acknowledgment acknowledgment) {
        listenLeaveTopic(record, acknowledgment);
    }

    @Transactional
    @KafkaListener(
            id = "leaveListener4",
            topics = "leave",
            groupId = "leaveListener",
            containerFactory = "kafkaChatMessageRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenLeaveTopic4(ConsumerRecord<String, KafkaChatMessageRequest> record, Acknowledgment acknowledgment) {
        listenLeaveTopic(record, acknowledgment);
    }

    @Transactional
    @KafkaListener(
            id = "leaveListener5",
            topics = "leave",
            groupId = "leaveListener",
            containerFactory = "kafkaChatMessageRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenLeaveTopic5(ConsumerRecord<String, KafkaChatMessageRequest> record, Acknowledgment acknowledgment) {
        listenLeaveTopic(record, acknowledgment);
    }

    private void listenLeaveTopic(ConsumerRecord<String, KafkaChatMessageRequest> record, Acknowledgment acknowledgment) {
        log.info("listen leave = {}", record);

        String uuid = record.value().getUuid();
        boolean isProcessed = false;
        try {
            if (!processedKafkaRequestRepository.existsByUuid(uuid)) {
                Long chatRoomId = record.value().getChatRoomId();
                messagingTemplate.convertAndSend("/sub/leave/" + chatRoomId, record.value());
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
