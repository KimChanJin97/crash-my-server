package cjkimhello97.toy.crashMyServer.chat.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "[ STOMP ] GroupChatMessageRequest : 그룹 채팅방 채팅 메시지 전송 요청 DTO")
public class GroupChatMessageRequest {

    @NotNull
    String senderNickname;
    @NotBlank
    String content;
    @NotNull
    Long chatRoomId;
    Long senderId;
    LocalDateTime createdAt;

    public void setCreatedAtNow() {
        this.createdAt = LocalDateTime.now();
    }
}

