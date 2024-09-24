package cjkimhello97.toy.crashMyServer.auth.controller.dto;

public record SigninResponse(
        String accessToken,
        String refreshToken
) {

}
