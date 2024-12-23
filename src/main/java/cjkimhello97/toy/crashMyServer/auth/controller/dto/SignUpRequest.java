package cjkimhello97.toy.crashMyServer.auth.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(title = "[ HTTP ] SignUpRequest : 회원가입 요청 DTO")
public record SignUpRequest(
        String nickname,
        String password
) {

}
