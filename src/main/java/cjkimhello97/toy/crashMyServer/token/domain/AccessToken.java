package cjkimhello97.toy.crashMyServer.token.domain;

import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor
@RedisHash(value = "access", timeToLive = 259200)
public class AccessToken {

    @Id
    private Long memberId;

    @Indexed
    private String claims;

    @Builder
    public AccessToken(Long memberId, String claims) {
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
        AccessToken that = (AccessToken) o;
        return Objects.equals(getClaims(), that.getClaims());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClaims());
    }
}
