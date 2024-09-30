package cjkimhello97.toy.crashMyServer.auth.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "[ HTTP ] SigninResponse : 로그인 응답 DTO")
public record SigninResponse(
        @Schema(description = "액세스 토큰", example = "aaa.bbb.ccc")
        String accessToken,
        @Schema(description = "리프레시 토큰", example = "aaa.bbb.ccc")
        String refreshToken
) {

}
