package cjkimhello97.toy.crashMyServer.kafka.service;

import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaChatMessageRequest;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaClickRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaTopicListener {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(id = "clickListener", topics = "click")
    public void listenClickTopic(ConsumerRecord<String, KafkaClickRequest> record) {
        log.info("listen click = {}", record);
        String nickname = record.value().getNickname();
        messagingTemplate.convertAndSend("/sub/click/" + nickname, record.value());
    }

    @KafkaListener(id = "clickRankListener", topics = "click-rank")
    public void listenClickRankTopic(ConsumerRecord<String, KafkaClickRequest> record) {
        log.info("listen click rank = {}", record);

        messagingTemplate.convertAndSend("/sub/click-rank", record.value());
    }

    @KafkaListener(id = "enterListener", topics = "enter")
    public void listenEnterTopic(ConsumerRecord<String, KafkaChatMessageRequest> record) {
        log.info("listen enter = {}", record);

        Long chatRoomId = record.value().getChatRoomId();
        messagingTemplate.convertAndSend("/sub/enter/" + chatRoomId, record.value());
    }

    @KafkaListener(id = "groupChatListener", topics = "group-chat")
    public void listenGroupChatTopic(ConsumerRecord<String, KafkaChatMessageRequest> record) {
        log.info("listen group chat = {}", record);

        Long chatRoomId = record.value().getChatRoomId();
        messagingTemplate.convertAndSend("/sub/group-chat/" + chatRoomId, record.value());
    }

    @KafkaListener(id = "leaveListener", topics = "leave")
    public void listenLeaveTopic(ConsumerRecord<String, KafkaChatMessageRequest> record) {
        log.info("listen leave = {}", record);

        Long chatRoomId = record.value().getChatRoomId();
        messagingTemplate.convertAndSend("/sub/leave/" + chatRoomId, record.value());
    }
}
