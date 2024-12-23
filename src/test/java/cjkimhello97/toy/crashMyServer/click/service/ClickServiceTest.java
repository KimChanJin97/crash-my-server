package cjkimhello97.toy.crashMyServer.click.service;

import cjkimhello97.toy.crashMyServer.IntegrationTest;
import cjkimhello97.toy.crashMyServer.kafka.service.KafkaClickListener;
import cjkimhello97.toy.crashMyServer.kafka.service.KafkaClickRankListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public class ClickServiceTest extends IntegrationTest {

    @Autowired
    private ClickService clickService;
    @MockBean
    private KafkaClickListener kafkaClickListener;
    @MockBean
    private KafkaClickRankListener kafkaClickRankListener;

    private static final String CLICK_TOPIC = "click";
    private static final String CLICK_RANK_TOPIC = "click-rank";
}
