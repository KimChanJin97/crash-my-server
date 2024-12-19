package cjkimhello97.toy.crashMyServer.auth.controller.dto;

import cjkimhello97.toy.crashMyServer.redis.domain.AccessToken;
import cjkimhello97.toy.crashMyServer.redis.domain.RefreshToken;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "[ HTTP ] TokenResponse : 토큰 재발급 응답 DTO")
public record TokenResponse(
        AccessToken accessToken,
        RefreshToken refreshToken
) {

}
