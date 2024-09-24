package cjkimhello97.toy.crashMyServer.chat.controller.dto;

import cjkimhello97.toy.crashMyServer.chat.domain.ChatRoom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GroupChatRoomResponse {

    private Long chatRoomId;
    private String chatRoomName;
    private SimpleMemberResponse host;
    private Set<SimpleMemberResponse> members;
    private LocalDateTime createdAt;
    private ChatMessageResponse chatMessageResponse;

    public static GroupChatRoomResponse from(ChatRoom chatRoom) {
        GroupChatRoomResponse groupChatRoomResponse = GroupChatRoomResponse.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .chatRoomName(chatRoom.getChatRoomName())
                .host(chatRoom.getHost() == null ? null : SimpleMemberResponse.from(chatRoom.getHost()))
                .createdAt(chatRoom.getCreatedAt())
                .build();
        Set<SimpleMemberResponse> simpleMemberResponses = new HashSet<>();
        chatRoom.getMembers().forEach(member -> simpleMemberResponses.add(SimpleMemberResponse.from(member)));
        groupChatRoomResponse.setMembers(simpleMemberResponses);
        return groupChatRoomResponse;
    }
}
