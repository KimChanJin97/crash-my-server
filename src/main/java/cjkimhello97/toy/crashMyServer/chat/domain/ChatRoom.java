package cjkimhello97.toy.crashMyServer.chat.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import cjkimhello97.toy.crashMyServer.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "chat_room_id", insertable = false, updatable = false)
    private Long chatRoomId;

    private String chatRoomName;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member host;

    @ManyToMany(mappedBy = "chatRooms", fetch = LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private Set<Member> members = new HashSet<>();

    @Column(name = "created_at")
    @CreatedDate
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-ddTHH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @Builder
    public ChatRoom(Long chatRoomId, Member host, String chatRoomName) {
        this.chatRoomId = chatRoomId;
        this.host = host;
        this.chatRoomName = chatRoomName;
    }
}
