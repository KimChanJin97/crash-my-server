package cjkimhello97.toy.crashMyServer.auth.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "[ HTTP ] ReissueRequest : 토큰 재발급 요청 DTO")
public record ReissueRequest(
        @Schema(description = "리프레시 토큰", example = "ddd.eee.fff")
        String refreshToken
) {

}

