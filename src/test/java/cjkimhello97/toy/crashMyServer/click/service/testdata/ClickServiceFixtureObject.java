package cjkimhello97.toy.crashMyServer.click.service.testdata;

import static cjkimhello97.toy.crashMyServer.click.utils.CountFormatter.format;

import cjkimhello97.toy.crashMyServer.click.dto.KafkaClickRankRequest;
import cjkimhello97.toy.crashMyServer.click.dto.KafkaClickRequest;
import java.util.Map;

public class ClickServiceFixtureObject {

    private static final Map<String, String> CLICK_RANK = Map.of(
            "aaa", "0",
            "bbb", "100"
    );
    private static final String NICKNAME = "aaa";
    private static final Double COUNT = 0D;

    public static KafkaClickRequest kafkaClickRequest() {
        return KafkaClickRequest.builder()
                .nickname(NICKNAME)
                .count(format(COUNT))
                .build();
    }

    public static KafkaClickRankRequest kafkaClickRankRequest() {
        return KafkaClickRankRequest.builder()
                .clickRank(CLICK_RANK)
                .build();
    }
}
