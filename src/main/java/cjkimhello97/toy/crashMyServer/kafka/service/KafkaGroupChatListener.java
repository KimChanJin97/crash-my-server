package cjkimhello97.toy.crashMyServer.kafka.service;

import cjkimhello97.toy.crashMyServer.chat.dto.KafkaChatMessageRequest;
import cjkimhello97.toy.crashMyServer.kafka.domain.ProcessedKafkaRequest;
import cjkimhello97.toy.crashMyServer.kafka.repository.ProcessedKafkaRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaGroupChatListener {

    private final KafkaRequestSender kafkaRequestSender;
    private final ProcessedKafkaRequestRepository processedKafkaRequestRepository;

    @Transactional
    @KafkaListener(
            groupId = "groupChatListener",
            topics = "group-chat",
            containerFactory = "kafkaChatMessageRequestContainerFactory"
    )
    public void listenGroupChatTopic(
            KafkaChatMessageRequest request,
            Acknowledgment acknowledgment
    ) {
        // 이미 처리한 적이 있는 메시지라면 예외 발생
        String uuid = request.getUuid();
        if (processedKafkaRequestRepository.existsByUuid(uuid)) {
            acknowledgment.acknowledge();
            return;
        }
        // 처리한 적이 없던 메시지이므로 처리
        Long chatRoomId = request.getChatRoomId();
        kafkaRequestSender.convertAndSend("/sub/group-chat/" + chatRoomId, request);
        // 처리했다면 처리했음을 기록(저장)
        processedKafkaRequestRepository.save(new ProcessedKafkaRequest(uuid));
        // 처리 및 저장했다면 커밋
        acknowledgment.acknowledge();
    }
}
