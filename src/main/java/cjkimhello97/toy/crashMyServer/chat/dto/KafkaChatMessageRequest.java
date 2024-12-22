package cjkimhello97.toy.crashMyServer.chat.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaChatMessageRequest implements Serializable {

    private String uuid;
    private String senderNickname;
    private Long senderId;
    private Long chatRoomId;
    private String content;
    private String createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KafkaChatMessageRequest request = (KafkaChatMessageRequest) o;
        return Objects.equals(getUuid(), request.getUuid()) && Objects.equals(getSenderNickname(),
                request.getSenderNickname()) && Objects.equals(getSenderId(), request.getSenderId())
                && Objects.equals(getChatRoomId(), request.getChatRoomId()) && Objects.equals(
                getContent(), request.getContent()) && Objects.equals(getCreatedAt(), request.getCreatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid(), getSenderNickname(), getSenderId(), getChatRoomId(), getContent(),
                getCreatedAt());
    }
}
