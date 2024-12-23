package cjkimhello97.toy.crashMyServer.auth.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(title = "[ HTTP ] SignOutRequest : 로그아웃 요청 DTO")
public record SignOutRequest(
        String claims
) {
}
