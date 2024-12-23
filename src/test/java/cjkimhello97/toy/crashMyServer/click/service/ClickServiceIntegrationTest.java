package cjkimhello97.toy.crashMyServer.click.service;

import static cjkimhello97.toy.crashMyServer.click.service.testdata.ClickServiceFixtureObject.click;
import static cjkimhello97.toy.crashMyServer.click.service.testdata.ClickServiceFixtureObject.member;

import cjkimhello97.toy.crashMyServer.IntegrationTest;
import cjkimhello97.toy.crashMyServer.click.domain.Click;
import cjkimhello97.toy.crashMyServer.click.repository.ClickRepository;
import cjkimhello97.toy.crashMyServer.member.domain.Member;
import cjkimhello97.toy.crashMyServer.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public class ClickServiceIntegrationTest extends IntegrationTest {

    private static final Long MEMBER_ID = 1L;
    private static final Member MEMBER = member();
    private static final Click CLICK = click();

    @Autowired
    private ClickService clickService;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private ClickRepository clickRepository;

    @BeforeEach
    void setUp() {
        // given : 멤버가 저장되어있고, 클릭이 저장되어있다고 설정
        Mockito.when(memberRepository.findByMemberId(MEMBER_ID)).thenReturn(Optional.of(MEMBER));
        Mockito.when(clickRepository.findByMemberMemberId(MEMBER_ID)).thenReturn(Optional.of(CLICK));
    }

    @Test
    @DisplayName("클릭하면_클릭_카운트가_증가한다")
    void 클릭하면_클릭_카운트가_증가한다() {
        // given
        int count = clickService.getClickByMemberId(MEMBER_ID).getCount();
        // when : 클릭하면
        clickService.click(MEMBER_ID);
        // then : 클릭 카운트가 증가한다
        int countPlusOne = clickService.getClickByMemberId(MEMBER_ID).getCount();
        Assertions.assertEquals(count + 1, countPlusOne);
    }
}
