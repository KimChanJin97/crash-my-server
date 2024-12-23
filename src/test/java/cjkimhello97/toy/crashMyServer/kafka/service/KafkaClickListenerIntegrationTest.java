package cjkimhello97.toy.crashMyServer.kafka.service;

import static cjkimhello97.toy.crashMyServer.kafka.service.testdata.KafkaClickListenerFixtureObject.click;

import cjkimhello97.toy.crashMyServer.IntegrationTest;
import cjkimhello97.toy.crashMyServer.click.domain.Click;
import cjkimhello97.toy.crashMyServer.click.dto.KafkaClickRequest;
import cjkimhello97.toy.crashMyServer.click.repository.ClickRepository;
import cjkimhello97.toy.crashMyServer.click.service.ClickService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@Order(0)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class KafkaClickListenerIntegrationTest extends IntegrationTest {

    private static final Long MEMBER_ID = 1L;
    private static final Click CLICK = click();

    @Autowired
    private ClickService clickService;
    @MockBean
    private ClickRepository clickRepository;
    @SpyBean
    private KafkaClickListener kafkaClickListener;

    @BeforeEach
    void setUp() {
        // given : 멤버가 저장되어있고, 채팅방이 생성되어 있도록 스텁
        Mockito.when(clickRepository.findByMemberMemberId(MEMBER_ID))
                .thenReturn(Optional.of(CLICK));
        Mockito.when(clickRepository.findTop10ClicksByCountDesc(Mockito.any(PageRequest.class)))
                .thenReturn(List.of(CLICK));
    }

    @Test
    void 클릭하면_클릭_메시지가_생산되고_소비된다() {
        // given & when : 클릭하면
        KafkaClickRequest producedRequest = clickService.click(MEMBER_ID).kafkaClickRequest();
        // then : 클릭 메시지가 생산되고 소비된다
        ArgumentCaptor<KafkaClickRequest> clickReqCaptor = ArgumentCaptor.forClass(KafkaClickRequest.class);
        ArgumentCaptor<Acknowledgment> ackCaptor = ArgumentCaptor.forClass(Acknowledgment.class);
        Mockito.verify(kafkaClickListener, Mockito.timeout(5000).times(1))
                .listenClickTopic(clickReqCaptor.capture(), ackCaptor.capture());

        KafkaClickRequest consumedRequest = clickReqCaptor.getValue();
        Assertions.assertEquals(producedRequest.getUuid(), consumedRequest.getUuid());
    }
}
