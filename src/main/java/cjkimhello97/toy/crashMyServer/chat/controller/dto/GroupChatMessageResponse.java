package cjkimhello97.toy.crashMyServer.chat.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(title = "[ HTTP ] GroupChatMessageResponse : 그룹 채팅방 채팅 메시지 응답 DTO")
public class GroupChatMessageResponse {

    private String id;
    private String senderNickname;
    private String content;
    private String createdAt;
}
