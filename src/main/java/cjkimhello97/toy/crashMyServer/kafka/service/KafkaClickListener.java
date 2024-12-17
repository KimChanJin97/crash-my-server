package cjkimhello97.toy.crashMyServer.kafka.service;

import cjkimhello97.toy.crashMyServer.kafka.domain.ProcessedKafkaRequest;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaClickRequest;
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
public class KafkaClickListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ProcessedKafkaRequestRepository processedKafkaRequestRepository;

    @Transactional
    @KafkaListener(
            id = "clickListener1",
            groupId = "clickListener",
            topics = "click",
            containerFactory = "kafkaClickRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenClickTopic1(ConsumerRecord<String, KafkaClickRequest> record, Acknowledgment acknowledgment) {
        listenClickTopic(record, acknowledgment);
    }

    @Transactional
    @KafkaListener(
            id = "clickListener2",
            groupId = "clickListener",
            topics = "click",
            containerFactory = "kafkaClickRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenClickTopic2(ConsumerRecord<String, KafkaClickRequest> record, Acknowledgment acknowledgment) {
        listenClickTopic(record, acknowledgment);
    }

    @Transactional
    @KafkaListener(
            id = "clickListener3",
            groupId = "clickListener",
            topics = "click",
            containerFactory = "kafkaClickRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenClickTopic3(ConsumerRecord<String, KafkaClickRequest> record, Acknowledgment acknowledgment) {
        listenClickTopic(record, acknowledgment);
    }

    @Transactional
    @KafkaListener(
            id = "clickListener4",
            groupId = "clickListener",
            topics = "click",
            containerFactory = "kafkaClickRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenClickTopic4(ConsumerRecord<String, KafkaClickRequest> record, Acknowledgment acknowledgment) {
        listenClickTopic(record, acknowledgment);
    }

    @Transactional
    @KafkaListener(
            id = "clickListener5",
            groupId = "clickListener",
            topics = "click",
            containerFactory = "kafkaClickRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenClickTopic5(ConsumerRecord<String, KafkaClickRequest> record, Acknowledgment acknowledgment) {
        listenClickTopic(record, acknowledgment);
    }

    private void listenClickTopic(ConsumerRecord<String, KafkaClickRequest> record, Acknowledgment acknowledgment) {
        log.info("listen click = {}", record);

        String uuid = record.value().getUuid();
        boolean isProcessed = false;
        try {
            if (!processedKafkaRequestRepository.existsByUuid(uuid)) {
                String nickname = record.value().getNickname();
                messagingTemplate.convertAndSend("/sub/click/" + nickname, record.value());
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
