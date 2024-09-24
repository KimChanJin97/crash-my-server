package cjkimhello97.toy.crashMyServer.chat.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class MemberChatRoomId implements Serializable {

    private Long memberId;
    private Long chatRoomId;
}
