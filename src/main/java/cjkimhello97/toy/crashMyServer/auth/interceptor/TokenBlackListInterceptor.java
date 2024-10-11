package cjkimhello97.toy.crashMyServer.auth.interceptor;

import cjkimhello97.toy.crashMyServer.auth.service.RedisTokenService;
import cjkimhello97.toy.crashMyServer.auth.support.AuthenticationExtractor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class TokenBlackListInterceptor implements HandlerInterceptor {

    private final RedisTokenService redisTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String accessToken = AuthenticationExtractor.extractAccessToken(request).get();
        if (accessToken != null) { // 액세스 토큰이 들어있다면
            return redisTokenService.isKeyOfAccessTokenInBlackList(accessToken); // 블랙리스트에 등록되었는지 확인
        }
        return true;
    }
}
