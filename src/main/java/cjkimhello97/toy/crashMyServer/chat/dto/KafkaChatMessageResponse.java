package cjkimhello97.toy.crashMyServer.chat.dto;

import lombok.Builder;

@Builder
public record KafkaChatMessageResponse(
        boolean isProcessed,
        KafkaChatMessageRequest request
) {
}
