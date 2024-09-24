package cjkimhello97.toy.crashMyServer.chat.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponse {

    private String id;
    private Long chatRoomId;
    private Long senderId;
    private String content;
    private String createdAt;
}
