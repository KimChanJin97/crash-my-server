package cjkimhello97.toy.crashMyServer.auth.controller.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {

}
