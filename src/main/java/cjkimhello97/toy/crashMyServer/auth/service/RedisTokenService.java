package cjkimhello97.toy.crashMyServer.auth.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

    @Value("${jwt.refreshExpiration}")
    private Long refreshExpiration;

    private final StringRedisTemplate stringRedisTemplate;

    public void setRefreshToken(Long memberId, String refreshToken) {
        stringRedisTemplate.opsForValue()
                .set(String.valueOf(memberId), refreshToken, refreshExpiration, TimeUnit.MINUTES);
    }

    public String getRefreshToken(String memberId) {
        return stringRedisTemplate.opsForValue().get(memberId);
    }

    public void deleteRefreshToken(String memberId) {
        stringRedisTemplate.delete(memberId);
    }
}
