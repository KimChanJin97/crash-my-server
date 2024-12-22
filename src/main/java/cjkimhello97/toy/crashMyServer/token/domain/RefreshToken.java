package cjkimhello97.toy.crashMyServer.redis.domain;

import java.util.Objects;
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
    private Long memberId;

    @Indexed
    private String claims;

    @Builder
    public RefreshToken(Long memberId, String claims) {
        this.memberId = memberId;
        this.claims = claims;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(getClaims(), that.getClaims());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClaims());
    }
}
