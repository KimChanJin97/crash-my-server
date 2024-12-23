package cjkimhello97.toy.crashMyServer.auth.service;

import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.INVALID_TOKEN;
import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.NICKNAME_DUPLICATED;
import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.NICKNAME_TOO_LONG;
import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.WRONG_PASSWORD;
import static java.lang.Boolean.TRUE;

import cjkimhello97.toy.crashMyServer.auth.controller.dto.ReissueRequest;
import cjkimhello97.toy.crashMyServer.auth.controller.dto.SignInResponse;
import cjkimhello97.toy.crashMyServer.auth.controller.dto.SignOutRequest;
import cjkimhello97.toy.crashMyServer.auth.controller.dto.SignOutResponse;
import cjkimhello97.toy.crashMyServer.auth.controller.dto.SignUpRequest;
import cjkimhello97.toy.crashMyServer.auth.controller.dto.TokenResponse;
import cjkimhello97.toy.crashMyServer.auth.exception.AuthException;
import cjkimhello97.toy.crashMyServer.auth.infrastructure.JwtProvider;
import cjkimhello97.toy.crashMyServer.click.domain.Click;
import cjkimhello97.toy.crashMyServer.click.repository.ClickRepository;
import cjkimhello97.toy.crashMyServer.member.domain.Member;
import cjkimhello97.toy.crashMyServer.member.repository.MemberRepository;
import cjkimhello97.toy.crashMyServer.token.domain.AccessToken;
import cjkimhello97.toy.crashMyServer.token.domain.RefreshToken;
import cjkimhello97.toy.crashMyServer.token.service.TokenService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final ClickRepository clickRepository;
    private final TokenService tokenService;

    @Transactional
    public SignInResponse signUp(SignUpRequest signUpRequest) {
        String nickname = signUpRequest.nickname();
        String password = signUpRequest.password();

        validateNicknameLength(nickname);

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
                .count(Double.valueOf(0))
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
        AccessToken accessToken = jwtProvider.issueAccessToken(savedMember.getMemberId());
        RefreshToken refreshToken = jwtProvider.issueRefreshToken(savedMember.getMemberId());
        return new SignInResponse(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse reissueTokens(Long memberId, ReissueRequest reissueRequest) {
        RefreshToken refreshToken = reissueRequest.refreshToken();
        RefreshToken storedRefreshToken = tokenService.getRefreshTokenByMemberId(memberId);

        if (!storedRefreshToken.equals(refreshToken)) {
            throw new AuthException(INVALID_TOKEN);
        }

        AccessToken newAccessToken = jwtProvider.issueAccessToken(memberId);
        RefreshToken newRefreshToken = jwtProvider.issueRefreshToken(memberId);
        tokenService.saveRefreshToken(newRefreshToken);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public SignOutResponse signOut(Long memberId, SignOutRequest request) {
        String claims = request.claims();

        tokenService.deleteRefreshTokenByMemberId(memberId);
        AccessToken accessToken = AccessToken.builder()
                .memberId(memberId)
                .claims(claims)
                .build();
        tokenService.saveAccessToken(accessToken);

        return new SignOutResponse(TRUE);
    }

    private void validateNicknameLength(String nickname) {
        if (nickname.length() > 20) {
            throw new AuthException(NICKNAME_TOO_LONG);
        }
    }
}
