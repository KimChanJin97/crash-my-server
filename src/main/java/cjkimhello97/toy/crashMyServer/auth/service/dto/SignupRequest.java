package cjkimhello97.toy.crashMyServer.auth.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "[ HTTP ] SignupRequest : 회원가입 요청 DTO")
public record SignupRequest(
        @Schema(description = "aaa")
        String nickname,
        @Schema(description = "1234")
        String password
) {

}
