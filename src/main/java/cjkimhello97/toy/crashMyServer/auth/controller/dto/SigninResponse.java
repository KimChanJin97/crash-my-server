package cjkimhello97.toy.crashMyServer.auth.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "[ HTTP ] SigninResponse : 로그인 응답 DTO")
public record SigninResponse(
        String accessToken,
        String refreshToken
) {

}
