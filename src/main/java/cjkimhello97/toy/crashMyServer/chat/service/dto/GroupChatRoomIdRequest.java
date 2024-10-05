package cjkimhello97.toy.crashMyServer.chat.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(title = "[ HTTP ] GroupChatIdRequest : 그룹 채팅방 입장/퇴장 요청 DTO, 그룹 채팅방 채팅 메시지 조회 요청 DTO")
public record GroupChatRoomIdRequest(
        Long chatRoomId
) {

}

