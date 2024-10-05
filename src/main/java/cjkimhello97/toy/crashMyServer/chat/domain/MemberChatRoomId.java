package cjkimhello97.toy.crashMyServer.chat.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberChatRoomId implements Serializable {

    private Long memberId;
    private Long chatRoomId;

    @Builder
    public MemberChatRoomId(Long memberId, Long chatRoomId) {
        this.memberId = memberId;
        this.chatRoomId = chatRoomId;
    }
}
