package cjkimhello97.toy.crashMyServer.kafka.service;

import cjkimhello97.toy.crashMyServer.click.dto.KafkaClickRequest;
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
public class KafkaClickListener {

    private final KafkaRequestSender kafkaRequestSender;
    private final ProcessedKafkaRequestRepository processedKafkaRequestRepository;

    @Transactional
    @KafkaListener(
            groupId = "clickListener",
            topics = "click",
            containerFactory = "kafkaClickRequestContainerFactory"
    )
    public void listenClickTopic(
            KafkaClickRequest request,
            Acknowledgment acknowledgment
    ) {
        // 처리한 적이 있는 메시지이므로 커밋
        String uuid = request.getUuid();
        if (processedKafkaRequestRepository.existsByUuid(uuid)) {
            acknowledgment.acknowledge();
            return;
        }
        // 처리한 적이 없던 메시지이므로 처리
        Long memberId = request.getMemberId();
        kafkaRequestSender.convertAndSend("/sub/click/" + memberId, request);
        // 처리했다면 처리했음을 기록(저장)
        processedKafkaRequestRepository.save(new ProcessedKafkaRequest(uuid));
        // 처리 및 저장했다면 커밋
        acknowledgment.acknowledge();
    }
}
