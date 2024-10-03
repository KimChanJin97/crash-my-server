package cjkimhello97.toy.crashMyServer.auth.service;

import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.*;

import cjkimhello97.toy.crashMyServer.auth.controller.dto.SigninResponse;
import cjkimhello97.toy.crashMyServer.auth.controller.dto.TokenResponse;
import cjkimhello97.toy.crashMyServer.auth.exception.AuthException;
import cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType;
import cjkimhello97.toy.crashMyServer.auth.infrastructure.JwtProvider;
import cjkimhello97.toy.crashMyServer.auth.service.dto.ReissueRequest;
import cjkimhello97.toy.crashMyServer.auth.service.dto.SignupRequest;
import cjkimhello97.toy.crashMyServer.click.domain.Click;
import cjkimhello97.toy.crashMyServer.click.repository.ClickRepository;
import cjkimhello97.toy.crashMyServer.member.domain.Member;
import cjkimhello97.toy.crashMyServer.member.repository.MemberRepository;
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
    private final RedisTokenService redisTokenService;

    @Transactional
    public SigninResponse signUp(SignupRequest signUpRequest) {
        String nickname = signUpRequest.nickname();
        String password = signUpRequest.password();

        validateNickname(nickname);

        // 닉네임 존재 O = 로그인 로직
        if (memberRepository.findByNickname(nickname).isPresent()) {
            return signIn(nickname, password);
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
        return signIn(nickname, password);
    }

    private SigninResponse signIn(String nickname, String password) {
        Member member = memberRepository.findByNickname(nickname).get();

        String savedNickname = member.getNickname();
        String savedPassword = member.getPassword();
        // 닉네임 존재 O && 비밀번호 존재 X = 예외
        if (savedNickname.equals(savedNickname) && !passwordEncoder.matches(password, savedPassword)) {
            throw new AuthException(WRONG_PASSWORD);
        }
        // 닉네임 존재 O && 비밀번호 존재 O = 로그인
        String accessToken = jwtProvider.createAccessToken(member.getMemberId());
        String refreshToken = jwtProvider.createRefreshToken(member.getMemberId());
        return new SigninResponse(accessToken, refreshToken);
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

    private void validateNickname(String nickname) {
        if (nickname.length() > 10) {
            throw new AuthException(NICKNAME_EXCEED_LENGTH_TEN);
        }
    }
}
