package cjkimhello97.toy.crashMyServer.click.dto;

import lombok.Builder;

@Builder
public record KafkaClickClickRankRequests(
        KafkaClickRequest kafkaClickRequest,
        KafkaClickRankRequest kafkaClickRankRequest
) {
}
