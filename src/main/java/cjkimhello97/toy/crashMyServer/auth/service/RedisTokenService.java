package cjkimhello97.toy.crashMyServer.auth.service;

import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.ALREADY_SIGN_OUT;
import static java.util.concurrent.TimeUnit.*;

import cjkimhello97.toy.crashMyServer.auth.exception.AuthException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

    @Value("${jwt.refreshExpiration}")
    private Long refreshExpiration;

    @Value("${jwt.sign-out-value}")
    private String signOutValue;

    private final StringRedisTemplate stringRedisTemplate;

    public void setRefreshToken(Long memberId, String refreshToken) {
        stringRedisTemplate.opsForValue()
                .set(String.valueOf(memberId), refreshToken, refreshExpiration, MINUTES);
    }

    public String getRefreshToken(String memberId) {
        return stringRedisTemplate.opsForValue().get(memberId);
    }

    public void setAccessTokenSignOut(String accessToken, Long minute) {
        stringRedisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        stringRedisTemplate.opsForValue().set(accessToken, signOutValue, minute, MINUTES);
    }

    public boolean isKeyOfAccessTokenInBlackList(String accessToken) {
        String storedSignOutValue = stringRedisTemplate.opsForValue().get(accessToken);
        if (storedSignOutValue != null && storedSignOutValue.equals(signOutValue)) {
            throw new AuthException(ALREADY_SIGN_OUT);
        }
        return true;
    }

    public void deleteRefreshToken(String memberId) {
        stringRedisTemplate.delete(memberId);
    }
}
