package cjkimhello97.toy.crashMyServer.auth.controller.dto;

import cjkimhello97.toy.crashMyServer.token.domain.RefreshToken;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(title = "[ HTTP ] ReissueRequest : 토큰 재발급 요청 DTO")
public record ReissueRequest(
        RefreshToken refreshToken
) {

}

