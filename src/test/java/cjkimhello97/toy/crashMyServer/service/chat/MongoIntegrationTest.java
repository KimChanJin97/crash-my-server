package cjkimhello97.toy.crashMyServer.service.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cjkimhello97.toy.crashMyServer.IntegrationTest;
import cjkimhello97.toy.crashMyServer.auth.service.AuthService;
import cjkimhello97.toy.crashMyServer.auth.service.dto.SignupRequest;
import cjkimhello97.toy.crashMyServer.chat.domain.ChatMessage;
import cjkimhello97.toy.crashMyServer.chat.repository.ChatMessageRepository;
import cjkimhello97.toy.crashMyServer.chat.service.GroupChatService;
import cjkimhello97.toy.crashMyServer.chat.service.dto.GroupChatMessageRequest;
import cjkimhello97.toy.crashMyServer.member.domain.Member;
import cjkimhello97.toy.crashMyServer.member.repository.MemberRepository;
import cjkimhello97.toy.crashMyServer.service.auth.testdata.AuthServiceFixtureObject;
import cjkimhello97.toy.crashMyServer.service.auth.testdata.AuthServiceTestDataBuilder;
import cjkimhello97.toy.crashMyServer.service.chat.testdata.GroupChatServiceFixtureObject;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class MongoIntegrationTest extends IntegrationTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private GroupChatService groupChatService;
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    private Member admin;
    private Member member;
    private final String CHAT_ROOM_NAME = "임의의 채팅방 이름";
    private final Long CHAT_ROOM_ID = 1L;

    @Test
    @DisplayName("[ CHAT ] MONGO TEST 1")
    void 전송한_채팅_메시지가_저장되어야_한다() {
        // given: 회원가입
        SignupRequest signupRequestOfAdmin = AuthServiceTestDataBuilder.signupRequestBuilder()
                .nickname("admin")
                .build();
        SignupRequest signupRequestOfMember = AuthServiceTestDataBuilder.signupRequestBuilder()
                .nickname("aaa")
                .build();

        authService.signUp(signupRequestOfAdmin);
        authService.signUp(signupRequestOfMember);

        admin = memberRepository.findByNickname(signupRequestOfAdmin.nickname()).get();
        member = memberRepository.findByNickname(signupRequestOfMember.nickname()).get();

        // given: 채팅방 생성
        groupChatService.createGroupChatRoom(admin.getMemberId(), CHAT_ROOM_NAME);

        // given: 채팅방 입장
        groupChatService.enterGroupChatRoom(CHAT_ROOM_ID, member.getMemberId());

        // when : 채팅 메시지 전송
        GroupChatMessageRequest groupChatMessageRequest = GroupChatServiceFixtureObject.groupChatMessageRequest();
        groupChatService.saveGroupChatMessage(groupChatMessageRequest);

        // then: 전송한 채팅 메시지가 저장되어야 한다
        List<ChatMessage> chatMessages = chatMessageRepository.findAll();
        ChatMessage chatMessage = chatMessages.get(chatMessages.size() - 1);

        assertEquals(chatMessage.getChatRoomId(), groupChatMessageRequest.getChatRoomId());
        assertEquals(chatMessage.getSenderId(), groupChatMessageRequest.getSenderId());
        assertEquals(chatMessage.getSenderNickname(), groupChatMessageRequest.getSenderNickname());
        assertEquals(chatMessage.getContent(), groupChatMessageRequest.getContent());
        assertEquals(chatMessage.getCreatedAt().toLocalDate(), groupChatMessageRequest.getCreatedAt().toLocalDate());
    }
}
