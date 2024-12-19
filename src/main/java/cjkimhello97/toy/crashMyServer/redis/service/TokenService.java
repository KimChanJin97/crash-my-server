package cjkimhello97.toy.crashMyServer.redis.service;

import static cjkimhello97.toy.crashMyServer.redis.exception.TokenExceptionInfo.NO_ACCESS_TOKEN;
import static cjkimhello97.toy.crashMyServer.redis.exception.TokenExceptionInfo.NO_REFRESH_TOKEN;

import cjkimhello97.toy.crashMyServer.redis.domain.AccessToken;
import cjkimhello97.toy.crashMyServer.redis.domain.RefreshToken;
import cjkimhello97.toy.crashMyServer.redis.exception.TokenException;
import cjkimhello97.toy.crashMyServer.redis.repository.AccessTokenRepository;
import cjkimhello97.toy.crashMyServer.redis.repository.RefreshTokenRepository;
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

    public RefreshToken findRefreshTokenMemberId(Long memberId) {
        return refreshTokenRepository.findById(memberId)
                .orElseThrow(() -> new TokenException(NO_REFRESH_TOKEN));
    }

    public void deleteRefreshToken(Long memberId) {
        refreshTokenRepository.deleteById(memberId);
    }

    public AccessToken saveAccessToken(AccessToken accessToken) {
        return accessTokenRepository.save(accessToken);
    }

    public boolean existsAccessTokenByMemberId(Long memberId) {
        return accessTokenRepository.existsById(memberId);
    }
}
