package cjkimhello97.toy.crashMyServer.auth.config;

import static cjkimhello97.toy.crashMyServer.auth.interceptor.HttpMethod.GET;
import static cjkimhello97.toy.crashMyServer.auth.interceptor.HttpMethod.OPTIONS;
import static cjkimhello97.toy.crashMyServer.auth.interceptor.HttpMethod.POST;

import cjkimhello97.toy.crashMyServer.auth.interceptor.LoginInterceptor;
import cjkimhello97.toy.crashMyServer.auth.interceptor.PathMatchInterceptor;
import cjkimhello97.toy.crashMyServer.auth.interceptor.TokenBlackListInterceptor;
import cjkimhello97.toy.crashMyServer.auth.interceptor.TokenReissueInterceptor;
import cjkimhello97.toy.crashMyServer.auth.support.AuthArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class AuthConfig implements WebMvcConfigurer {

    private final AuthArgumentResolver authArgumentResolver;
    private final LoginInterceptor loginInterceptor;
    private final TokenReissueInterceptor tokenReissueInterceptor;
    private final TokenBlackListInterceptor tokenBlackListInterceptor;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor());
        registry.addInterceptor(tokenReissueInterceptor());
        registry.addInterceptor(tokenBlackListInterceptor());
    }

    private HandlerInterceptor loginInterceptor() {
        return new PathMatchInterceptor(loginInterceptor)
                .excludePathPattern("/**", OPTIONS)
                .includePathPattern("/api/v1/**", GET, POST)
                .excludePathPattern("/api/v1/auth/sign-up", POST)
                ;
    }

    private HandlerInterceptor tokenReissueInterceptor() {
        return new PathMatchInterceptor(tokenReissueInterceptor)
                .excludePathPattern("/**", OPTIONS)
                .includePathPattern("/api/v1/auth/reissue", POST)
                ;
    }

    private HandlerInterceptor tokenBlackListInterceptor() {
        return new PathMatchInterceptor(tokenBlackListInterceptor)
                .excludePathPattern("/**", OPTIONS)
                .includePathPattern("/api/v1/**", GET, POST)
                .excludePathPattern("/api/v1/auth/sign-up", POST)
                ;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authArgumentResolver);
    }
}
