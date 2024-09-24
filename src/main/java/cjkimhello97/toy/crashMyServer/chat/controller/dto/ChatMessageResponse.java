package cjkimhello97.toy.crashMyServer.chat.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "[ HTTP ] ChatMessageResponse : 채팅 메시지 응답 DTO")
public class ChatMessageResponse {

    private String id;
    private Long chatRoomId;
    private Long senderId;
    private String content;
    private String createdAt;
}
