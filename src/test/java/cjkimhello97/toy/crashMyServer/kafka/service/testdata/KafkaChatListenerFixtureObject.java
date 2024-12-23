package cjkimhello97.toy.crashMyServer.kafka.service.testdata;

import cjkimhello97.toy.crashMyServer.chat.domain.ChatRoom;
import cjkimhello97.toy.crashMyServer.chat.domain.MemberChatRoom;
import cjkimhello97.toy.crashMyServer.chat.domain.MemberChatRoomId;
import cjkimhello97.toy.crashMyServer.chat.dto.GroupChatMessageRequest;
import cjkimhello97.toy.crashMyServer.chat.dto.KafkaChatMessageRequest;
import cjkimhello97.toy.crashMyServer.member.domain.Member;

public class KafkaChatListenerFixtureObject {

    private static final Long MEMBER_ID = 1L;
    private static final String NICKNAME = "aaa";
    private static final String PASSWORD = "aaa";
    private static final Long CHAT_ROOM_ID = 1L;
    private static final String CHAT_ROOM_NAME = "xxx";
    private static final String CONTENT = "안녕하세요";
    private static final String TEST_UUID = "test-uuid";

    public static Member member() {
        return Member.builder()
                .memberId(MEMBER_ID)
                .nickname(NICKNAME)
                .password(PASSWORD)
                .build();
    }

    public static ChatRoom chatRoom() {
        return ChatRoom.builder()
                .chatRoomId(CHAT_ROOM_ID)
                .chatRoomName(CHAT_ROOM_NAME)
                .host(member())
                .build();
    }

    public static GroupChatMessageRequest groupChatMessageRequest() {
        return GroupChatMessageRequest.builder()
                .chatRoomId(CHAT_ROOM_ID)
                .senderId(MEMBER_ID)
                .content(CONTENT)
                .build();
    }

    public static MemberChatRoom memberChatRoom() {
        return MemberChatRoom.builder()
                .id(memberChatRoomId())
                .member(member())
                .chatRoom(chatRoom())
                .build();
    }

    private static MemberChatRoomId memberChatRoomId() {
        return MemberChatRoomId.builder()
                .memberId(MEMBER_ID)
                .chatRoomId(CHAT_ROOM_ID)
                .build();
    }

    public static KafkaChatMessageRequest kafkaChatMessageRequest() {
        return KafkaChatMessageRequest.builder()
                .uuid(TEST_UUID)
                .chatRoomId(CHAT_ROOM_ID)
                .senderId(MEMBER_ID)
                .senderNickname(NICKNAME)
                .build();
    }
}
