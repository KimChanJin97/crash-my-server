package cjkimhello97.toy.crashMyServer.redis.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor
@RedisHash(value = "refresh", timeToLive = 604_800)
public class RefreshToken {

    @Id
    private String memberId;

    @Indexed
    private String claims;

    @Builder
    public RefreshToken(String memberId, String claims) {
        this.memberId = memberId;
        this.claims = claims;
    }
}
