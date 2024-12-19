package cjkimhello97.toy.crashMyServer.redis.dto;

import cjkimhello97.toy.crashMyServer.redis.domain.AccessToken;
import cjkimhello97.toy.crashMyServer.redis.domain.RefreshToken;

public record TokenResponse(
        AccessToken accessToken,
        RefreshToken refreshToken
) {
}
