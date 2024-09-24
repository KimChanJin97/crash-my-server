package cjkimhello97.toy.crashMyServer.member.domain;

import static jakarta.persistence.FetchType.LAZY;

import cjkimhello97.toy.crashMyServer.chat.domain.ChatRoom;
import cjkimhello97.toy.crashMyServer.common.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@EqualsAndHashCode(of = "memberId", callSuper = false)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", insertable = false, updatable = false)
    private Long memberId;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "password")
    private String password;

    @ManyToMany(cascade = CascadeType.ALL, fetch = LAZY)
    @JsonManagedReference
    @JoinTable(
            name = "member_chat_room",
            joinColumns = {@JoinColumn(name = "member_id")},
            inverseJoinColumns = {@JoinColumn(name = "chat_room_id")}
    )
    private Set<ChatRoom> chatRooms = new HashSet<>();

    public void addChatRoom(ChatRoom chatRoom) {
        this.chatRooms.add(chatRoom);
    }

    public void removeChatRoom(ChatRoom chatRoom) {
        this.chatRooms.remove(chatRoom);
    }
}
