package cjkimhello97.toy.crashMyServer.service.click;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cjkimhello97.toy.crashMyServer.IntegrationTest;
import cjkimhello97.toy.crashMyServer.auth.service.AuthService;
import cjkimhello97.toy.crashMyServer.auth.service.dto.SignupRequest;
import cjkimhello97.toy.crashMyServer.click.domain.Click;
import cjkimhello97.toy.crashMyServer.click.repository.ClickRepository;
import cjkimhello97.toy.crashMyServer.click.service.ClickService;
import cjkimhello97.toy.crashMyServer.member.domain.Member;
import cjkimhello97.toy.crashMyServer.member.repository.MemberRepository;
import cjkimhello97.toy.crashMyServer.service.auth.testdata.AuthServiceTestDataBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MysqlIntegrationTest extends IntegrationTest {

    @Autowired
    private ClickService clickService;
    @Autowired
    private AuthService authService;
    @Autowired
    private ClickRepository clickRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Member memberA;
    private Member memberB;

    @BeforeEach
    void beforeEach() {
        // 회원가입 요청 DTO 생성
        SignupRequest signupRequestOfA = AuthServiceTestDataBuilder.signupRequestBuilder()
                .nickname("aaa")
                .build();
        SignupRequest signupRequestOfB = AuthServiceTestDataBuilder.signupRequestBuilder()
                .nickname("bbb")
                .build();
        // 회원가입
        authService.signUp(signupRequestOfA);
        authService.signUp(signupRequestOfB);

        memberA = memberRepository.findByNickname(signupRequestOfA.nickname()).get();
        memberB = memberRepository.findByNickname(signupRequestOfB.nickname()).get();
    }

    @Test
    @DisplayName("[ CLICK ] MYSQL TEST 1")
    void 클릭하면_클릭_카운트가_증가되어야_한다() {
        // given: 기존의 클릭 횟수 조회
        Click oldClick = clickRepository.findByMemberMemberId(memberA.getMemberId()).get();
        Double oldCount = oldClick.getCount();

        // when: 클릭
        Click newClick = clickService.click(memberA.getMemberId());
        Double newCount = newClick.getCount();

        // then: 클릭하면 클릭 카운트가 증가되어야 한다
        assertEquals(oldCount + 1, newCount);
    }

    @Test
    @DisplayName("[ CLICK ] MYSQL TEST 2")
    void 클릭하면_그에_따라_클릭_랭킹이_업데이트된다() {
        // given & when: A가 1번 클릭
        clickService.click(memberA.getMemberId());

        Click clickOfA = clickService.getTopTenClicks().get(0); // 내림차순 정렬이므로 A가 1등
        Click clickOfB = clickService.getTopTenClicks().get(1);

        Member memberA = clickOfA.getMember();
        Double countOfA = clickOfA.getCount();
        Member memberB = clickOfB.getMember();
        Double countOfB = clickOfB.getCount();

        // then: 총 클릭을 1번한 A가 클릭 횟수 2회로 1등, 총 클릭을 0번한 클릭 횟수 1회로 B가 2등이다
        Assertions.assertEquals(this.memberA, memberA);
        Assertions.assertEquals(countOfA, 2D);
        Assertions.assertEquals(this.memberB, memberB);
        Assertions.assertEquals(countOfB, 1D);

        // given & when: B가 2번 클릭
        clickService.click(this.memberB.getMemberId());
        clickService.click(this.memberB.getMemberId());

        clickOfB = clickService.getTopTenClicks().get(0); // 내림차순 정렬이므로 B가 1등
        clickOfA = clickService.getTopTenClicks().get(1);

        memberB = clickOfB.getMember();
        countOfB = clickOfB.getCount();
        memberA = clickOfA.getMember();
        countOfA = clickOfA.getCount();

        // then: 총 클릭을 2번한 B가 클릭 횟수 3회로 1등, 총 클릭을 1번한 클릭 횟수 2회로 A가 2등이다
        Assertions.assertEquals(this.memberB, memberB);
        Assertions.assertEquals(countOfB, 3D);
        Assertions.assertEquals(this.memberA, memberA);
        Assertions.assertEquals(countOfA, 2D);
    }
}