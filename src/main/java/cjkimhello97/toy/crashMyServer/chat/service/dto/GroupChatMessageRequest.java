package cjkimhello97.toy.crashMyServer.chat.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(title = "[ STOMP ] GroupChatMessageRequest : 그룹 채팅방 채팅 메시지 전송 요청 DTO")
public class GroupChatMessageRequest {

    @NotNull
    Long chatRoomId;
    @NotNull
    String senderNickname;
    Long senderId;
    @NotBlank
    String content;
    LocalDateTime createdAt;

    public void setCreatedAtNow() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public GroupChatMessageRequest(Long chatRoomId, String senderNickname, Long senderId, String content) {
        this.chatRoomId = chatRoomId;
        this.senderNickname = senderNickname;
        this.senderId = senderId;
        this.content = content;
    }
}

