package cjkimhello97.toy.crashMyServer.service.auth;

import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.WRONG_PASSWORD;

import cjkimhello97.toy.crashMyServer.auth.exception.AuthException;
import cjkimhello97.toy.crashMyServer.auth.infrastructure.JwtProvider;
import cjkimhello97.toy.crashMyServer.auth.service.AuthService;
import cjkimhello97.toy.crashMyServer.auth.service.RedisTokenService;
import cjkimhello97.toy.crashMyServer.auth.service.dto.SignupRequest;
import cjkimhello97.toy.crashMyServer.click.repository.ClickRepository;
import cjkimhello97.toy.crashMyServer.member.domain.Member;
import cjkimhello97.toy.crashMyServer.member.repository.MemberRepository;
import cjkimhello97.toy.crashMyServer.service.auth.testdata.AuthServiceFixtureObject;
import cjkimhello97.toy.crashMyServer.service.auth.testdata.AuthServiceTestDataBuilder;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MockTest {

    private JwtProvider jwtProvider;
    private PasswordEncoder passwordEncoder;
    private MemberRepository memberRepository;
    private ClickRepository clickRepository;
    private RedisTokenService redisTokenService;
    private AuthService authService;

    @BeforeEach
    void beforeEach() {
        jwtProvider = Mockito.mock(JwtProvider.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        memberRepository = Mockito.mock(MemberRepository.class);
        clickRepository = Mockito.mock(ClickRepository.class);
        redisTokenService = Mockito.mock(RedisTokenService.class);

        authService = new AuthService(
                jwtProvider,
                passwordEncoder,
                memberRepository,
                clickRepository,
                redisTokenService
        );
    }

    @Test
    @DisplayName("[ AUTH ] MOCK TEST 1")
    void 닉네임과_비밀번호가_존재하면_로그인_처리되어야_한다() {
        // given: 회원가입 요청 DTO 생성
        SignupRequest signupRequest = AuthServiceFixtureObject.signupRequest();
        String nickname = signupRequest.nickname();
        String password = signupRequest.password();

        Member member = Member.builder()
                .memberId(1L)
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .build();

        Mockito.when(memberRepository.findByNickname(nickname))
                .thenReturn(Optional.of(member)); // Stubbing 으로 멤버가 저장되어 있다고 가정
        Mockito.when(passwordEncoder.matches(password, member.getPassword()))
                .thenReturn(true); // 올바른 비밀번호를 입력한다고 가정

        // when: 회원가입
        authService.signUp(signupRequest);

        // then: 닉네임과 비밀번호가 존재하면 로그인 처리되어야 한다
        Mockito.verify(memberRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("[ AUTH ] MOCK TEST 2")
    void 회원가입_시_닉네임은_존재하지만_비밀번호가_존재하지_않다면_예외가_발생해야_한다() {
        // given: 회원가입 요청 DTO 생성
        SignupRequest signupRequest = AuthServiceTestDataBuilder.signupRequestBuilder().build();
        String nickname = signupRequest.nickname();
        String password = signupRequest.password();

        Member member = Member.builder()
                .memberId(1L)
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .build();

        Mockito.when(memberRepository.findByNickname(nickname))
                .thenReturn(Optional.of(member)); // Stubbing 으로 멤버가 저장되어 있다고 가정
        Mockito.when(passwordEncoder.matches(password, member.getPassword()))
                .thenReturn(false); // 틀린 비밀번호를 입력한다고 가정

        // when & then: 회원가입 시 닉네임은 존재하지만 비밀번호가 존재하지 않다면 예외가 발생해야 한다
        AuthException authException = Assertions.assertThrows(AuthException.class, () -> {
            authService.signUp(signupRequest);
        });
        Assertions.assertEquals(authException.getExceptionType(), WRONG_PASSWORD);

        Mockito.verify(memberRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("[ AUTH ] MOCK TEST 3")
    void 닉네임이_존재하지_않다면_회원가입_후_로그인_처리되어야_한다() {
        // given: 회원가입 요청 DTO 생성
        SignupRequest signupRequest = AuthServiceFixtureObject.signupRequest();
        String nickname = signupRequest.nickname();
        String password = signupRequest.password();

        Member member = Member.builder()
                .memberId(1L)
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .build();

        Mockito.when(memberRepository.findByNickname(nickname))
                .thenReturn(Optional.empty()); // Stubbing 으로 멤버가 저장되어 있지 않다고 가정
        Mockito.when(passwordEncoder.matches(password, member.getPassword()))
                .thenReturn(true); // 회원가입 로직 이후에는 멤버가 저장되어 있다고 가정

        // when: 회원가입
        authService.signUp(signupRequest);

        // then: 닉네임이 존재하지않다면 회원가입 후 로그인 처리되어야 한다
        Mockito.verify(memberRepository, Mockito.times(1)).save(Mockito.any());
    }
}
