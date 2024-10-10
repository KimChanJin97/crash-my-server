package cjkimhello97.toy.crashMyServer.click.service;

import static cjkimhello97.toy.crashMyServer.click.utils.CountFormatter.format;

import cjkimhello97.toy.crashMyServer.click.domain.Click;
import cjkimhello97.toy.crashMyServer.click.exception.ClickException;
import cjkimhello97.toy.crashMyServer.click.exception.ClickExceptionType;
import cjkimhello97.toy.crashMyServer.click.repository.ClickRepository;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaClickRankRequest;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaClickRequest;
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
    public Click click(Long memberId) {
        log.info("[ ClickService ] click START ============================================");
        Click click = getClickByMemberId(memberId);
        KafkaClickRequest kafkaClickRequest = KafkaClickRequest.builder()
                .uuid(String.valueOf(UUID.randomUUID()))
                .count(format(click.getCount()))
                .nickname(click.getMember().getNickname())
                .build();
        kafkaClickRequestKafkaTemplate.send("click", kafkaClickRequest);

        Map<String, String> clickRank = new HashMap<>();
        List<Click> topTenClicks = getTopTenClicks();
        topTenClicks.stream().forEach(c -> clickRank.put(c.getMember().getNickname(), format(c.getCount())));
        KafkaClickRankRequest kafkaClickRankRequest = KafkaClickRankRequest.builder()
                .uuid(String.valueOf(UUID.randomUUID()))
                .clickRank(clickRank)
                .build();
        kafkaClickRankRequestKafkaTemplate.send("click-rank", kafkaClickRankRequest);

        log.info("[ ClickService ] click - addCount()");
        return click.addCount();
    }

    public List<Click> getTopTenClicks() {
        log.info("[ ClickService ] click - getClickByMemberId(memberId)");
        return clickRepository.findTop10ClicksByCountDesc(PageRequest.of(0, 10));
    }

    public Click getClickByMemberId(Long memberId) {
        log.info("[ ClickService ] click - getTopTenClicks");
        return clickRepository.findByMemberMemberId(memberId)
                .orElseThrow(() -> new ClickException(ClickExceptionType.CLICK_NOT_FOUND));
    }
}
