package cjkimhello97.toy.crashMyServer.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "사용 X")
public record GroupChatRoomNameRequest(
        String chatRoomName
) {

}
