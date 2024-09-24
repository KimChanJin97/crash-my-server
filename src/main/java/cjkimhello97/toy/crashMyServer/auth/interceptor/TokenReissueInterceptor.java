package cjkimhello97.toy.crashMyServer.auth.interceptor;

import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.UNAUTHORIZED;

import cjkimhello97.toy.crashMyServer.auth.exception.AuthException;
import cjkimhello97.toy.crashMyServer.auth.infrastructure.JwtProvider;
import cjkimhello97.toy.crashMyServer.auth.support.AuthenticationContext;
import cjkimhello97.toy.crashMyServer.auth.support.AuthenticationExtractor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class TokenReissueInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;
    private final AuthenticationContext authenticationContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String accessToken = AuthenticationExtractor.extractAccessToken(request)
                .orElseThrow(() -> new AuthException(UNAUTHORIZED));

        Long memberId = jwtProvider.extractIdWithoutExpiration(accessToken);
        authenticationContext.setAuthentication(memberId);

        return true;
    }
}