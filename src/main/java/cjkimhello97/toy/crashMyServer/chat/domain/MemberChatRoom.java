package cjkimhello97.toy.crashMyServer.chat.domain;

import static jakarta.persistence.FetchType.LAZY;

import cjkimhello97.toy.crashMyServer.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member_chat_room")
public class MemberChatRoom { // 다대다 중간 테이블

    @EmbeddedId
    private MemberChatRoomId id = new MemberChatRoomId(); // 다대다 중간 테이블 복합키

    @ManyToOne(fetch = LAZY)
    @MapsId("chatRoomId")
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;
}
