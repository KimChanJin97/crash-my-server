package cjkimhello97.toy.crashMyServer.service.auth.testdata;

import cjkimhello97.toy.crashMyServer.auth.service.dto.SignupRequest;

public class AuthServiceFixtureObject {

    private static final String NICKNAME = "aaa";
    private static final String PASSWORD = "aaa";

    public static SignupRequest signupRequest() {
        return SignupRequest.builder()
                .nickname(NICKNAME)
                .password(PASSWORD)
                .build();
    }
}
