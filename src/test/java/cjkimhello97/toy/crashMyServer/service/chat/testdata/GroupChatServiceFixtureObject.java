package cjkimhello97.toy.crashMyServer.service.chat.testdata;

import static cjkimhello97.toy.crashMyServer.chat.utils.GroupChatMessageUtils.enterGroupChatRoomMessage;
import static cjkimhello97.toy.crashMyServer.chat.utils.GroupChatMessageUtils.leaveGroupChatRoomMessage;

import cjkimhello97.toy.crashMyServer.chat.service.dto.GroupChatMessageRequest;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaChatMessageRequest;

public class GroupChatServiceFixtureObject {

    private static final String SENDER_NICKNAME = "aaa";
    private static final Long SENDER_ID = 1L;
    private static final Long CHAT_ROOM_ID = 1L;
    private static final String CONTENT = "안녕하세요";
    private static final String CREATED_AT = "2024-10-24T00:00:00.000000";

    public static GroupChatMessageRequest groupChatMessageRequest() {
        return GroupChatMessageRequest.builder()
                .chatRoomId(CHAT_ROOM_ID)
                .senderNickname(SENDER_NICKNAME)
                .senderId(SENDER_ID)
                .content(CONTENT)
                .build();
    }

    public static KafkaChatMessageRequest kafkaEnterRequest() {
        return KafkaChatMessageRequest.builder()
                .senderNickname(SENDER_NICKNAME)
                .senderId(SENDER_ID)
                .chatRoomId(CHAT_ROOM_ID)
                .content(enterGroupChatRoomMessage(SENDER_NICKNAME))
                .createdAt(null)
                .build();
    }

    public static KafkaChatMessageRequest kafkaChatMessageRequest() {
        return KafkaChatMessageRequest.builder()
                .senderNickname(SENDER_NICKNAME)
                .senderId(SENDER_ID)
                .chatRoomId(CHAT_ROOM_ID)
                .content(CONTENT)
                .createdAt(CREATED_AT)
                .build();
    }

    public static KafkaChatMessageRequest kafkaLeaveRequest() {
        return KafkaChatMessageRequest.builder()
                .senderNickname(SENDER_NICKNAME)
                .senderId(SENDER_ID)
                .chatRoomId(CHAT_ROOM_ID)
                .content(leaveGroupChatRoomMessage(SENDER_NICKNAME))
                .createdAt(null)
                .build();
    }
}
