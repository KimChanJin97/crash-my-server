package cjkimhello97.toy.crashMyServer.service.chat;

import static cjkimhello97.toy.crashMyServer.chat.exception.ChatExceptionType.ALREADY_LEFT_CHAT_ROOM;

import cjkimhello97.toy.crashMyServer.IntegrationTest;
import cjkimhello97.toy.crashMyServer.auth.service.AuthService;
import cjkimhello97.toy.crashMyServer.auth.service.dto.SignupRequest;
import cjkimhello97.toy.crashMyServer.chat.controller.dto.GroupChatMessageResponse;
import cjkimhello97.toy.crashMyServer.chat.controller.dto.GroupChatMessageResponses;
import cjkimhello97.toy.crashMyServer.chat.domain.ChatRoom;
import cjkimhello97.toy.crashMyServer.chat.exception.ChatException;
import cjkimhello97.toy.crashMyServer.chat.service.GroupChatService;
import cjkimhello97.toy.crashMyServer.chat.service.dto.GroupChatMessageRequest;
import cjkimhello97.toy.crashMyServer.member.domain.Member;
import cjkimhello97.toy.crashMyServer.member.repository.MemberRepository;
import cjkimhello97.toy.crashMyServer.service.auth.testdata.AuthServiceFixtureObject;
import cjkimhello97.toy.crashMyServer.service.chat.testdata.GroupChatServiceFixtureObject;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class MysqlIntegrationTest extends IntegrationTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private GroupChatService groupChatService;

    private Member member;
    private final String CHAT_ROOM_NAME = "임의의 채팅방 이름";
    private final Long CHAT_ROOM_ID = 1L;

    @BeforeEach
    void beforeEach() {
        // 회원가입
        SignupRequest signupRequest = AuthServiceFixtureObject.signupRequest();
        authService.signUp(signupRequest);

        member = memberRepository.findByNickname(signupRequest.nickname()).get();
    }

    @Test
    @DisplayName("[ CHAT ] MYSQL TEST 1")
    void 지정한_이름으로_채팅방이_생성되어야_한다() {
        // given: 채팅방 생성
        Long chatRoomId = groupChatService.createGroupChatRoom(member.getMemberId(), CHAT_ROOM_NAME);

        // when: 채팅방 입장
        ChatRoom chatRoom = groupChatService.getChatRoomByChatRoomId(chatRoomId);

        // then: 지정한 이름으로 채팅방이 생성되어야 한다
        Assertions.assertEquals(chatRoom.getChatRoomName(), CHAT_ROOM_NAME);
    }

    @Test
    @DisplayName("[ CHAT ] MYSQL TEST 2")
    void 채팅방에_입장했다면_채팅방_목록에_포함되어_있어야_한다() {
        // given: 채팅방 생성
        Long chatRoomId = groupChatService.createGroupChatRoom(member.getMemberId(), CHAT_ROOM_NAME);

        // when: 채팅방 입장
        ChatRoom chatRoom = groupChatService.getChatRoomByChatRoomId(chatRoomId);
        groupChatService.enterGroupChatRoom(CHAT_ROOM_ID, member.getMemberId());

        // then: 채팅방에 입장했다면 채팅방 목록에 포함되어 있어야 한다
        Set<ChatRoom> chatRooms = member.getChatRooms();
        Assertions.assertTrue(chatRooms.contains(chatRoom));
    }

    @Test
    @DisplayName("[ CHAT ] MYSQL TEST 3")
    void 채팅방을_퇴장했다면_채팅방_목록에_포함되어_있지_않아야_한다() {
        // given: 채팅방 생성
        Long chatRoomId = groupChatService.createGroupChatRoom(member.getMemberId(), CHAT_ROOM_NAME);

        // when: 채팅방 퇴장
        ChatRoom chatRoom = groupChatService.getChatRoomByChatRoomId(chatRoomId);
        groupChatService.leaveGroupChatRoom(CHAT_ROOM_ID, member.getMemberId());

        // then: 채팅방을 퇴장했다면 채팅방 목록에 포함되어 있지 않아야 한다
        Set<ChatRoom> chatRooms = member.getChatRooms();
        Assertions.assertFalse(chatRooms.contains(chatRoom));
    }

    @Test
    @DisplayName("[ CHAT ] MYSQL TEST 4")
    void 채팅_메시지를_3개_전송했다면_사이즈는_3이다() {
        // given: 채팅방 생성
        Long chatRoomId = groupChatService.createGroupChatRoom(member.getMemberId(), CHAT_ROOM_NAME);

        // when: 채팅 메시지 3번 전송
        GroupChatMessageRequest groupChatMessageRequest = GroupChatServiceFixtureObject.groupChatMessageRequest();
        final int TRIES = 3;
        for (int i = 0; i < TRIES; i++) {
            groupChatService.saveGroupChatMessage(groupChatMessageRequest);
        }

        // when: 채팅 메시지 조회
        GroupChatMessageResponses responses = groupChatService.getGroupChatMessages(CHAT_ROOM_ID, member.getMemberId());
        List<GroupChatMessageResponse> response = responses.getGroupChatMessageResponses();

        // then: 채팅 메시지를 3개 전송했다면 사이즈는 3이다
        Assertions.assertEquals(response.size(), TRIES);
    }

    @Test
    @DisplayName("[ CHAT ] MYSQL TEST 5")
    void 채팅방을_퇴장했다면_채팅_메시지가_조회되지_않아야_한다() {
        // given: 채팅방 생성
        Long chatRoomId = groupChatService.createGroupChatRoom(member.getMemberId(), CHAT_ROOM_NAME);

        // when: 채팅 메시지 3번 전송
        GroupChatMessageRequest groupChatMessageRequest = GroupChatServiceFixtureObject.groupChatMessageRequest();
        final int TRIES = 3;
        for (int i = 0; i < TRIES; i++) {
            groupChatService.saveGroupChatMessage(groupChatMessageRequest);
        }

        // when: 채팅 메시지 조회
        GroupChatMessageResponses responses = groupChatService.getGroupChatMessages(CHAT_ROOM_ID, member.getMemberId());
        List<GroupChatMessageResponse> response = responses.getGroupChatMessageResponses();

        // then: 채팅 메시지를 3개 전송했다면 사이즈는 3이다
        Assertions.assertEquals(response.size(), TRIES);

        // when: 채팅방 퇴장
        groupChatService.leaveGroupChatRoom(CHAT_ROOM_ID, member.getMemberId());

        // then: 채팅방을 퇴장했다면 채팅 메시지가 조회되지 않아야 한다
        ChatException chatException = Assertions.assertThrows(ChatException.class, () -> {
            groupChatService.getGroupChatMessages(CHAT_ROOM_ID, member.getMemberId());
        });
        Assertions.assertEquals(chatException.getExceptionType(), ALREADY_LEFT_CHAT_ROOM);
    }
}
