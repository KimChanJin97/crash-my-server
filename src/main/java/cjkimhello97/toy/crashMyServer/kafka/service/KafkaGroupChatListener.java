package cjkimhello97.toy.crashMyServer.kafka.service;

import static cjkimhello97.toy.crashMyServer.kafka.exception.ProcessedKafkaRequestExceptionType.ALREADY_PROCESSED_MESSAGE;

import cjkimhello97.toy.crashMyServer.kafka.domain.ProcessedKafkaRequest;
import cjkimhello97.toy.crashMyServer.chat.dto.KafkaChatMessageRequest;
import cjkimhello97.toy.crashMyServer.kafka.exception.ProcessedKafkaRequestException;
import cjkimhello97.toy.crashMyServer.kafka.repository.ProcessedKafkaRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            id = "groupChatListener0",
            groupId = "groupChatListener",
            topics = "group-chat",
            containerFactory = "kafkaChatMessageRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenGroupChatTopic(
            KafkaChatMessageRequest request,
            Acknowledgment acknowledgment
    ) {
        String uuid = request.getUuid();
        boolean isProcessed = false;
        try {
            if (processedKafkaRequestRepository.existsByUuid(uuid)) {
                throw new ProcessedKafkaRequestException(ALREADY_PROCESSED_MESSAGE);
            }
            // 메시지 처리 로직
            Long chatRoomId = request.getChatRoomId();
            messagingTemplate.convertAndSend("/sub/group-chat/" + chatRoomId, request);
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
