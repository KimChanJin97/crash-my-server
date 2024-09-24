package cjkimhello97.toy.crashMyServer.chat.repository;

import cjkimhello97.toy.crashMyServer.chat.domain.ChatRoom;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface ChatRoomRepository extends Repository<ChatRoom, Long> {

    ChatRoom save(ChatRoom chatRoom);

    Optional<ChatRoom> findByChatRoomId(Long chatRoomId);
}
