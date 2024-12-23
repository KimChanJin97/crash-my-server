package cjkimhello97.toy.crashMyServer.auth.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(title = "[ HTTP ] SignUpResponse : 회원가입 응답 DTO")
public record SignUpResponse(
        boolean isSignUp
) {
}
