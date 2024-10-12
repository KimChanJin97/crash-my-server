package cjkimhello97.toy.crashMyServer.service.click;

import static org.junit.jupiter.api.Assertions.*;

import cjkimhello97.toy.crashMyServer.IntegrationTest;
import cjkimhello97.toy.crashMyServer.auth.infrastructure.JwtProvider;
import cjkimhello97.toy.crashMyServer.auth.service.AuthService;
import cjkimhello97.toy.crashMyServer.auth.service.RedisTokenService;
import cjkimhello97.toy.crashMyServer.auth.service.dto.SignupRequest;
import cjkimhello97.toy.crashMyServer.click.domain.Click;
import cjkimhello97.toy.crashMyServer.click.repository.ClickRepository;
import cjkimhello97.toy.crashMyServer.click.service.ClickService;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaClickRankRequest;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaClickRequest;
import cjkimhello97.toy.crashMyServer.member.domain.Member;
import cjkimhello97.toy.crashMyServer.member.repository.MemberRepository;
import cjkimhello97.toy.crashMyServer.service.auth.testdata.AuthServiceTestDataBuilder;
import cjkimhello97.toy.crashMyServer.service.click.testdata.ClickServiceFixtureObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class KafkaMockTest extends IntegrationTest {

    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ClickRepository clickRepository;
    @Mock
    private RedisTokenService redisTokenService;
    @InjectMocks
    private AuthService authService;
    @Mock
    private KafkaTemplate<String, KafkaClickRequest> kafkaClickRequestKafkaTemplate;
    @Mock
    private KafkaTemplate<String, KafkaClickRankRequest> kafkaClickRankRequestKafkaTemplate;
    @InjectMocks
    private ClickService clickService;

    private Member memberA;
    private Member memberB;

    private final String MEMBER_A_NICKNAME = "aaa";
    private final String MEMBER_B_NICKNAME = "bbb";
    private final Long MEMBER_A_ID = 1L;
    private final Long MEMBER_B_ID = 2L;
    private final Double MEMBER_A_CLICK_COUNT = 0D;
    private final Double MEMBER_B_CLICK_COUNT = 100D;

    @BeforeEach
    void beforeEach() {
        // 회원가입
        SignupRequest signupRequestOfA = AuthServiceTestDataBuilder.signupRequestBuilder()
                .nickname(MEMBER_A_NICKNAME).build();
        SignupRequest signupRequestOfB = AuthServiceTestDataBuilder.signupRequestBuilder()
                .nickname(MEMBER_B_NICKNAME).build();

        memberA = Member.builder()
                .memberId(MEMBER_A_ID)
                .nickname(signupRequestOfA.nickname())
                .password(passwordEncoder.encode(signupRequestOfA.password()))
                .build();
        memberB = Member.builder()
                .memberId(MEMBER_B_ID)
                .nickname(signupRequestOfB.nickname())
                .password(passwordEncoder.encode(signupRequestOfB.password()))
                .build();

        Mockito.when(memberRepository.findByNickname(signupRequestOfA.nickname()))
                .thenReturn(Optional.of(memberA));
        Mockito.when(passwordEncoder.matches(signupRequestOfA.password(), memberA.getPassword()))
                .thenReturn(true);
        Mockito.when(memberRepository.findByNickname(signupRequestOfB.nickname()))
                .thenReturn(Optional.of(memberB));
        Mockito.when(passwordEncoder.matches(signupRequestOfB.password(), memberB.getPassword()))
                .thenReturn(true);

        authService.signUp(signupRequestOfA);
        authService.signUp(signupRequestOfB);

        // 클릭
        Click clickOfA = Click.builder()
                .member(memberA)
                .count(MEMBER_A_CLICK_COUNT)
                .build();
        Click clickOfB = Click.builder()
                .member(memberB)
                .count(MEMBER_B_CLICK_COUNT)
                .build();

        Mockito.when(clickRepository.findByMemberMemberId(memberA.getMemberId()))
                .thenReturn(Optional.of(clickOfA));
        Mockito.when(clickRepository.findByMemberMemberId(memberB.getMemberId()))
                .thenReturn(Optional.of(clickOfB));

        List<Click> clicks = new ArrayList<>();
        clicks.add(clickOfA);
        clicks.add(clickOfB);
        Collections.sort(clicks);

        Mockito.when(clickRepository.findTop10ClicksByCountDesc(PageRequest.of(0, 10)))
                .thenReturn(clicks);
    }

    @Test
    @DisplayName("[ CLICK ] MOCK TEST 1")
    void 클릭하면_카프카_클릭_메시지가_발행되어야_한다() {
        // given: 카프카 클릭 메시지 요청 DTO 생성
        KafkaClickRequest kafkaClickRequest = ClickServiceFixtureObject.kafkaClickRequest(); // aaa, 0

        // when: 클릭
        clickService.click(memberA.getMemberId());

        // then: 클릭하면 카프카 클릭 메시지가 발행되어야 한다
        ArgumentCaptor<String> clickTopicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<KafkaClickRequest> clickRequestCaptor = ArgumentCaptor.forClass(KafkaClickRequest.class);
        Mockito.verify(kafkaClickRequestKafkaTemplate).send(clickTopicCaptor.capture(), clickRequestCaptor.capture());

        assertEquals("click", clickTopicCaptor.getValue());

        assertEquals(kafkaClickRequest.getNickname(), clickRequestCaptor.getValue().getNickname());
        assertEquals(kafkaClickRequest.getCount(), clickRequestCaptor.getValue().getCount());
    }

    @Test
    @DisplayName("[ CLICK ] MOCK TEST 2")
    void 클릭하면_카프카_클릭_랭킹_메시지가_발행되어야_한다() {
        // given: 카프카 클릭 랭킹 메시지 요청 DTO 생성
        KafkaClickRankRequest kafkaClickRankRequest = ClickServiceFixtureObject.kafkaClickRankRequest(); // aaa:0, bbb:100

        // when: 클릭
        clickService.click(memberA.getMemberId());

        // then: 클릭하면 카프카 클릭 랭킹 메시지가 발행되어야 한다
        ArgumentCaptor<String> clickRankTopicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<KafkaClickRankRequest> clickRankRequestCaptor = ArgumentCaptor.forClass(KafkaClickRankRequest.class);
        Mockito.verify(kafkaClickRankRequestKafkaTemplate).send(clickRankTopicCaptor.capture(), clickRankRequestCaptor.capture());

        assertEquals("click-rank", clickRankTopicCaptor.getValue());

        assertEquals(
                kafkaClickRankRequest.getClickRank().get(memberA.getNickname()),
                clickRankRequestCaptor.getValue().getClickRank().get(memberA.getNickname())
        );
        assertEquals(
                kafkaClickRankRequest.getClickRank().get(memberB.getNickname()),
                clickRankRequestCaptor.getValue().getClickRank().get(memberB.getNickname())
        );
    }
}