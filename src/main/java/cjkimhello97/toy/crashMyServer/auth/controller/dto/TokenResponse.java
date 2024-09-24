package cjkimhello97.toy.crashMyServer.auth.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "[ HTTP ] TokenResponse : 토큰 재발급 응답 DTO")
public record TokenResponse(
        @Schema(defaultValue = "액세스 토큰", example = "aaa.bbb.ccc")
        String accessToken,
        @Schema(defaultValue = "리프레시 토큰", example = "aaa.bbb.ccc")
        String refreshToken
) {

}
