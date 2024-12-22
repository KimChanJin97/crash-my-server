package cjkimhello97.toy.crashMyServer.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "[ HTTP ] GroupChatMessageResponses : 그룹 채팅방 채팅 메시지 리스트 응답 DTO")
public class GroupChatMessageResponses {

    List<GroupChatMessageResponse> groupChatMessageResponses;
}
