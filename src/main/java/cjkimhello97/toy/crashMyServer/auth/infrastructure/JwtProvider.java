package cjkimhello97.toy.crashMyServer.auth.infrastructure;

import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.EXPIRED_TOKEN;
import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.ILLEGAL_ARGUMENT;
import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.INVALID_SIGNATURE;
import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.INVALID_TOKEN;
import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.MALFORMED_TOKEN;

import cjkimhello97.toy.crashMyServer.auth.exception.AuthException;
import cjkimhello97.toy.crashMyServer.token.domain.AccessToken;
import cjkimhello97.toy.crashMyServer.token.domain.RefreshToken;
import cjkimhello97.toy.crashMyServer.token.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;
    private Key key;

    private final TokenService tokenService;

    @PostConstruct
    private void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public AccessToken issueAccessToken(Long memberId) {
        String claims = Jwts.builder()
                .claim("memberId", memberId)
                .issuedAt(issuedAt())
                .expiration(accessTokenExpiration())
                .signWith(key)
                .compact();
        return AccessToken.builder()
                .memberId(memberId)
                .claims(claims)
                .build();
    }

    public RefreshToken issueRefreshToken(Long memberId) {
        String claims = Jwts.builder()
                .claim("memberId", memberId)
                .issuedAt(issuedAt())
                .expiration(refreshTokenExpiration())
                .signWith(key)
                .compact();
        RefreshToken refreshToken = RefreshToken.builder()
                .memberId(memberId)
                .claims(claims)
                .build();
        return tokenService.saveRefreshToken(refreshToken);
    }

    private Date issuedAt() {
        LocalDateTime now = LocalDateTime.now();
        return Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Date accessTokenExpiration() {
        LocalDateTime now = LocalDateTime.now();
        return Date.from(now
                .plusSeconds(accessTokenExpiration)
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    private Date refreshTokenExpiration() {
        LocalDateTime now = LocalDateTime.now();
        return Date.from(now
                .plusSeconds(refreshTokenExpiration)
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public Long extractId(String claim) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret.getBytes())
                    .build()
                    .parseClaimsJws(claim)
                    .getBody();
            return claims.get("memberId", Long.class);
        } catch (ExpiredJwtException e) {
            throw new AuthException(EXPIRED_TOKEN);
        } catch (SecurityException e) {
            throw new AuthException(INVALID_SIGNATURE);
        } catch (MalformedJwtException e) {
            throw new AuthException(MALFORMED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new AuthException(INVALID_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new AuthException(ILLEGAL_ARGUMENT);
        }
    }
}
