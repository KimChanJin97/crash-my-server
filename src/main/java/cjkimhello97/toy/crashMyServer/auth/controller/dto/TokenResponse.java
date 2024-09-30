package cjkimhello97.toy.crashMyServer.auth.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "[ HTTP ] TokenResponse : 토큰 재발급 응답 DTO")
public record TokenResponse(
        String accessToken,
        String refreshToken
) {

}
