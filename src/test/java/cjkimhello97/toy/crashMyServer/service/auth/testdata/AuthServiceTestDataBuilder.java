package cjkimhello97.toy.crashMyServer.service.auth.testdata;

import cjkimhello97.toy.crashMyServer.auth.service.dto.ReissueRequest;
import cjkimhello97.toy.crashMyServer.auth.service.dto.SignupRequest;

public class AuthServiceTestDataBuilder {

    private static final String NICKNAME = "aaa";
    private static final String PASSWORD = "aaa";
    private static final String REFRESH_TOKEN = "d.e.f";

    public static SignupRequest.SignupRequestBuilder signupRequestBuilder() {
        return SignupRequest.builder()
                .nickname(NICKNAME)
                .password(PASSWORD);
    }

    public static ReissueRequest.ReissueRequestBuilder reissueRequestBuilder() {
        return ReissueRequest.builder()
                .refreshToken(REFRESH_TOKEN);
    }
}
