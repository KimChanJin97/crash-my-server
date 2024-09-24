package cjkimhello97.toy.crashMyServer.auth.controller;

import cjkimhello97.toy.crashMyServer.auth.controller.dto.SigninResponse;
import cjkimhello97.toy.crashMyServer.auth.controller.dto.TokenResponse;
import cjkimhello97.toy.crashMyServer.auth.service.AuthService;
import cjkimhello97.toy.crashMyServer.auth.service.dto.ReissueRequest;
import cjkimhello97.toy.crashMyServer.auth.service.dto.SignupRequest;
import cjkimhello97.toy.crashMyServer.auth.support.AuthMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<SigninResponse> signUp(
            @RequestBody SignupRequest signUpRequest
    ) {
        return ResponseEntity.ok(authService.signUp(signUpRequest));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissueTokens(
            @RequestBody ReissueRequest reissueRequest,
            @AuthMember Long memberId
    ) {
        String refreshToken = reissueRequest.refreshToken();
        return ResponseEntity.ok(authService.reissueTokens(memberId, refreshToken));
    }
}
