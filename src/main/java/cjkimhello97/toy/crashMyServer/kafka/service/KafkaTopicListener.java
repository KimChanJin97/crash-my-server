package cjkimhello97.toy.crashMyServer.kafka.service;

import cjkimhello97.toy.crashMyServer.kafka.domain.ProcessedKafkaRequest;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaClickRankRequest;
import cjkimhello97.toy.crashMyServer.kafka.repository.ProcessedKafkaRequestRepository;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaChatMessageRequest;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaClickRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaTopicListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ProcessedKafkaRequestRepository processedKafkaRequestRepository;

    @Transactional // DB 예외 발생할 경우 롤백시켜서 uuid 저장 X
    @KafkaListener(id = "clickListener", topics = "click", containerFactory = "kafkaClickRequestConcurrentKafkaListenerContainerFactory")
    public void listenClickTopic(ConsumerRecord<String, KafkaClickRequest> record, Acknowledgment acknowledgment) {
        log.info("listen click = {}", record);

        String uuid = record.value().getUuid();
        if (!processedKafkaRequestRepository.existsByUuid(uuid)) { // 처리된 적 없다면
            String nickname = record.value().getNickname();
            messagingTemplate.convertAndSend("/sub/click/" + nickname, record.value());

            ProcessedKafkaRequest processedKafkaRequest = new ProcessedKafkaRequest();
            processedKafkaRequest.setUuid(uuid);
            processedKafkaRequestRepository.save(processedKafkaRequest);
        } else {
            log.info("uuid({}) message has already been processed", uuid);
        }

        // 커밋하기 전에 uuid를 저장했기 때문에 오프셋 커밋 직전 커넥션이 끊기더라도 uuid 존재여부를 확인하여 무한컨슘 방지
        acknowledgment.acknowledge();
    }

    @KafkaListener(id = "clickRankListener", topics = "click-rank", containerFactory = "kafkaClickRankRequestConcurrentKafkaListenerContainerFactory")
    public void listenClickRankTopic(ConsumerRecord<String, KafkaClickRankRequest> record, Acknowledgment acknowledgment) {
        log.info("listen click rank = {}", record);

        String uuid = record.value().getUuid();
        if (!processedKafkaRequestRepository.existsByUuid(uuid)) {
            messagingTemplate.convertAndSend("/sub/click-rank", record.value());

            ProcessedKafkaRequest processedKafkaRequest = new ProcessedKafkaRequest();
            processedKafkaRequest.setUuid(uuid);
            processedKafkaRequestRepository.save(processedKafkaRequest);
        } else {
            log.info("uuid({}) message has already been processed", uuid);
        }

        acknowledgment.acknowledge();
    }

    @KafkaListener(id = "enterListener", topics = "enter", containerFactory = "kafkaChatMessageRequestConcurrentKafkaListenerContainerFactory")
    public void listenEnterTopic(ConsumerRecord<String, KafkaChatMessageRequest> record, Acknowledgment acknowledgment) {
        log.info("listen enter = {}", record);

        String uuid = record.value().getUuid();
        if (!processedKafkaRequestRepository.existsByUuid(uuid)) {
            Long chatRoomId = record.value().getChatRoomId();
            messagingTemplate.convertAndSend("/sub/enter/" + chatRoomId, record.value());

            ProcessedKafkaRequest processedKafkaRequest = new ProcessedKafkaRequest();
            processedKafkaRequest.setUuid(uuid);
            processedKafkaRequestRepository.save(processedKafkaRequest);
        } else {
            log.info("uuid({}) message has already been processed", uuid);
        }

        acknowledgment.acknowledge();
    }

    @KafkaListener(id = "groupChatListener", topics = "group-chat", containerFactory = "kafkaChatMessageRequestConcurrentKafkaListenerContainerFactory")
    public void listenGroupChatTopic(ConsumerRecord<String, KafkaChatMessageRequest> record, Acknowledgment acknowledgment) {
        log.info("listen group chat = {}", record);

        String uuid = record.value().getUuid();
        if (!processedKafkaRequestRepository.existsByUuid(uuid)) {
            Long chatRoomId = record.value().getChatRoomId();
            messagingTemplate.convertAndSend("/sub/group-chat/" + chatRoomId, record.value());

            ProcessedKafkaRequest processedKafkaRequest = new ProcessedKafkaRequest();
            processedKafkaRequest.setUuid(uuid);
            processedKafkaRequestRepository.save(processedKafkaRequest);
        } else {
            log.info("uuid({}) message has already been processed", uuid);
        }

        acknowledgment.acknowledge();
    }

    @KafkaListener(id = "leaveListener", topics = "leave", containerFactory = "kafkaChatMessageRequestConcurrentKafkaListenerContainerFactory")
    public void listenLeaveTopic(ConsumerRecord<String, KafkaChatMessageRequest> record, Acknowledgment acknowledgment) {
        log.info("listen leave = {}", record);

        String uuid = record.value().getUuid();
        if (!processedKafkaRequestRepository.existsByUuid(uuid)) {
            Long chatRoomId = record.value().getChatRoomId();
            messagingTemplate.convertAndSend("/sub/leave/" + chatRoomId, record.value());

            ProcessedKafkaRequest processedKafkaRequest = new ProcessedKafkaRequest();
            processedKafkaRequest.setUuid(uuid);
            processedKafkaRequestRepository.save(processedKafkaRequest);
        } else {
            log.info("uuid({}) message has already been processed", uuid);
        }

        acknowledgment.acknowledge();
    }
}
