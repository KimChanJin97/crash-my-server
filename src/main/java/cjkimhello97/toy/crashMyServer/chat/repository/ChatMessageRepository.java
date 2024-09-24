package cjkimhello97.toy.crashMyServer.chat.repository;

import cjkimhello97.toy.crashMyServer.chat.domain.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, Long> {

    List<ChatMessage> findAllByChatRoomIdAndCreatedAtGreaterThanEqual(Long chatRoomId, LocalDateTime createdAt);

    Optional<ChatMessage> findFirstByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);
}
