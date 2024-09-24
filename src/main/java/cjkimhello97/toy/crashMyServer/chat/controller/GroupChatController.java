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
import cjkimhello97.toy.crashMyServer.common.exception.dto.ExceptionResponse;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaChatMessageRequest;
import cjkimhello97.toy.crashMyServer.member.domain.Member;
import cjkimhello97.toy.crashMyServer.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "2. 그룹 채팅")
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
    @Operation(summary = "사용 X")
    public ResponseEntity<Long> createGroupChatRoom(
            @AuthMember Long senderId,
            @RequestBody GroupChatRoomNameRequest groupChatRoomNameRequest
    ) {
        String chatRoomName = groupChatRoomNameRequest.chatRoomName();
        return ResponseEntity.ok(groupChatService.createGroupChatRoom(senderId, chatRoomName));
    }

    // 개발자만 사용
    @GetMapping("/rooms")
    @Operation(summary = "사용 X")
    public ResponseEntity<Set<GroupChatRoomResponse>> getGroupChatRooms(
            @AuthMember Long senderId
    ) {
        return ResponseEntity.ok(groupChatService.getGroupChatRooms(senderId));
    }

    @GetMapping("/enter")
    @Operation(
            summary = "[ HTTP 요청 + STOMP 응답 ] 그룹 채팅방 입장 API",
            description = """
                    STOMP 응답을 받기 위한 조건
                     1. 웹소켓 연결 : ws://도메인:8080/ws URL을 연결한 상태이어야 합니다.
                     2. 웹소켓 구독 : /sub/enter/1 URL을 구독한 상태이어야 있어야 합니다. (* 주의 chatRoomId 1 고정)
                    주의사항
                     1. 구독한 모든 클라이언트에게 HTTP 응답이 아니라 STOMP 응답이 옵니다.
                     2. 메시지 응답 형식 : {"senderNickname":"bbb","senderId":2,"chatRoomId":1,"content":"bbb 님이 입장하셨습니다!","createdAt":null}
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공"),
            @ApiResponse(
                    responseCode = "400, 401, 404, 500",
                    description = "그룹 채팅방 예외(400, 500), JWT 검증/파싱 예외(401, 404)",
                    content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
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
    @Operation(summary = "[ HTTP 요청/응답 ] 그룹 채팅방 채팅 메시지 조회 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = {@Content(schema = @Schema(implementation = GroupChatMessageResponses.class))}),
            @ApiResponse(
                    responseCode = "400, 401, 404, 500",
                    description = "그룹 채팅방 예외(400, 500), JWT 검증/파싱 예외(401, 404)",
                    content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    public ResponseEntity<GroupChatMessageResponses> getGroupChatMessages(
            @AuthMember Long senderId,
            @RequestBody GroupChatRoomIdRequest groupChatRoomIdRequest
    ) {
        Long chatRoomId = groupChatRoomIdRequest.chatRoomId();
        return ResponseEntity.ok(groupChatService.getGroupChatMessages(chatRoomId, senderId));
    }

    @MessageMapping("/group-chat-messages")
    @Operation(
            summary = "[ STOMP 요청/응답 ] 그룹 채팅방 채팅 메시지 전송 API",
            description = """
                    API 사용 방법
                     STOMP 응답을 받기 위한 조건
                     1. 웹소켓 연결 : ws://도메인:8080/ws URL을 연결한 상태이어야 합니다.
                     2. 웹소켓 구독 : /sub/group-chat/1 URL을 구독한 상태이어야 있어야 합니다. (* 주의 chatRoomId 1 고정)
                     3. 웹소켓 발행 : /pub/group-chat-messages URL로 메시지를 발행해야 합니다.
                     4. 메시지 요청 형식 : {"senderNickname":"ccc","chatRoomId":1,"content":"안녕하세요1"} (* 주의 chatRoomId 1 고정)
                    주의사항
                     1. 구독한 모든 클라이언트에게 HTTP 응답이 아니라 STOMP 응답이 옵니다.
                     2. 메시지 응답 형식 :  {"senderNickname":"ccc","senderId":3,"chatRoomId":1,"content":"안녕하세요1","createdAt":"2024-09-24T00:25:48.088022"}
                     """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = {@Content(schema = @Schema(implementation = GroupChatMessageResponses.class))}),
            @ApiResponse(
                    responseCode = "400, 401, 404, 500",
                    description = "그룹 채팅방 예외(400, 500), JWT 검증/파싱 예외(401, 404)",
                    content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
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
    @Operation(
            summary = "[ HTTP 요청 + STOMP 응답 ] 그룹 채팅방 퇴장 API",
            description = """
                    STOMP 응답을 받기 위한 조건
                     1. 웹소켓 연결 : ws://도메인:8080/ws URL을 연결한 상태이어야 합니다.
                     2. 웹소켓 구독 : /sub/leave/1 URL을 구독한 상태이어야 있어야 합니다. (* 주의 chatRoomId 1 고정)
                    주의사항
                     1. 구독한 모든 클라이언트에게 HTTP 응답이 아니라 STOMP 응답이 옵니다.
                     2. 메시지 응답 형식 : {"senderNickname":"bbb","senderId":2,"chatRoomId":1,"content":"bbb 님이 퇴장하셨습니다!","createdAt":null}
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공"),
            @ApiResponse(
                    responseCode = "400, 401, 404, 500",
                    description = "그룹 채팅방 예외(400, 500), JWT 검증/파싱 예외(401, 404)",
                    content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
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