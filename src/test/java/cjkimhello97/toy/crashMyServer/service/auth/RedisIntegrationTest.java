package cjkimhello97.toy.crashMyServer.service.auth;

import cjkimhello97.toy.crashMyServer.IntegrationTest;
import cjkimhello97.toy.crashMyServer.auth.controller.dto.SignInResponse;
import cjkimhello97.toy.crashMyServer.auth.controller.dto.TokenResponse;
import cjkimhello97.toy.crashMyServer.auth.infrastructure.JwtProvider;
import cjkimhello97.toy.crashMyServer.auth.service.AuthService;
import cjkimhello97.toy.crashMyServer.auth.service.RedisTokenService;
import cjkimhello97.toy.crashMyServer.auth.service.dto.ReissueRequest;
import cjkimhello97.toy.crashMyServer.auth.service.dto.SignupRequest;
import cjkimhello97.toy.crashMyServer.service.auth.testdata.AuthServiceFixtureObject;
import cjkimhello97.toy.crashMyServer.service.auth.testdata.AuthServiceTestDataBuilder;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class RedisIntegrationTest extends IntegrationTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private RedisTokenService redisTokenService;
    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("[ AUTH ] REDIS TEST 1")
    void 회원가입() {
        // given: 회원가입 요청 DTO 생성
        SignupRequest signupRequest = AuthServiceFixtureObject.signupRequest();

        // when: 회원가입 요청
        SignInResponse signinResponse = authService.signUp(signupRequest);
        String refreshToken = signinResponse.refreshToken();

        Long memberId = jwtProvider.extractId(refreshToken);
        String storedRefreshToken = redisTokenService.getRefreshToken(String.valueOf(memberId));

        // then: 회원가입 응답 DTO의 리프레시 토큰값과 레디스에 저장된 리프레시 토큰값이 일치하는지 검증
        Assertions.assertEquals(refreshToken, storedRefreshToken);
    }

    @Test
    @DisplayName("[ AUTH ] REDIS TEST 2")
    void 토큰_재발행() {
        // given: 회원가입 요청 DTO 생성
        SignupRequest signupRequest = AuthServiceFixtureObject.signupRequest();
        SignInResponse signinResponse = authService.signUp(signupRequest);
        String refreshToken = signinResponse.refreshToken();

        // given: 토큰 재발행 요청 DTO 생성
        ReissueRequest reissueRequest = AuthServiceTestDataBuilder.reissueRequestBuilder()
                .refreshToken(refreshToken)
                .build();

        // given: 리프레시 토큰으로부터 memberId 추출
        Long memberId = jwtProvider.extractId(refreshToken);

        // when: 토큰 재발행 요청
        TokenResponse tokenResponse = authService.reissueTokens(memberId, reissueRequest);
        String newRefreshToken = tokenResponse.refreshToken();
        String storedRefreshToken = redisTokenService.getRefreshToken(String.valueOf(memberId));

        // then: 토큰 재발행 응답 DTO의 리프레시 토큰값과 레디스에 저장된 리프레시 토큰값이 일치하는지 검증
        Assertions.assertEquals(newRefreshToken, storedRefreshToken);
    }

    @Test
    @DisplayName("[ AUTH ] REDIS TEST 3")
    void 토큰_재발행_시_멤버_아이디가_토큰의_클레임에_저장되어_발행() {
        // given: 회원가입 요청 DTO, 토큰 재발행 요청 DTO 생성
        SignupRequest signupRequestOfA = AuthServiceTestDataBuilder.signupRequestBuilder()
                .nickname("aaa")
                .build();
        SignInResponse signInResponseOfA = authService.signUp(signupRequestOfA);
        String refreshTokenOfA = signInResponseOfA.refreshToken();

        entityManager.flush();
        entityManager.clear();

        SignupRequest signupRequestOfB = AuthServiceTestDataBuilder.signupRequestBuilder()
                .nickname("bbb")
                .build();
        SignInResponse signInResponseOfB = authService.signUp(signupRequestOfB);
        String refreshTokenOfB = signInResponseOfB.refreshToken();

        entityManager.flush();
        entityManager.clear();

        Long memberIdOfA = jwtProvider.extractId(refreshTokenOfA);
        Long memberIdOfB = jwtProvider.extractId(refreshTokenOfB);

        ReissueRequest reissueRequestOfA = AuthServiceTestDataBuilder.reissueRequestBuilder()
                .refreshToken(refreshTokenOfA)
                .build();
        ReissueRequest reissueRequestOfB = AuthServiceTestDataBuilder.reissueRequestBuilder()
                .refreshToken(refreshTokenOfB)
                .build();

        // when: 토큰 재발행 요청
        TokenResponse tokenResponseOfA = authService.reissueTokens(memberIdOfA, reissueRequestOfA);
        TokenResponse tokenResponseOfB = authService.reissueTokens(memberIdOfB, reissueRequestOfB);

        String newAccessTokenOfA = tokenResponseOfA.accessToken();
        String newRefreshTokenOfA = tokenResponseOfA.refreshToken();
        String newAccessTokenOfB = tokenResponseOfB.accessToken();
        String newRefreshTokenOfB = tokenResponseOfB.refreshToken();

        // then: 각 토큰별 클레임 id 가 일치하는지 검증
        Assertions.assertEquals(jwtProvider.extractId(newAccessTokenOfA), 1);
        Assertions.assertEquals(jwtProvider.extractId(newRefreshTokenOfA), 1);
        Assertions.assertEquals(jwtProvider.extractId(newAccessTokenOfB), 2);
        Assertions.assertEquals(jwtProvider.extractId(newRefreshTokenOfB), 2);
    }
}
