package cjkimhello97.toy.crashMyServer.chat.repository;

import cjkimhello97.toy.crashMyServer.chat.domain.MemberChatRoom;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface MemberChatRoomRepository extends Repository<MemberChatRoom, Long> {

    Optional<MemberChatRoom> findByMemberMemberIdAndChatRoomChatRoomId(Long memberId, Long chatRoomId);
}
