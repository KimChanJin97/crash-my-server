package cjkimhello97.toy.crashMyServer.auth.controller;

import cjkimhello97.toy.crashMyServer.auth.controller.dto.SignInResponse;
import cjkimhello97.toy.crashMyServer.auth.controller.dto.SignOutResponse;
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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원가입 / 로그인 / 토큰 재발급")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    @Operation(
            summary = "[ HTTP ] 회원가입/로그인 API",
            description = """
                    회원가입/로그인 로직
                    - 로직 1. 닉네임 존재 X = 회원가입 후 로그인 처리
                    - 로직 2. 닉네임 존재 O && 비밀번호 존재 O = 로그인 처리
                    - 로직 3. 닉네임 존재 O && 비밀번호 존재 X = 예외
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            - 설명 : 발급받은 액세스 토큰은 인증 헤더에, 리프레시 토큰은 로컬 스토리지에 저장해야 합니다. 
                                    액세스 토큰은 이후 요청부터 Bearer 방식(Bearer aaa.bbb.ccc)으로 담겨져 사용되어야 합니다. 
                                    리프레시 토큰은 토큰 재발급시 로컬 스토리지로부터 꺼내어 사용되어야 합니다. 
                            - 응답 형식(로직 1,2) : { "accessToken": "aaa.bbb.ccc", "refreshToken": "ddd.eee.fff" }
                             """,
                    content = {@Content(schema = @Schema(implementation = SignInResponse.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            - 설명 : 닉네임은 존재하지만 비밀번호가 존재하지 않을 때 반환될 예외 DTO 입니다.
                            - 예외 형식(로직 3) : { "exceptionCode": 4007, "message": "WRONG PASSWORD" }
                            """,
                    content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    public ResponseEntity<SignInResponse> signUp(
            @RequestBody SignupRequest signUpRequest
    ) {
        return ResponseEntity.ok(authService.signUp(signUpRequest));
    }

    @PostMapping("/reissue")
    @Operation(
            summary = "[ HTTP ] 토큰 재발급 API",
            description = """
                    토큰 재발급 로직
                    - 로컬 스토리지에 저장해두었던 리프레시 토큰를 JSON 형식으로 요청 바디에 실어 요청해야 합니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            - 설명 : 토큰 재발급 요청이 정상적으로 이뤄져 반환될 토큰 응답 DTO 입니다. 
                            - 응답 형식 : 200 { "accessToken": "aaa.bbb.ccc", "refreshToken": "ddd.eee.fff" }
                            """,
                    content = {@Content(schema = @Schema(implementation = TokenResponse.class))}),
            @ApiResponse(
                    responseCode = "401, 404",
                    description = """
                            - 설명 : 토큰이 위변조될 경우 반환될 예외 DTO 입니다.
                            - 예외 형식 1 : { "exceptionCode": 4001, "message":"FAIL TO AUTHORIZATION" }
                            - 예외 형식 2 : { "exceptionCode": 4002, "message":"TOKEN EXPIRED" }
                            - 예외 형식 3 : { "exceptionCode": 4003, "message":"INVALID SIGNATURE" }
                            - 예외 형식 4 : { "exceptionCode": 4004, "message":"FORGED TOKEN" }
                            - 예외 형식 5 : { "exceptionCode": 4005, "message":"INVALID TOKEN" }
                            - 예외 형식 6 : { "exceptionCode": 4006, "message":"MEMBER NOT FOUND" }
                            """,
                    content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    public ResponseEntity<TokenResponse> reissueTokens(
            @AuthMember Long memberId,
            @RequestBody ReissueRequest reissueRequest
    ) {
        return ResponseEntity.ok(authService.reissueTokens(memberId, reissueRequest));
    }

    @PostMapping("/sign-out")
    @Operation(
            summary = "[ HTTP ] 로그아웃 API",
            description = """
                    로그아웃 로직
                    - 로그아웃을 요청하면 인증 헤더에 담겨있던 액세스 토큰을 블랙리스트에 등록합니다. 블랙리스트 처리된 토큰은 다시 사용할 수 없습니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            - 설명 : 로그아웃 요청이 정상적으로 이뤄져 반환될 응답 DTO 입니다. 
                            - 응답 형식 : { "isSignOut": true }
                             """,
                    content = {@Content(schema = @Schema(implementation = SignInResponse.class))}),
            @ApiResponse(
                    responseCode = "401, 404",
                    description = """
                            - 설명 : 토큰이 위변조될 경우 반환될 예외 DTO 입니다.
                            - 예외 형식 1 : { "exceptionCode": 4001, "message":"FAIL TO AUTHORIZATION" }
                            - 예외 형식 2 : { "exceptionCode": 4002, "message":"TOKEN EXPIRED" }
                            - 예외 형식 3 : { "exceptionCode": 4003, "message":"INVALID SIGNATURE" }
                            - 예외 형식 4 : { "exceptionCode": 4004, "message":"FORGED TOKEN" }
                            - 예외 형식 5 : { "exceptionCode": 4005, "message":"INVALID TOKEN" }
                            - 예외 형식 6 : { "exceptionCode": 4006, "message":"MEMBER NOT FOUND" }
                            """,
                    content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    public ResponseEntity<SignOutResponse> signOut(
            HttpServletRequest request,
            @AuthMember Long memberId
    ) {
        return ResponseEntity.ok(authService.signOut(request, memberId));
    }
}
