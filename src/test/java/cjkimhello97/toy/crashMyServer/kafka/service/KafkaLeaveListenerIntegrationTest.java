package cjkimhello97.toy.crashMyServer.kafka.service;

import static cjkimhello97.toy.crashMyServer.kafka.service.testdata.KafkaChatListenerFixtureObject.chatRoom;
import static cjkimhello97.toy.crashMyServer.kafka.service.testdata.KafkaChatListenerFixtureObject.groupChatMessageRequest;
import static cjkimhello97.toy.crashMyServer.kafka.service.testdata.KafkaChatListenerFixtureObject.member;
import static cjkimhello97.toy.crashMyServer.kafka.service.testdata.KafkaChatListenerFixtureObject.memberChatRoom;

import cjkimhello97.toy.crashMyServer.IntegrationTest;
import cjkimhello97.toy.crashMyServer.chat.domain.ChatRoom;
import cjkimhello97.toy.crashMyServer.chat.domain.MemberChatRoom;
import cjkimhello97.toy.crashMyServer.chat.dto.GroupChatMessageRequest;
import cjkimhello97.toy.crashMyServer.chat.dto.KafkaChatMessageRequest;
import cjkimhello97.toy.crashMyServer.chat.repository.ChatRoomRepository;
import cjkimhello97.toy.crashMyServer.chat.repository.MemberChatRoomRepository;
import cjkimhello97.toy.crashMyServer.chat.service.GroupChatService;
import cjkimhello97.toy.crashMyServer.member.domain.Member;
import cjkimhello97.toy.crashMyServer.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@Order(4)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class KafkaLeaveListenerIntegrationTest extends IntegrationTest {

    private static final Long MEMBER_ID = 1L;
    private static final Long CHAT_ROOM_ID = 1L;
    private static final Member MEMBER = member();
    private static final ChatRoom CHAT_ROOM = chatRoom();
    private static final MemberChatRoom MEMBER_CHAT_ROOM = memberChatRoom();


    @Autowired
    private GroupChatService groupChatService;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private ChatRoomRepository chatRoomRepository;
    @MockBean
    private MemberChatRoomRepository memberChatRoomRepository;
    @SpyBean
    private KafkaLeaveListener kafkaLeaveListener;

    @BeforeEach
    void setUp() {
        // given : 멤버가 저장되어있고, 채팅방이 생성되어 있도록 스텁
        Mockito.when(memberRepository.findByMemberId(MEMBER_ID))
                .thenReturn(Optional.of(MEMBER));
        Mockito.when(chatRoomRepository.findByChatRoomId(CHAT_ROOM_ID))
                .thenReturn(Optional.of(CHAT_ROOM));
        Mockito.when(memberChatRoomRepository.findByMemberMemberIdAndChatRoomChatRoomId(MEMBER_ID, CHAT_ROOM_ID))
                .thenReturn(Optional.of(MEMBER_CHAT_ROOM));
    }

    @Test
    void 채팅방을_퇴장하면_퇴장_메시지가_생산되고_소비된다() {
        // when : 채팅방에 입장하면
        KafkaChatMessageRequest producedRequest = groupChatService.leaveGroupChatRoom(CHAT_ROOM_ID, MEMBER_ID);

        // then : 입장 메시지가 생산되고 소비된다
        ArgumentCaptor<KafkaChatMessageRequest> reqCaptor = ArgumentCaptor.forClass(KafkaChatMessageRequest.class);
        ArgumentCaptor<Acknowledgment> ackCaptor = ArgumentCaptor.forClass(Acknowledgment.class);
        Mockito.verify(kafkaLeaveListener, Mockito.timeout(5000).times(1))
                .listenLeaveTopic(reqCaptor.capture(), ackCaptor.capture());
        KafkaChatMessageRequest consumedRequest = reqCaptor.getValue();

        Assertions.assertEquals(producedRequest.getUuid(), consumedRequest.getUuid());
    }
}
