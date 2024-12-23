package cjkimhello97.toy.crashMyServer.kafka.service;

import static cjkimhello97.toy.crashMyServer.kafka.exception.ProcessedKafkaRequestExceptionType.ALREADY_PROCESSED_MESSAGE;

import cjkimhello97.toy.crashMyServer.kafka.domain.ProcessedKafkaRequest;
import cjkimhello97.toy.crashMyServer.click.dto.KafkaClickRequest;
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
public class KafkaClickListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ProcessedKafkaRequestRepository processedKafkaRequestRepository;

    @Transactional
    @KafkaListener(
            id = "clickListener0",
            groupId = "clickListener",
            topics = "click",
            containerFactory = "kafkaClickRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenClickTopic(
            KafkaClickRequest request,
            Acknowledgment acknowledgment
    ) {
        // 이미 처리한 적이 있는 메시지라면 예외 발생
        String uuid = request.getUuid();
        if (processedKafkaRequestRepository.existsByUuid(uuid)) {
            throw new ProcessedKafkaRequestException(ALREADY_PROCESSED_MESSAGE);
        }
        // 메시지 처리(전송)
        Long memberId = request.getMemberId();
        messagingTemplate.convertAndSend("/sub/click/" + memberId, request);
        // 처리했다면 처리했음을 기록(저장)
        processedKafkaRequestRepository.save(new ProcessedKafkaRequest(uuid));
        acknowledgment.acknowledge();
    }
}
