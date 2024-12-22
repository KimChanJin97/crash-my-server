package cjkimhello97.toy.crashMyServer.kafka.service;

import static cjkimhello97.toy.crashMyServer.kafka.exception.ProcessedKafkaRequestExceptionType.ALREADY_PROCESSED_MESSAGE;

import cjkimhello97.toy.crashMyServer.chat.dto.KafkaChatMessageRequest;
import cjkimhello97.toy.crashMyServer.kafka.domain.ProcessedKafkaRequest;
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
public class KafkaEnterListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ProcessedKafkaRequestRepository processedKafkaRequestRepository;

    @Transactional(noRollbackFor = ProcessedKafkaRequestException.class)
    @KafkaListener(
            id = "enterListener0",
            groupId = "enterListener",
            topics = "enter",
            containerFactory = "kafkaChatMessageRequestConcurrentKafkaListenerContainerFactory"
    )
    public void listenEnterTopic(
            KafkaChatMessageRequest request,
            Acknowledgment acknowledgment
    ) {
        System.out.println("\n\n\n호출\n\n\n");

        String uuid = request.getUuid();
        // 메시지가 이미 처리된 경우 예외 발생
        if (processedKafkaRequestRepository.existsByUuid(uuid)) {
            System.out.println("\n\n\n" + uuid + " 이미 처리된 적이 있기 때문에 예외 발생\n\n\n");
            throw new ProcessedKafkaRequestException(ALREADY_PROCESSED_MESSAGE);
        }

        // 메시지 처리 로직
        System.out.println("\n\n\n" + uuid +" 처리한 적 없기 때문에 처리\n\n\n");
        Long chatRoomId = request.getChatRoomId();
        messagingTemplate.convertAndSend("/sub/enter/" + chatRoomId, request);

        // 처리 완료 기록
        ProcessedKafkaRequest processedKafkaRequest = new ProcessedKafkaRequest();
        processedKafkaRequest.setUuid(uuid);
        ProcessedKafkaRequest save = processedKafkaRequestRepository.save(processedKafkaRequest);
        System.out.println("\n\n\nsave = " + save.getUuid() + "\n\n\n");

        acknowledgment.acknowledge(); // 커밋
    }
}
