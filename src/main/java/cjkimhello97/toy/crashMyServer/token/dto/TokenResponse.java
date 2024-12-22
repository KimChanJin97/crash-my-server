package cjkimhello97.toy.crashMyServer.token.dto;

import cjkimhello97.toy.crashMyServer.token.domain.AccessToken;
import cjkimhello97.toy.crashMyServer.token.domain.RefreshToken;

public record TokenResponse(
        AccessToken accessToken,
        RefreshToken refreshToken
) {
}
