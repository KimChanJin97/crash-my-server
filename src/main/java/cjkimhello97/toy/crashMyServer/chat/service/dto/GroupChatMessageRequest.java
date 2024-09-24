package cjkimhello97.toy.crashMyServer.chat.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupChatMessageRequest {

    @NotNull
    String senderNickname;
    @NotBlank
    String content;
    @NotNull
    Long chatRoomId;
    Long senderId; // can null
    LocalDateTime createdAt; // can null

    public void setCreatedAtNow() {
        this.createdAt = LocalDateTime.now();
    }
}
