package cjkimhello97.toy.crashMyServer.chat.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GroupChatMessageResponse {

    private String id;
    private String senderNickname;
    private String content;
    private String createdAt;
}
