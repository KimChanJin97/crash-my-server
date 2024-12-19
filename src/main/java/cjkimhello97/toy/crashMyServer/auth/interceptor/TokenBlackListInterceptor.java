package cjkimhello97.toy.crashMyServer.auth.interceptor;

import static cjkimhello97.toy.crashMyServer.redis.exception.TokenExceptionInfo.BLACKLISTED_ACCESS_TOKEN;

import cjkimhello97.toy.crashMyServer.auth.infrastructure.JwtProvider;
import cjkimhello97.toy.crashMyServer.auth.support.AuthenticationExtractor;
import cjkimhello97.toy.crashMyServer.redis.exception.TokenException;
import cjkimhello97.toy.crashMyServer.redis.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class TokenBlackListInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;
    private final TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String claims = AuthenticationExtractor.extractAccessToken(request).get();
        Long memberId = jwtProvider.extractId(claims);
        boolean isBlackListed = tokenService.existsAccessTokenByMemberId(memberId);
        if (claims != null && isBlackListed) {
            throw new TokenException(BLACKLISTED_ACCESS_TOKEN);
        }
        return true;
    }
}
