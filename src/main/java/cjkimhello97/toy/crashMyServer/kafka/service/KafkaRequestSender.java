package cjkimhello97.toy.crashMyServer.kafka.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaRequestSender {

    private final SimpMessagingTemplate messagingTemplate;

    public void convertAndSend(String destination, Object request) {
        messagingTemplate.convertAndSend(destination, request);
    }
}
