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

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaLeaveListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ProcessedKafkaRequestRepository processedKafkaRequestRepository;

    @Transactional
    @KafkaListener(
            id = "leaveListener0",
            groupId = "leaveListener",
            topics = "leave",
            containerFactory = "kafkaChatMessageRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenLeaveTopic1(
            KafkaChatMessageRequest request,
            Acknowledgment acknowledgment
    ) {
        String uuid = request.getUuid();
        boolean isProcessed = false;
        try {
            if (processedKafkaRequestRepository.findById(uuid) == null) {
                Long chatRoomId = request.getChatRoomId();
                messagingTemplate.convertAndSend("/sub/leave/" + chatRoomId, request);
                isProcessed = true;
            } else {
                throw new ProcessedKafkaRequestException(ALREADY_PROCESSED_MESSAGE);
            }
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
