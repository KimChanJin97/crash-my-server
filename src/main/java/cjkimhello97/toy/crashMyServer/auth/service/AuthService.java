package cjkimhello97.toy.crashMyServer.auth.service;

import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.INVALID_TOKEN;
import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.NICKNAME_TOO_LONG;
import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.UNAUTHORIZED;
import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.WRONG_PASSWORD;
import static java.lang.Boolean.TRUE;

import cjkimhello97.toy.crashMyServer.auth.controller.dto.SignInResponse;
import cjkimhello97.toy.crashMyServer.auth.controller.dto.SignOutResponse;
import cjkimhello97.toy.crashMyServer.auth.controller.dto.TokenResponse;
import cjkimhello97.toy.crashMyServer.auth.exception.AuthException;
import cjkimhello97.toy.crashMyServer.auth.infrastructure.JwtProvider;
import cjkimhello97.toy.crashMyServer.auth.service.dto.ReissueRequest;
import cjkimhello97.toy.crashMyServer.auth.service.dto.SignupRequest;
import cjkimhello97.toy.crashMyServer.auth.support.AuthenticationExtractor;
import cjkimhello97.toy.crashMyServer.click.domain.Click;
import cjkimhello97.toy.crashMyServer.click.repository.ClickRepository;
import cjkimhello97.toy.crashMyServer.member.domain.Member;
import cjkimhello97.toy.crashMyServer.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${jwt.sign-out-time}")
    private Long signOutTime;

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final ClickRepository clickRepository;
    private final RedisTokenService redisTokenService;

    @Transactional
    public SignInResponse signUp(SignupRequest signUpRequest) {
        String nickname = signUpRequest.nickname();
        String password = signUpRequest.password();

        validateNickname(nickname);

        // 닉네임 존재 O = 로그인 로직
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        if (optionalMember.isPresent()) {
            return signIn(optionalMember.get(), password);
        }

        // 닉네임 존재 X = 회원가입 후 로그인 로직
        Member member = Member.builder()
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .build();
        memberRepository.save(member);

        Click click = Click.builder()
                .member(member)
                .count(Double.valueOf(1))
                .build();
        clickRepository.save(click);
        return signIn(member, password);
    }

    public SignInResponse signIn(Member savedMember, String password) {
        // 닉네임 존재 O && 비밀번호 존재 X = 예외
        if (!passwordEncoder.matches(password, savedMember.getPassword())) {
            throw new AuthException(WRONG_PASSWORD);
        }
        // 닉네임 존재 O && 비밀번호 존재 O = 로그인
        String accessToken = jwtProvider.createAccessToken(savedMember.getMemberId());
        String refreshToken = jwtProvider.createRefreshToken(savedMember.getMemberId());
        return new SignInResponse(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse reissueTokens(Long memberId, ReissueRequest reissueRequest) {
        String refreshToken = reissueRequest.refreshToken();
        String storedRefreshToken = redisTokenService.getRefreshToken(String.valueOf(memberId));

        if (!storedRefreshToken.equals(refreshToken)) {
            throw new AuthException(INVALID_TOKEN);
        }

        String newAccessToken = jwtProvider.createAccessToken(memberId);
        String newRefreshToken = jwtProvider.createRefreshToken(memberId);
        redisTokenService.setRefreshToken(memberId, newRefreshToken);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public SignOutResponse signOut(HttpServletRequest request, Long memberId) {
        String accessToken = AuthenticationExtractor.extractAccessToken(request)
                .orElseThrow(() -> new AuthException(UNAUTHORIZED));

        redisTokenService.deleteRefreshToken(String.valueOf(memberId));
        redisTokenService.setAccessTokenSignOut(accessToken, signOutTime);

        return new SignOutResponse(TRUE);
    }

    private void validateNickname(String nickname) {
        if (nickname.length() > 10) {
            throw new AuthException(NICKNAME_TOO_LONG);
        }
    }
}
