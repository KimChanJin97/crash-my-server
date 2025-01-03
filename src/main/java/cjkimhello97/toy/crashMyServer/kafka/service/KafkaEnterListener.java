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

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEnterListener {

    private final KafkaRequestSender kafkaRequestSender;
    private final ProcessedKafkaRequestRepository processedKafkaRequestRepository;

    @Transactional
    @KafkaListener(
            groupId = "enterListener",
            topics = "enter",
            containerFactory = "kafkaChatMessageRequestContainerFactory"
    )
    public void listenEnterTopic(
            KafkaChatMessageRequest request,
            Acknowledgment acknowledgment
    ) {
        String uuid = request.getUuid();
        // 처리한 적이 있던 메시지이므로 커밋
        if (processedKafkaRequestRepository.existsByUuid(uuid)) {
            acknowledgment.acknowledge();
            return;
        }
        // 처리한 적이 없던 메시지이므로 처리
        Long chatRoomId = request.getChatRoomId();
        kafkaRequestSender.convertAndSend("/sub/enter/" + chatRoomId, request);
        // 처리했다면 처리했음을 기록(저장)
        processedKafkaRequestRepository.save(new ProcessedKafkaRequest(uuid));
        // 처리 및 저장했다면 커밋
        acknowledgment.acknowledge();
    }
}
