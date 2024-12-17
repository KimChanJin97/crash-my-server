package cjkimhello97.toy.crashMyServer.kafka.service;

import cjkimhello97.toy.crashMyServer.kafka.domain.ProcessedKafkaRequest;
import cjkimhello97.toy.crashMyServer.kafka.repository.ProcessedKafkaRequestRepository;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaChatMessageRequest;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaGroupChatListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ProcessedKafkaRequestRepository processedKafkaRequestRepository;

    @Transactional
    @KafkaListener(
            id = "groupChatListener1",
            groupId = "groupChatListener",
            topics = "group-chat",
            containerFactory = "kafkaChatMessageRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenGroupChatTopic1(ConsumerRecord<String, KafkaChatMessageRequest> record, Acknowledgment acknowledgment) {
        listenGroupChatTopic(record, acknowledgment);
    }

    @Transactional
    @KafkaListener(
            id = "groupChatListener2",
            groupId = "groupChatListener",
            topics = "group-chat",
            containerFactory = "kafkaChatMessageRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenGroupChatTopic2(ConsumerRecord<String, KafkaChatMessageRequest> record, Acknowledgment acknowledgment) {
        listenGroupChatTopic(record, acknowledgment);
    }

    @Transactional
    @KafkaListener(
            id = "groupChatListener3",
            groupId = "groupChatListener",
            topics = "group-chat",
            containerFactory = "kafkaChatMessageRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenGroupChatTopic3(ConsumerRecord<String, KafkaChatMessageRequest> record, Acknowledgment acknowledgment) {
        listenGroupChatTopic(record, acknowledgment);
    }

    @Transactional
    @KafkaListener(
            id = "groupChatListener4",
            groupId = "groupChatListener",
            topics = "group-chat",
            containerFactory = "kafkaChatMessageRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenGroupChatTopic4(ConsumerRecord<String, KafkaChatMessageRequest> record, Acknowledgment acknowledgment) {
        listenGroupChatTopic(record, acknowledgment);
    }

    @Transactional
    @KafkaListener(
            id = "groupChatListener5",
            groupId = "groupChatListener",
            topics = "group-chat",
            containerFactory = "kafkaChatMessageRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenGroupChatTopic5(ConsumerRecord<String, KafkaChatMessageRequest> record, Acknowledgment acknowledgment) {
        listenGroupChatTopic(record, acknowledgment);
    }

    private void listenGroupChatTopic(ConsumerRecord<String, KafkaChatMessageRequest> record, Acknowledgment acknowledgment) {
        log.info("listen group chat = {}", record);

        String uuid = record.value().getUuid();
        boolean isProcessed = false;
        try {
            if (!processedKafkaRequestRepository.existsByUuid(uuid)) {
                Long chatRoomId = record.value().getChatRoomId();
                messagingTemplate.convertAndSend("/sub/group-chat/" + chatRoomId, record.value());
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
