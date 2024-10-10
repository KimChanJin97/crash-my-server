package cjkimhello97.toy.crashMyServer.chat.service;

import static cjkimhello97.toy.crashMyServer.chat.exception.ChatExceptionType.CHAT_ROOM_NOT_FOUND;
import static cjkimhello97.toy.crashMyServer.chat.exception.ChatExceptionType.MEMBER_CHAT_ROOM_TABLE_NOT_EXIST;
import static cjkimhello97.toy.crashMyServer.chat.utils.GroupChatMessageUtils.enterGroupChatRoomMessage;
import static cjkimhello97.toy.crashMyServer.chat.utils.GroupChatMessageUtils.leaveGroupChatRoomMessage;

import cjkimhello97.toy.crashMyServer.chat.controller.dto.ChatMessageResponse;
import cjkimhello97.toy.crashMyServer.chat.controller.dto.GroupChatMessageResponse;
import cjkimhello97.toy.crashMyServer.chat.controller.dto.GroupChatMessageResponses;
import cjkimhello97.toy.crashMyServer.chat.controller.dto.GroupChatRoomResponse;
import cjkimhello97.toy.crashMyServer.chat.domain.ChatMessage;
import cjkimhello97.toy.crashMyServer.chat.domain.ChatRoom;
import cjkimhello97.toy.crashMyServer.chat.domain.MemberChatRoom;
import cjkimhello97.toy.crashMyServer.chat.exception.ChatException;
import cjkimhello97.toy.crashMyServer.chat.repository.ChatMessageRepository;
import cjkimhello97.toy.crashMyServer.chat.repository.ChatRoomRepository;
import cjkimhello97.toy.crashMyServer.chat.repository.MemberChatRoomRepository;
import cjkimhello97.toy.crashMyServer.chat.service.dto.GroupChatMessageRequest;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaChatMessageRequest;
import cjkimhello97.toy.crashMyServer.member.domain.Member;
import cjkimhello97.toy.crashMyServer.member.service.MemberService;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final MemberService memberService;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<String, KafkaChatMessageRequest> kafkaChatMessageRequestTemplate;

    @Transactional
    public Long createGroupChatRoom(Long senderId, String chatRoomName) {
        log.info("[ GroupChatService ] createGroupChatRoom START ============================================");
        Member sender = memberService.getMemberByMemberId(senderId);
        log.info("[ GroupChatService ] createGroupChatRoom - memberService.getMemberByMemberId(senderId)");
        ChatRoom chatRoom = ChatRoom.builder()
                .host(sender)
                .chatRoomName(chatRoomName)
                .build();
        log.info("[ GroupChatService ] createGroupChatRoom - chatRoomRepository.save(chatRoom)");
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        enterGroupChatRoom(savedChatRoom.getChatRoomId(), senderId);
        return savedChatRoom.getChatRoomId();
    }

    @Transactional
    public void enterGroupChatRoom(Long chatRoomId, Long senderId) {
        log.info("[ GroupChatService ] enterGroupChatRoom START ============================================");

        log.info("[ GroupChatService ] enterGroupChatRoom - memberService.getMemberNicknameByMemberId(senderId)");
        String senderNickname = memberService.getMemberNicknameByMemberId(senderId);
        KafkaChatMessageRequest kafkaRequest = KafkaChatMessageRequest.builder()
                .uuid(String.valueOf(UUID.randomUUID()))
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .senderNickname(senderNickname)
                .content(enterGroupChatRoomMessage(senderNickname))
                .build();

        kafkaChatMessageRequestTemplate.send("enter", kafkaRequest);

        ChatRoom chatRoom = getChatRoomByChatRoomId(chatRoomId);
        log.info("[ GroupChatService ] enterGroupChatRoom - getMemberByMemberId(senderId)");
        Member sender = memberService.getMemberByMemberId(senderId);
        log.info("[ GroupChatService ] enterGroupChatRoom - addChatRoom(chatRoom)");
        sender.addChatRoom(chatRoom);

        // joinedAt이 null로 저장되는 문제 발생 -> @CreatedDate, @PrePersist 실패 -> 직접 중간 테이블 저장
        MemberChatRoom memberChatRoom = getMemberChatRoomByMemberIdAndChatRoomId(senderId, chatRoomId);
        log.info("[ GroupChatService ] enterGroupChatRoom - setJoinedAt(LocalDateTime.now())");
        memberChatRoom.setJoinedAt(LocalDateTime.now());
    }

    @Transactional
    public void saveGroupChatMessage(GroupChatMessageRequest groupChatMessageRequest) {
        log.info("[ GroupChatService ] saveGroupChatMessage START ============================================");

        log.info("[ GroupChatService ] saveGroupChatMessage - groupChatMessageRequest.setCreatedAtNow()");
        groupChatMessageRequest.setCreatedAtNow();
        log.info("[ GroupChatService ] saveGroupChatMessage - memberService.getMemberByNickname(groupChatMessageRequest.getSenderNickname())");
        Member member = memberService.getMemberByNickname(groupChatMessageRequest.getSenderNickname());
        KafkaChatMessageRequest kafkaRequest = modelMapper.map(groupChatMessageRequest, KafkaChatMessageRequest.class);
        kafkaRequest.setUuid(String.valueOf(UUID.randomUUID()));
        kafkaRequest.setSenderId(member.getMemberId());
        kafkaRequest.setChatRoomId(groupChatMessageRequest.getChatRoomId());
        kafkaChatMessageRequestTemplate.send("group-chat", kafkaRequest);

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoomId(groupChatMessageRequest.getChatRoomId())
                .senderNickname(groupChatMessageRequest.getSenderNickname())
                .senderId(groupChatMessageRequest.getSenderId())
                .content(groupChatMessageRequest.getContent())
                .createdAt(groupChatMessageRequest.getCreatedAt())
                .build();
        log.info("[ GroupChatService ] saveGroupChatMessage - chatMessageRepository.save(chatMessage)");
        chatMessageRepository.save(chatMessage);
    }

    public Set<GroupChatRoomResponse> getGroupChatRooms(Long senderId) {
        log.info("[ GroupChatService ] getGroupChatRooms START ============================================");

        log.info("[ GroupChatService ] getGroupChatRooms - memberService.getMemberByMemberId(senderId)");
        Member sender = memberService.getMemberByMemberId(senderId);
        Set<GroupChatRoomResponse> groupChatRoomResponses = new HashSet<>();

        log.info("[ GroupChatService ] getGroupChatRooms - sender.getChatRooms().forEach");
        sender.getChatRooms().forEach(chatRoom -> {
            log.info(
                    "[ GroupChatService ] getGroupChatRooms - .map(chatMessageRepository.findFirstByChatRoomIdOrderByCreatedAtDesc(chatRoom.getChatRoomId())");
            GroupChatRoomResponse groupChatRoomResponse = GroupChatRoomResponse.from(chatRoom);
            ChatMessageResponse chatMessageResponse = modelMapper
                    .map(chatMessageRepository.findFirstByChatRoomIdOrderByCreatedAtDesc(chatRoom.getChatRoomId()),
                            ChatMessageResponse.class
                    );
            log.info(
                    "[ GroupChatService ] getGroupChatRooms - groupChatRoomResponse.setChatMessageResponse(chatMessageResponse)");
            groupChatRoomResponse.setChatMessageResponse(chatMessageResponse);
            log.info("[ GroupChatService ] getGroupChatRooms - groupChatRoomResponses.add(groupChatRoomResponse)");
            groupChatRoomResponses.add(groupChatRoomResponse);
        });
        return groupChatRoomResponses;
    }

    public GroupChatMessageResponses getGroupChatMessages(Long chatRoomId, Long senderId) {
        log.info("[ GroupChatService ] getGroupChatMessages START ============================================");

        MemberChatRoom memberChatRoom = getMemberChatRoomByMemberIdAndChatRoomId(senderId, chatRoomId);
        log.info(
                "[ GroupChatService ] getGroupChatMessages - chatMessageRepository.findAllByChatRoomIdAndCreatedAtGreaterThanEqual(chatRoomId, memberChatRoom.getJoinedAt())");
        List<GroupChatMessageResponse> groupChatMessageResponses = chatMessageRepository
                .findAllByChatRoomIdAndCreatedAtGreaterThanEqual(chatRoomId, memberChatRoom.getJoinedAt())
                .stream()
                .map(chatMessage -> modelMapper.map(chatMessage, GroupChatMessageResponse.class))
                .collect(Collectors.toList());
        return new GroupChatMessageResponses(groupChatMessageResponses);
    }

    @Transactional
    public void leaveGroupChatRoom(Long chatRoomId, Long senderId) {
        log.info("[ GroupChatService ] getGroupChatMessages START ============================================");

        log.info("[ GroupChatService ] getGroupChatMessages - memberService.getMemberNicknameByMemberId(senderId)");
        String senderNickname = memberService.getMemberNicknameByMemberId(senderId);

        KafkaChatMessageRequest kafkaRequest = KafkaChatMessageRequest.builder()
                .uuid(String.valueOf(UUID.randomUUID()))
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .senderNickname(senderNickname)
                .content(leaveGroupChatRoomMessage(senderNickname))
                .build();
        kafkaChatMessageRequestTemplate.send("leave", kafkaRequest);

        ChatRoom chatRoom = getChatRoomByChatRoomId(chatRoomId);
        log.info("[ GroupChatService ] getGroupChatMessages - memberService.getMemberByMemberId(senderId)");
        Member sender = memberService.getMemberByMemberId(senderId);
        log.info("[ GroupChatService ] getGroupChatMessages - sender.removeChatRoom(chatRoom)");
        sender.removeChatRoom(chatRoom);
    }

    public ChatRoom getChatRoomByChatRoomId(Long chatRoomId) {
        log.info("[ GroupChatService ] getChatRoomByChatRoomId(chatRoomId)");
        return chatRoomRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new ChatException(CHAT_ROOM_NOT_FOUND));
    }

    private MemberChatRoom getMemberChatRoomByMemberIdAndChatRoomId(Long memberId, Long chatRoomId) {
        log.info("[ GroupChatService ] getMemberChatRoomByMemberIdAndChatRoomId(senderId, chatRoomId)");
        return memberChatRoomRepository.findByMemberMemberIdAndChatRoomChatRoomId(memberId, chatRoomId)
                .orElseThrow(() -> new ChatException(MEMBER_CHAT_ROOM_TABLE_NOT_EXIST));
    }
}
