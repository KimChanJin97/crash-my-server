package cjkimhello97.toy.crashMyServer.service.auth;

import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.NICKNAME_EXCEED_LENGTH_TEN;
import static cjkimhello97.toy.crashMyServer.service.auth.testdata.AuthServiceTestDataBuilder.*;

import cjkimhello97.toy.crashMyServer.IntegrationTest;
import cjkimhello97.toy.crashMyServer.auth.controller.dto.SignInResponse;
import cjkimhello97.toy.crashMyServer.auth.exception.AuthException;
import cjkimhello97.toy.crashMyServer.auth.infrastructure.JwtProvider;
import cjkimhello97.toy.crashMyServer.auth.service.AuthService;
import cjkimhello97.toy.crashMyServer.auth.service.dto.SignupRequest;
import cjkimhello97.toy.crashMyServer.member.domain.Member;
import cjkimhello97.toy.crashMyServer.member.repository.MemberRepository;
import cjkimhello97.toy.crashMyServer.service.auth.testdata.AuthServiceFixtureObject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class MysqlIntegrationTest extends IntegrationTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("[ AUTH ] MYSQL TEST 1")
    void 닉네임과_비밀번호가_존재하면_로그인_처리되어야_한다() {
        // given: 회원가입 요청 DTO 생성
        SignupRequest signupRequest = AuthServiceFixtureObject.signupRequest();
        String nickname = signupRequest.nickname();
        String password = signupRequest.password();

        // when: 회원가입
        authService.signUp(signupRequest);

        // then: 닉네임과 비밀번호가 존재하면 로그인 처리되어야 한다
        Member member = memberRepository.findByNickname(nickname).get();
        SignInResponse signinResponse = authService.signIn(member, password);

        Assertions.assertNotNull(signinResponse.accessToken());
        Assertions.assertNotNull(signinResponse.refreshToken());
    }

    @Test
    @DisplayName("[ AUTH ] MYSQL TEST 2")
    void 닉네임은_존재하지만_비밀번호가_존재하지_않다면_로그인_처리되어야_한다() {
        // given: 회원가입 요청 DTO 생성
        SignupRequest signupRequest = AuthServiceFixtureObject.signupRequest();
        String nickname = signupRequest.nickname();
        String password = signupRequest.password();

        // when: 회원가입
        authService.signUp(signupRequest);

        // then: 닉네임은 존재하지만 비밀번호가 존재하지 않다면 로그인 처리되어야 한다
        Member member = memberRepository.findByNickname(nickname).get();
        String wrongPassword = "틀린" + password;

        Assertions.assertThrows(AuthException.class, () -> {
            authService.signIn(member, wrongPassword);
        });
    }

    @Test
    @DisplayName("[ AUTH ] MYSQL TEST 3")
    void 닉네임이_존재하지_않다면_회원가입_처리_후_로그인_처리되어야_한다() {
        // given: 회원가입 요청 DTO 생성
        SignupRequest signupRequest = AuthServiceFixtureObject.signupRequest();

        // when: 회원가입
        authService.signUp(signupRequest);

        // then: 닉네임이 존재하지 않다면 회원가입 처리 후 로그인 처리되어야 한다
        Member member = memberRepository.findByNickname(signupRequest.nickname()).orElse(null);
        Assertions.assertNotNull(member);
    }

    @Test
    @DisplayName("[ AUTH ] MYSQL TEST 4")
    void 회원가입_시_멤버_아이디가_토큰의_클레임에_저장되어_발행되어야_한다() {
        // given: 회원가입 요청 DTO 생성
        SignupRequest signupRequestOfA = signupRequestBuilder().nickname("aaa").build();
        SignupRequest signupRequestOfB = signupRequestBuilder().nickname("bbb").build();

        // when: 회원가입 요청
        SignInResponse signInResponseOfA = authService.signUp(signupRequestOfA);
        SignInResponse signInResponseOfB = authService.signUp(signupRequestOfB);

        entityManager.flush();
        entityManager.clear();

        String accessTokenOfA = signInResponseOfA.accessToken();
        String accessTokenOfB = signInResponseOfB.accessToken();

        Long memberIdOfA = jwtProvider.extractId(accessTokenOfA);
        Long memberIdOfB = jwtProvider.extractId(accessTokenOfB);

        // then: 회원가입 시 멤버 아이디가 토큰의 클레임에 저장되어 발행되어야 한다
        Assertions.assertEquals(memberIdOfA, 1);
        Assertions.assertEquals(memberIdOfB, 2);
    }

    @Test
    @DisplayName("[ AUTH ] MYSQL TEST 5")
    void 닉네임의_길이가_10을_초과하지_않다면_예외가_발생하지_않아야_한다() {
        // given: 회원가입 요청 DTO 생성
        final String NICKNAME = "0123456789";
        SignupRequest signupRequest = signupRequestBuilder().nickname(NICKNAME).build();

        // when & then: 닉네임의 길이가 10을 초과하지 않다면 예외가 발생하지 않아야 한다
        Assertions.assertDoesNotThrow(() -> {
            authService.signUp(signupRequest);
        });
    }

    @Test
    @DisplayName("[ AUTH ] MYSQL TEST 6")
    void 닉네임의_길이가_10을_초과한다면_예외가_발생해야_한다() {
        // given: 회원가입 요청 DTO 생성
        final String BAD_NICKNAME = "0123456789A";
        SignupRequest badSignupRequest = signupRequestBuilder().nickname(BAD_NICKNAME).build();

        // when & then: 닉네임의 길이가 10을 초과한다면 예외가 발생해야 한다
        AuthException authException = Assertions.assertThrows(AuthException.class, () -> {
            authService.signUp(badSignupRequest);
        });
        Assertions.assertEquals(authException.getExceptionType(), NICKNAME_EXCEED_LENGTH_TEN);
    }

}
