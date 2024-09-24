package cjkimhello97.toy.crashMyServer.chat.controller;

import static cjkimhello97.toy.crashMyServer.chat.utils.GroupChatMessageUtils.enterGroupChatRoomMessage;
import static cjkimhello97.toy.crashMyServer.chat.utils.GroupChatMessageUtils.leaveGroupChatRoomMessage;

import cjkimhello97.toy.crashMyServer.auth.support.AuthMember;
import cjkimhello97.toy.crashMyServer.chat.controller.dto.GroupChatMessageResponses;
import cjkimhello97.toy.crashMyServer.chat.controller.dto.GroupChatRoomResponse;
import cjkimhello97.toy.crashMyServer.chat.service.GroupChatService;
import cjkimhello97.toy.crashMyServer.chat.service.dto.GroupChatMessageRequest;
import cjkimhello97.toy.crashMyServer.chat.service.dto.GroupChatRoomIdRequest;
import cjkimhello97.toy.crashMyServer.chat.service.dto.GroupChatRoomNameRequest;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaChatMessageRequest;
import cjkimhello97.toy.crashMyServer.member.domain.Member;
import cjkimhello97.toy.crashMyServer.member.service.MemberService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/v1/group-chat")
public class GroupChatController {

    private final KafkaTemplate<String, KafkaChatMessageRequest> kafkaChatMessageRequestTemplate;
    private final MemberService memberService;
    private final GroupChatService groupChatService;
    private final ModelMapper modelMapper;

    // 개발자만 사용
    @PostMapping("/rooms")
    public ResponseEntity<Long> createGroupChatRoom(
            @AuthMember Long senderId,
            @RequestBody GroupChatRoomNameRequest groupChatRoomNameRequest
    ) {
        String chatRoomName = groupChatRoomNameRequest.chatRoomName();
        return ResponseEntity.ok(groupChatService.createGroupChatRoom(senderId, chatRoomName));
    }

    // 개발자만 사용
    @GetMapping("/rooms")
    public ResponseEntity<Set<GroupChatRoomResponse>> getGroupChatRooms(
            @AuthMember Long senderId
    ) {
        return ResponseEntity.ok(groupChatService.getGroupChatRooms(senderId));
    }

    @GetMapping("/enter")
    public ResponseEntity<Void> enterGroupChatRoom(
            @AuthMember Long senderId,
            @RequestBody GroupChatRoomIdRequest groupChatRoomIdRequest
    ) {
        String senderNickname = memberService.getMemberNicknameByMemberId(senderId);
        Long chatRoomId = groupChatRoomIdRequest.chatRoomId();
        KafkaChatMessageRequest kafkaRequest = KafkaChatMessageRequest.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .senderNickname(senderNickname)
                .content(enterGroupChatRoomMessage(senderNickname))
                .build();
        kafkaChatMessageRequestTemplate.send("enter", kafkaRequest);
        groupChatService.enterGroupChatRoom(chatRoomId, senderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/messages")
    public ResponseEntity<GroupChatMessageResponses> getGroupChatMessages(
            @AuthMember Long senderId,
            @RequestBody GroupChatRoomIdRequest groupChatRoomIdRequest
    ) {
        Long chatRoomId = groupChatRoomIdRequest.chatRoomId();
        return ResponseEntity.ok(groupChatService.getGroupChatMessages(chatRoomId, senderId));
    }

    @MessageMapping("/group-chat-messages")
    public void sendGroupChatMessage(
            @Payload GroupChatMessageRequest groupChatMessageRequest
    ) {
        groupChatMessageRequest.setCreatedAtNow();
        Member member = memberService.getMemberByNickname(groupChatMessageRequest.getSenderNickname());
        KafkaChatMessageRequest kafkaRequest = modelMapper.map(groupChatMessageRequest, KafkaChatMessageRequest.class);
        kafkaRequest.setSenderId(member.getMemberId());
        kafkaRequest.setChatRoomId(groupChatMessageRequest.getChatRoomId());
        groupChatService.saveGroupChatMessage(groupChatMessageRequest);
        kafkaChatMessageRequestTemplate.send("group-chat", kafkaRequest);
    }

    @GetMapping("/leave")
    public ResponseEntity<Void> leaveGroupChatRoom(
            @AuthMember Long senderId,
            @RequestBody GroupChatRoomIdRequest groupChatRoomIdRequest
    ) {
        String senderNickname = memberService.getMemberNicknameByMemberId(senderId);
        Long chatRoomId = groupChatRoomIdRequest.chatRoomId();

        KafkaChatMessageRequest kafkaRequest = KafkaChatMessageRequest.builder()
                .chatRoomId(chatRoomId)
                .senderNickname(senderNickname)
                .content(leaveGroupChatRoomMessage(senderNickname))
                .build();
        kafkaChatMessageRequestTemplate.send("leave", kafkaRequest);
        groupChatService.leaveGroupChatRoom(groupChatRoomIdRequest.chatRoomId(), senderId);
        return ResponseEntity.ok().build();
    }
}
