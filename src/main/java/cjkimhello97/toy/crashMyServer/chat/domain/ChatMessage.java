package cjkimhello97.toy.crashMyServer.chat.domain;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "chat_message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    private String id;

    private Long chatRoomId;

    private Long senderId;

    private String senderNickname;

    private Long receiverId;

    private String content;

    private LocalDateTime createdAt;

    @Builder
    public ChatMessage(
            Long senderId,
            String senderNickname,
            Long receiverId,
            String content,
            Long chatRoomId,
            LocalDateTime createdAt
    ) {
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.receiverId = receiverId;
        this.content = content;
        this.chatRoomId = chatRoomId;
        this.createdAt = createdAt;
    }
}
