package cjkimhello97.toy.crashMyServer.service.chat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cjkimhello97.toy.crashMyServer.IntegrationTest;
import cjkimhello97.toy.crashMyServer.auth.infrastructure.JwtProvider;
import cjkimhello97.toy.crashMyServer.auth.service.AuthService;
import cjkimhello97.toy.crashMyServer.auth.service.RedisTokenService;
import cjkimhello97.toy.crashMyServer.auth.service.dto.SignupRequest;
import cjkimhello97.toy.crashMyServer.chat.domain.ChatRoom;
import cjkimhello97.toy.crashMyServer.chat.domain.MemberChatRoom;
import cjkimhello97.toy.crashMyServer.chat.domain.MemberChatRoomId;
import cjkimhello97.toy.crashMyServer.chat.repository.ChatMessageRepository;
import cjkimhello97.toy.crashMyServer.chat.repository.ChatRoomRepository;
import cjkimhello97.toy.crashMyServer.chat.repository.MemberChatRoomRepository;
import cjkimhello97.toy.crashMyServer.chat.service.GroupChatService;
import cjkimhello97.toy.crashMyServer.chat.service.dto.GroupChatMessageRequest;
import cjkimhello97.toy.crashMyServer.click.repository.ClickRepository;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaChatMessageRequest;
import cjkimhello97.toy.crashMyServer.member.domain.Member;
import cjkimhello97.toy.crashMyServer.member.repository.MemberRepository;
import cjkimhello97.toy.crashMyServer.member.service.MemberService;
import cjkimhello97.toy.crashMyServer.service.auth.testdata.AuthServiceFixtureObject;
import cjkimhello97.toy.crashMyServer.service.chat.testdata.GroupChatServiceFixtureObject;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class KafkaMockTest extends IntegrationTest {

    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ClickRepository clickRepository;
    @Mock
    private RedisTokenService redisTokenService;
    @InjectMocks
    private AuthService authService;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private MemberChatRoomRepository memberChatRoomRepository;
    @Mock
    private MemberService memberService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private KafkaTemplate<String, KafkaChatMessageRequest> kafkaChatMessageRequestKafkaTemplate;
    @InjectMocks
    private GroupChatService groupChatService;

    private Member member;
    private ChatRoom chatRoom;
    private MemberChatRoomId id;
    private MemberChatRoom memberChatRoom;

    private final Long MEMBER_ID = 1L;
    private final Long CHAT_ROOM_ID = 1L;
    private final String CHAT_ROOM_NAME = "임의의 채팅방 이름";

    @BeforeEach
    void beforeEach() {
        // 회원가입
        SignupRequest signupRequest = AuthServiceFixtureObject.signupRequest();
        String nickname = signupRequest.nickname();
        String password = signupRequest.password();

        member = Member.builder()
                .memberId(MEMBER_ID)
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .build();

        when(memberRepository.findByNickname(signupRequest.nickname()))
                .thenReturn(Optional.of(member));
        when(passwordEncoder.matches(password, member.getPassword()))
                .thenReturn(true);

        authService.signUp(signupRequest);

        // 채팅방 생성
        chatRoom = ChatRoom.builder()
                .chatRoomId(CHAT_ROOM_ID)
                .chatRoomName(CHAT_ROOM_NAME)
                .host(member)
                .build();

        when(memberService.getMemberByMemberId(member.getMemberId()))
                .thenReturn(member);
        when(chatRoomRepository.save(chatRoom))
                .thenReturn(chatRoom);

        chatRoomRepository.save(chatRoom);

        id = MemberChatRoomId.builder()
                .memberId(member.getMemberId())
                .chatRoomId(chatRoom.getChatRoomId())
                .build();

        memberChatRoom = MemberChatRoom.builder()
                .id(id)
                .member(member)
                .chatRoom(chatRoom)
                .joinedAt(LocalDateTime.now())
                .build();

        when(chatRoomRepository.findByChatRoomId(CHAT_ROOM_ID))
                .thenReturn(Optional.of(chatRoom));
        when(memberChatRoomRepository.findByMemberMemberIdAndChatRoomChatRoomId(MEMBER_ID, CHAT_ROOM_ID))
                .thenReturn(Optional.of(memberChatRoom));
        when(memberService.getMemberNicknameByMemberId(member.getMemberId()))
                .thenReturn(member.getNickname());
    }

    @Test
    @DisplayName("[ CHAT ] MOCK TEST 1")
    void 채팅방에_입장하면_카프카_입장_메시지가_발행되어야_한다() {
        // given: 카프카 입장 메시지 요청 DTO 생성
        KafkaChatMessageRequest kafkaEnterRequest = GroupChatServiceFixtureObject.kafkaEnterRequest();

        // when: 채팅방 입장
        Long senderId = kafkaEnterRequest.getSenderId();
        groupChatService.enterGroupChatRoom(CHAT_ROOM_ID, senderId);

        // then: 채팅방에 입장하면 카프카 입장 메시지가 발행되어야_한다
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<KafkaChatMessageRequest> requestCaptor = ArgumentCaptor.forClass(KafkaChatMessageRequest.class);
        verify(kafkaChatMessageRequestKafkaTemplate).send(topicCaptor.capture(), requestCaptor.capture());

        assertEquals("enter", topicCaptor.getValue());

        assertEquals(kafkaEnterRequest.getSenderNickname(), requestCaptor.getValue().getSenderNickname());
        assertEquals(kafkaEnterRequest.getSenderId(), requestCaptor.getValue().getSenderId());
        assertEquals(kafkaEnterRequest.getChatRoomId(), requestCaptor.getValue().getChatRoomId());
        assertEquals(kafkaEnterRequest.getContent(), requestCaptor.getValue().getContent());
    }

    @Test
    @DisplayName("[ CHAT ] MOCK TEST 2")
    void 채팅_메시지를_전송하면_카프카_채팅_메시지_전송_메시지가_발행되어야_한다() {
        // given: 카프카 채팅 메시지 전송 메시지 요청 DTO 생성
        KafkaChatMessageRequest kafkaChatMessageRequest = GroupChatServiceFixtureObject.kafkaChatMessageRequest();
        GroupChatMessageRequest groupChatMessageRequest = GroupChatServiceFixtureObject.groupChatMessageRequest();

        // when: 채팅 메시지 전송
        when(memberService.getMemberByNickname(kafkaChatMessageRequest.getSenderNickname()))
                .thenReturn(member);
        when(modelMapper.map(groupChatMessageRequest, KafkaChatMessageRequest.class))
                .thenReturn(kafkaChatMessageRequest);
        groupChatService.saveGroupChatMessage(groupChatMessageRequest);

        // then: 채팅 메시지를 전송하면 카프카 채팅 메시지 전송 메시지가 발행되어야 한다
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<KafkaChatMessageRequest> requestCaptor = ArgumentCaptor.forClass(KafkaChatMessageRequest.class);
        verify(kafkaChatMessageRequestKafkaTemplate).send(topicCaptor.capture(), requestCaptor.capture());

        assertEquals("group-chat", topicCaptor.getValue());
        assertEquals(kafkaChatMessageRequest, requestCaptor.getValue());
    }

    @Test
    @DisplayName("[ CHAT ] MOCK TEST 3")
    void 채팅방을_퇴장하면_카프카_퇴장_메시지가_발행되어야_한다() {
        // given: 카프카 입장 메시지 요청 DTO 생성
        KafkaChatMessageRequest kafkaLeaveRequest = GroupChatServiceFixtureObject.kafkaLeaveRequest();

        // when: 채팅방 퇴장
        Long senderId = kafkaLeaveRequest.getSenderId();
        groupChatService.leaveGroupChatRoom(1L, senderId);

        // then: 채팅방을 퇴장하면 카프카 퇴장 메시지가 발행되어야 한다
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<KafkaChatMessageRequest> requestCaptor = ArgumentCaptor.forClass(KafkaChatMessageRequest.class);
        verify(kafkaChatMessageRequestKafkaTemplate).send(topicCaptor.capture(), requestCaptor.capture());

        assertEquals("leave", topicCaptor.getValue());

        assertEquals(kafkaLeaveRequest.getSenderNickname(), requestCaptor.getValue().getSenderNickname());
        assertEquals(kafkaLeaveRequest.getSenderId(), requestCaptor.getValue().getSenderId());
        assertEquals(kafkaLeaveRequest.getChatRoomId(), requestCaptor.getValue().getChatRoomId());
        assertEquals(kafkaLeaveRequest.getContent(), requestCaptor.getValue().getContent());
    }
}

