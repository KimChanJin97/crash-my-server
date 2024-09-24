package cjkimhello97.toy.crashMyServer.click.controller;

import static cjkimhello97.toy.crashMyServer.click.utils.CountFormatter.format;

import cjkimhello97.toy.crashMyServer.auth.support.AuthMember;
import cjkimhello97.toy.crashMyServer.click.domain.Click;
import cjkimhello97.toy.crashMyServer.click.service.ClickService;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaClickRankRequest;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaClickRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/click")
@RequiredArgsConstructor
public class ClickController {

    private final ClickService clickService;
    private final KafkaTemplate<String, KafkaClickRequest> kafkaClickRequestKafkaTemplate;
    private final KafkaTemplate<String, KafkaClickRankRequest> kafkaClickRankRequestKafkaTemplate;

    @PostMapping
    public void click(@AuthMember Long memberId) {
        Click click = clickService.click(memberId);
        KafkaClickRequest kafkaClickRequest = KafkaClickRequest.builder()
                .count(format(click.getCount()))
                .nickname(click.getMember().getNickname())
                .build();
        kafkaClickRequestKafkaTemplate.send("click", null, kafkaClickRequest);

        List<Click> topTenClicks = clickService.getTopTenClicks();
        Map<String, String> clickRank = new HashMap<>();
        topTenClicks.stream().forEach(c -> clickRank.put(c.getMember().getNickname(), format(c.getCount())));
        KafkaClickRankRequest kafkaClickRankRequest = KafkaClickRankRequest.builder()
                .clickRank(clickRank)
                .build();
        kafkaClickRankRequestKafkaTemplate.send("click-rank", null, kafkaClickRankRequest);
    }
}
