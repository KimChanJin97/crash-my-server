package cjkimhello97.toy.crashMyServer.auth.controller.dto;

import cjkimhello97.toy.crashMyServer.token.domain.AccessToken;
import cjkimhello97.toy.crashMyServer.token.domain.RefreshToken;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "[ HTTP ] SignInResponse : 로그인 응답 DTO")
public record SignInResponse(
        AccessToken accessToken,
        RefreshToken refreshToken
) {

}
