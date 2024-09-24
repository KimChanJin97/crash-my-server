package cjkimhello97.toy.crashMyServer.auth.controller;

import cjkimhello97.toy.crashMyServer.auth.controller.dto.SigninResponse;
import cjkimhello97.toy.crashMyServer.auth.controller.dto.TokenResponse;
import cjkimhello97.toy.crashMyServer.auth.service.AuthService;
import cjkimhello97.toy.crashMyServer.auth.service.dto.ReissueRequest;
import cjkimhello97.toy.crashMyServer.auth.service.dto.SignupRequest;
import cjkimhello97.toy.crashMyServer.auth.support.AuthMember;
import cjkimhello97.toy.crashMyServer.common.exception.dto.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "1. 회원가입 / 로그인 / 토큰 재발급")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    @Operation(summary = "[ HTTP ] 회원가입/로그인 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = {@Content(schema = @Schema(implementation = SigninResponse.class))}),
            @ApiResponse(
                    responseCode = "400, 401, 404",
                    description = "비밀번호 예외(400), JWT 검증/파싱 예외(401, 404)",
                    content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    public ResponseEntity<SigninResponse> signUp(
            @RequestBody SignupRequest signUpRequest
    ) {
        return ResponseEntity.ok(authService.signUp(signUpRequest));
    }

    @PostMapping("/reissue")
    @Operation(summary = "[ HTTP ] 토큰 재발급 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = {@Content(schema = @Schema(implementation = TokenResponse.class))}),
            @ApiResponse(
                    responseCode = "401, 404",
                    description = "JWT 검증/파싱 예외(401, 404)",
                    content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    public ResponseEntity<TokenResponse> reissueTokens(
            @RequestBody ReissueRequest reissueRequest,
            @AuthMember Long memberId
    ) {
        String refreshToken = reissueRequest.refreshToken();
        return ResponseEntity.ok(authService.reissueTokens(memberId, refreshToken));
    }
}
