package cjkimhello97.toy.crashMyServer.auth.service.testdata;

import cjkimhello97.toy.crashMyServer.auth.controller.dto.SignUpRequest;

public class AuthServiceTestDataBuilder {

    private static final String NICKNAME = "aaa";
    private static final String PASSWORD = "aaa";

    public static SignUpRequest.SignUpRequestBuilder signupRequest() {
        return SignUpRequest.builder()
                .nickname(NICKNAME)
                .password(PASSWORD);
    }
}
