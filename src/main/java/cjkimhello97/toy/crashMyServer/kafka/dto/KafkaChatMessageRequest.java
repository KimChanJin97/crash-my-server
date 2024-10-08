package cjkimhello97.toy.crashMyServer.kafka.dto;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
}
