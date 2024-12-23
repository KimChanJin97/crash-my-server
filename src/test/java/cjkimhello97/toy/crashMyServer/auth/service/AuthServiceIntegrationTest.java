package cjkimhello97.toy.crashMyServer.auth.service;

import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.WRONG_PASSWORD;
import static cjkimhello97.toy.crashMyServer.auth.service.testdata.AuthServiceTestDataBuilder.signupRequest;
import static cjkimhello97.toy.crashMyServer.token.exception.TokenExceptionInfo.NO_REFRESH_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cjkimhello97.toy.crashMyServer.IntegrationTest;
import cjkimhello97.toy.crashMyServer.auth.controller.dto.SignInResponse;
import cjkimhello97.toy.crashMyServer.auth.controller.dto.SignOutRequest;
import cjkimhello97.toy.crashMyServer.auth.controller.dto.SignUpRequest;
import cjkimhello97.toy.crashMyServer.auth.exception.AuthException;
import cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType;
import cjkimhello97.toy.crashMyServer.member.domain.Member;
import cjkimhello97.toy.crashMyServer.member.repository.MemberRepository;
import cjkimhello97.toy.crashMyServer.token.domain.AccessToken;
import cjkimhello97.toy.crashMyServer.token.domain.RefreshToken;
import cjkimhello97.toy.crashMyServer.token.exception.TokenException;
import cjkimhello97.toy.crashMyServer.token.service.TokenService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private MemberRepository memberRepository;

    private static final SignUpRequest SIGNUP_REQUEST = signupRequest().build();
    private static final SignUpRequest BAD_NICKNAME_SIGNUP_REQUEST = signupRequest().nickname("bbb").build();
    private static final SignUpRequest BAD_PASSWORD_SIGNUP_REQUEST = signupRequest().password("bbb").build();

    @Test
    @DisplayName("회원가입시_닉네임이_존재하지_않다면_회원가입_후_로그인_처리되어야한다")
    void 회원가입시_닉네임이_존재하지_않다면_회원가입_후_로그인_처리되어야한다() {
        // given : 미리 회원가입
        authService.signUp(SIGNUP_REQUEST);
        // when : 회원가입시 닉네임이 존재하지 않다면
        authService.signUp(BAD_NICKNAME_SIGNUP_REQUEST);
        // then : 회원가입 후 로그인 처리되어야 하고 멤버가 Mysql에 저장되어야 한다
        Member firstMember = getMemberByNickname(SIGNUP_REQUEST.nickname());
        Member secondMember = getMemberByNickname(BAD_NICKNAME_SIGNUP_REQUEST.nickname());
        assertNotEquals(firstMember, secondMember);
    }

    @Test
    @DisplayName("회원가입시_닉네임이_존재하고_비밀번호가_일치한다면_로그인_처리되어야_한다")
    void 회원가입시_닉네임이_존재하고_비밀번호가_일치한다면_로그인_처리되어야_한다() {
        // given : 미리 회원가입
        authService.signUp(SIGNUP_REQUEST);
        // when : 회원가입시 닉네임이 존재하고 비밀번호가 일치하다면
        authService.signUp(SIGNUP_REQUEST);
        // then : 로그인 처리되어야 하고 멤버가 Mysql에 저장되어야 한다
        List<Member> members = getAllMembers();
        assertEquals(1, members.size());
    }

    @Test
    @DisplayName("회원가입시_닉네임이_존재하지만_비밀번호가_일치하지_않다면_예외처리되어야_한다")
    void 회원가입시_닉네임이_존재하지만_비밀번호가_일치하지_않다면_예외처리되어야_한다() {
        // given : 미리 회원가입
        authService.signUp(SIGNUP_REQUEST);
        // when & then : 회원가입시 닉네임은 존재하지만 비밀번호가 일치하지 않다면 예외처리되어야 한다
        AuthException e = assertThrows(
                AuthException.class,
                () -> authService.signUp(BAD_PASSWORD_SIGNUP_REQUEST)
        );
        assertEquals(WRONG_PASSWORD.message(), e.getMessage());
    }

    @Test
    @DisplayName("인증시_닉네임이_존재하지_않다면_회원가입_후_로그인_처리되어야하고_리프레시_토큰이_발급되어야_한다")
    void 인증시_닉네임이_존재하지_않다면_회원가입_후_로그인_처리되어야하고_리프레시_토큰이_발급되어야_한다() {
        // given : 미리 회원가입
        RefreshToken firstRefreshToken = authService.signUp(SIGNUP_REQUEST).refreshToken();
        // when : 회원가입시 닉네임이 존재하지 않다면
        RefreshToken secondRefreshToken = authService.signUp(BAD_NICKNAME_SIGNUP_REQUEST).refreshToken();
        // then : 회원가입 후 로그인 처리되어야 하고 리프레시 토큰이 Redis에 저장되어야 한다
        assertNotEquals(firstRefreshToken, secondRefreshToken);
    }

    @Test
    @DisplayName("인증시_닉네임이_존재하고_비밀번호가_일치한다면_로그인_처리되어야하고_리프레시_토큰이_발급되어야_한다")
    void 인증시_닉네임이_존재하고_비밀번호가_일치한다면_로그인_처리되어야하고_리프레시_토큰이_발급되어야_한다() {
        // given : 미리 회원가입
        SignInResponse response = authService.signUp(SIGNUP_REQUEST);
        // when : 회원가입시 닉네임이 존재하고 비밀번호가 일치하다면
        SignInResponse newResponse = authService.signUp(SIGNUP_REQUEST);
        // then : 로그인 처리되어야 하고 리프레시 토큰이 발급되어야 한다
        RefreshToken refreshToken = response.refreshToken();
        RefreshToken newRefreshToken = newResponse.refreshToken();
        assertNotEquals(refreshToken, newRefreshToken);
    }

    @Test
    @DisplayName("인증시_닉네임이_존재하지만_비밀번호가_일치하지_않다면_예외처리되어야하고_리프레시_토큰이_발급되지_않아야_한다")
    void 인증시_닉네임이_존재하지만_비밀번호가_일치하지_않다면_예외처리되어야하고_리프레시_토큰이_발급되지_않아야_한다() {
        // given : 미리 회원가입
        RefreshToken refreshToken = authService.signUp(SIGNUP_REQUEST).refreshToken();
        // when & then : 회원가입시 닉네임이 존재하지만 비밀번호가 일치하지 않다면 예외처리되어야 하고
        AuthException e = assertThrows(
                AuthException.class,
                () -> authService.signUp(BAD_PASSWORD_SIGNUP_REQUEST)
        );
        assertEquals(WRONG_PASSWORD.message(), e.getMessage());
        // then : 새로운 리프레시 토큰이 Redis에 저장되지 않아야 한다
        Member member = getMemberByNickname(BAD_PASSWORD_SIGNUP_REQUEST.nickname());
        RefreshToken originRefreshToken = tokenService.getRefreshTokenByMemberId(member.getMemberId());
        assertEquals(refreshToken, originRefreshToken);
    }

    @Test
    @DisplayName("인증시_닉네임이_존재하지_않다면_회원가입_후_로그인_처리되어야하고_발급된_리프레시_토큰이_레디스에_저장되어야_한다")
    void 인증시_닉네임이_존재하지_않다면_회원가입_후_로그인_처리되어야하고_발급된_리프레시_토큰이_레디스에_저장되어야_한다() {
        // given : 미리 회원가입
        RefreshToken firstRefreshToken = authService.signUp(SIGNUP_REQUEST).refreshToken();
        System.out.println("첫번째 = " + firstRefreshToken.getClaims());
        System.out.println("첫번째 = " + memberRepository.findByNickname(SIGNUP_REQUEST.nickname()).get().getNickname());

        // when : 회원가입시 닉네임이 존재하지 않다면
        RefreshToken secondRefreshToken = authService.signUp(BAD_NICKNAME_SIGNUP_REQUEST).refreshToken();
        System.out.println("두번째 = " + secondRefreshToken.getClaims());
        System.out.println(
                "두번째 = " + memberRepository.findByNickname(BAD_NICKNAME_SIGNUP_REQUEST.nickname()).get().getNickname());

        // then : 회원가입 후 로그인 처리되어야 하고 리프레시 토큰이 Redis에 저장되어야 한다
        RefreshToken firstSavedRefreshToken = tokenService.getRefreshTokenByMemberId(firstRefreshToken.getMemberId());
        System.out.println("첫번째 = " + firstSavedRefreshToken.getClaims());
        RefreshToken secondSavedRefreshToken = tokenService.getRefreshTokenByMemberId(secondRefreshToken.getMemberId());
        System.out.println("두번째 = " + secondSavedRefreshToken.getClaims());
        assertNotNull(firstSavedRefreshToken);
        assertNotNull(secondSavedRefreshToken);
    }

    @Test
    @DisplayName("인증시_닉네임이_존재하고_비밀번호가_일치한다면_로그인_처리되어야하고_발급된_리프레시_토큰이_레디스에_저장되어야_한다")
    void 인증시_닉네임이_존재하고_비밀번호가_일치한다면_로그인_처리되어야하고_발급된_리프레시_토큰이_레디스에_저장되어야_한다() {
        // given : 미리 회원가입
        RefreshToken firstRefreshToken = authService.signUp(SIGNUP_REQUEST).refreshToken();
        RefreshToken firstSavedRefreshToken = tokenService.getRefreshTokenByMemberId(firstRefreshToken.getMemberId());
        // when : 회원가입시 닉네임이 존재하고 비밀번호가 일치하다면
        RefreshToken secondRefreshToken = authService.signUp(SIGNUP_REQUEST).refreshToken();
        RefreshToken secondSavedRefreshToken = tokenService.getRefreshTokenByMemberId(secondRefreshToken.getMemberId());
        // then : 로그인 처리되어야 하고 리프레시 토큰이 레디스에 저장되어야 한다
        assertEquals(firstRefreshToken, firstSavedRefreshToken);
        assertEquals(secondRefreshToken, secondSavedRefreshToken);
        assertNotEquals(firstSavedRefreshToken, secondSavedRefreshToken);
    }

    @Test
    @DisplayName("인증시_닉네임이_존재하지만_비밀번호가_일치하지_않다면_예외처리되어야하고_리프레시_토큰이_레디스에_저장되지_않아야_한다")
    void 인증시_닉네임이_존재하지만_비밀번호가_일치하지_않다면_예외처리되어야하고_리프레시_토큰이_레디스에_저장되지_않아야_한다() {
        // given : 미리 회원가입
        RefreshToken refreshToken = authService.signUp(SIGNUP_REQUEST).refreshToken();
        // when & then : 회원가입시 닉네임이 존재하지만 비밀번호가 일치하지 않다면 예외처리되어야 하고
        AuthException authException = assertThrows(
                AuthException.class,
                () -> authService.signUp(BAD_PASSWORD_SIGNUP_REQUEST)
        );
        assertEquals(WRONG_PASSWORD.message(), authException.getMessage());
        // then : 리프레시 토큰이 레디스에 저장되지 않아야 한다
        RefreshToken savedRefreshToken = tokenService.getRefreshTokenByMemberId(refreshToken.getMemberId());
        assertEquals(refreshToken, savedRefreshToken);
    }

    @Test
    @DisplayName("로그아웃시_Redis에_저장되어있던_리프레시_토큰은_삭제되어야_한다")
    void 로그아웃시_Redis에_저장되어있던_리프레시_토큰은_삭제되어야_한다() {
        // given : 회원가입
        AccessToken accessToken = authService.signUp(SIGNUP_REQUEST).accessToken();
        // when : 로그아웃시
        SignOutRequest signOutRequest = SignOutRequest.builder()
                .claims(accessToken.getClaims())
                .build();
        Member savedMember = getMemberByNickname(SIGNUP_REQUEST.nickname());
        authService.signOut(savedMember.getMemberId(), signOutRequest);
        // then : Redis에 저장되어있던 리프레시 토큰은 삭제되어야 한다
        TokenException e = assertThrows(TokenException.class,
                () -> tokenService.getRefreshTokenByMemberId(savedMember.getMemberId()));
        assertEquals(NO_REFRESH_TOKEN.message(), e.getMessage());
    }

    @Test
    @DisplayName("로그아웃시_Redis에_액세스_토큰을_블랙리스트_처리해야_한다")
    void 로그아웃시_Redis에_액세스_토큰을_블랙리스트_처리해야_한다() {
        // given : 회원가입
        AccessToken accessToken = authService.signUp(SIGNUP_REQUEST).accessToken();
        // when : 로그아웃
        SignOutRequest signOutRequest = SignOutRequest.builder()
                .claims(accessToken.getClaims())
                .build();
        Member savedMember = getMemberByNickname(SIGNUP_REQUEST.nickname());
        authService.signOut(savedMember.getMemberId(), signOutRequest);
        // then : Redis에 액세스 토큰을 블랙리스트 처리해야 한다
        boolean isBlackListed = tokenService.existsAccessTokenByMemberId(savedMember.getMemberId());
        assertEquals(true, isBlackListed);
    }

    private Member getMemberByNickname(String nickname) {
        return memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new AuthException(AuthExceptionType.MEMBER_NOT_FOUND));
    }

    private List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
}
