package cjkimhello97.toy.crashMyServer.token.service;

import static cjkimhello97.toy.crashMyServer.token.exception.TokenExceptionInfo.NO_REFRESH_TOKEN;

import cjkimhello97.toy.crashMyServer.token.domain.AccessToken;
import cjkimhello97.toy.crashMyServer.token.domain.RefreshToken;
import cjkimhello97.toy.crashMyServer.token.exception.TokenException;
import cjkimhello97.toy.crashMyServer.token.repository.AccessTokenRepository;
import cjkimhello97.toy.crashMyServer.token.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenRepository accessTokenRepository;

    public RefreshToken saveRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken getRefreshTokenByMemberId(Long memberId) {
        return refreshTokenRepository.findById(memberId)
                .orElseThrow(() -> new TokenException(NO_REFRESH_TOKEN));
    }

    public void deleteRefreshTokenByMemberId(Long memberId) {
        refreshTokenRepository.deleteById(memberId);
    }

    public void deleteAllRefreshTokens() {
        refreshTokenRepository.deleteAll();
    }

    public AccessToken saveAccessToken(AccessToken accessToken) {
        return accessTokenRepository.save(accessToken);
    }

    public boolean existsAccessTokenByMemberId(Long memberId) {
        return accessTokenRepository.existsById(memberId);
    }
}
