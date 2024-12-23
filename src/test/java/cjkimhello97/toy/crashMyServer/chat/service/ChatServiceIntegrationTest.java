package cjkimhello97.toy.crashMyServer.chat.service;

import static cjkimhello97.toy.crashMyServer.chat.service.testdata.ChatServiceFixtureObject.*;

import cjkimhello97.toy.crashMyServer.IntegrationTest;
import cjkimhello97.toy.crashMyServer.chat.domain.ChatMessage;
import cjkimhello97.toy.crashMyServer.chat.domain.ChatRoom;
import cjkimhello97.toy.crashMyServer.chat.dto.GroupChatMessageRequest;
import cjkimhello97.toy.crashMyServer.chat.dto.KafkaChatMessageRequest;
import cjkimhello97.toy.crashMyServer.chat.repository.ChatMessageRepository;
import cjkimhello97.toy.crashMyServer.chat.repository.ChatRoomRepository;
import cjkimhello97.toy.crashMyServer.chat.service.testdata.ChatServiceFixtureObject;
import cjkimhello97.toy.crashMyServer.kafka.service.KafkaEnterListener;
import cjkimhello97.toy.crashMyServer.member.domain.Member;
import cjkimhello97.toy.crashMyServer.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

public class ChatServiceIntegrationTest extends IntegrationTest {

    private static final Long MEMBER_ID = 1L;
    private static final Long CHAT_ROOM_ID = 1L;
    private static final Member MEMBER = member();
    private static final ChatRoom CHAT_ROOM = chatRoom();
    private static final GroupChatMessageRequest GROUP_CHAT_MESSAGE_REQUEST = groupChatMessageRequest();

    @Autowired
    private GroupChatService groupChatService;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @BeforeEach
    void setUp() {
        // given : 멤버가 저장되어있고, 채팅방이 생성되어 있도록 스텁
        Mockito.when(memberRepository.findByMemberId(MEMBER_ID)).thenReturn(Optional.of(MEMBER));
        Mockito.when(chatRoomRepository.findByChatRoomId(CHAT_ROOM_ID)).thenReturn(Optional.of(CHAT_ROOM));
    }

    @Test
    @DisplayName("채팅_메시지를_전송하면_몽고디비에_저장되어야한다")
    void 채팅_메시지를_전송하면_몽고디비에_저장되어야한다() {
        // when : 채팅 메시지를 전송하면
        groupChatService.saveGroupChatMessage(GROUP_CHAT_MESSAGE_REQUEST);
        // then : 몽고디비에 저장되어야한다
        ChatMessage chatMessage = chatMessageRepository.findAll().get(0);
        Assertions.assertEquals(chatMessage.getChatRoomId(), GROUP_CHAT_MESSAGE_REQUEST.getChatRoomId());
        Assertions.assertEquals(chatMessage.getSenderId(), GROUP_CHAT_MESSAGE_REQUEST.getSenderId());
        Assertions.assertEquals(chatMessage.getSenderNickname(), GROUP_CHAT_MESSAGE_REQUEST.getSenderNickname());
        Assertions.assertEquals(chatMessage.getContent(), GROUP_CHAT_MESSAGE_REQUEST.getContent());
    }
}
