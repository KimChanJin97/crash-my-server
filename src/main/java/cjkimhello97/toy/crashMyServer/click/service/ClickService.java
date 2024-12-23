package cjkimhello97.toy.crashMyServer.click.service;

import cjkimhello97.toy.crashMyServer.click.domain.Click;
import cjkimhello97.toy.crashMyServer.click.dto.KafkaClickClickRankRequests;
import cjkimhello97.toy.crashMyServer.click.dto.KafkaClickRankRequest;
import cjkimhello97.toy.crashMyServer.click.dto.KafkaClickRequest;
import cjkimhello97.toy.crashMyServer.click.exception.ClickException;
import cjkimhello97.toy.crashMyServer.click.exception.ClickExceptionType;
import cjkimhello97.toy.crashMyServer.click.repository.ClickRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickService {

    private final KafkaTemplate<String, KafkaClickRequest> kafkaClickRequestKafkaTemplate;
    private final KafkaTemplate<String, KafkaClickRankRequest> kafkaClickRankRequestKafkaTemplate;
    private final ClickRepository clickRepository;

    @Transactional
    public KafkaClickClickRankRequests click(Long memberId) {
        Click click = getClickByMemberId(memberId);
        String clickUuid = String.valueOf(UUID.randomUUID());
        KafkaClickRequest kafkaClickRequest = KafkaClickRequest.builder()
                .uuid(clickUuid)
                .count(String.valueOf(click.getCount()))
                .nickname(click.getMember().getNickname())
                .build();
        kafkaClickRequestKafkaTemplate.send("click", kafkaClickRequest);

        Map<String, String> clickRank = new HashMap<>();
        String clickRankUuid = String.valueOf(UUID.randomUUID());
        List<Click> topTenClicks = getTopTenClicks();
        topTenClicks.stream()
                .forEach(c -> clickRank.put(c.getMember().getNickname(), String.valueOf(click.getCount())));
        KafkaClickRankRequest kafkaClickRankRequest = KafkaClickRankRequest.builder()
                .uuid(clickRankUuid)
                .clickRank(clickRank)
                .build();
        kafkaClickRankRequestKafkaTemplate.send("click-rank", kafkaClickRankRequest);
        click.addCount();

        return KafkaClickClickRankRequests.builder()
                .kafkaClickRequest(kafkaClickRequest)
                .kafkaClickRankRequest(kafkaClickRankRequest)
                .build();
    }

    public List<Click> getTopTenClicks() {
        return clickRepository.findTop10ClicksByCountDesc(PageRequest.of(0, 10));
    }

    public Click getClickByMemberId(Long memberId) {
        return clickRepository.findByMemberMemberId(memberId)
                .orElseThrow(() -> new ClickException(ClickExceptionType.CLICK_NOT_FOUND));
    }
}
