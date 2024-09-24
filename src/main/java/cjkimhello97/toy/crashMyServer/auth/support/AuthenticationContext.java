package cjkimhello97.toy.crashMyServer.auth.support;

import static com.cjkim.crashMyServer.auth.exception.AuthExceptionType.UNAUTHORIZED;

import com.cjkim.crashMyServer.auth.exception.AuthException;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@Component
public class AuthenticationContext {

    private Long memberId;

    public void setAuthentication(Long memerId) {
        this.memberId = memerId;
    }

    public Long getAuthentication() {
        if (Objects.isNull(this.memberId)) {
            throw new AuthException(UNAUTHORIZED);
        }
        return memberId;
    }
}
