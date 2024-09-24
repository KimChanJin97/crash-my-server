package cjkimhello97.toy.crashMyServer.auth.infrastructure;

import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.EXPIRED_TOKEN;
import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.INVALID_SIGNATURE;
import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.INVALID_TOKEN;
import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.MALFORMED_TOKEN;

import cjkimhello97.toy.crashMyServer.auth.exception.AuthException;
import cjkimhello97.toy.crashMyServer.auth.service.RedisTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.accessExpiration}")
    private int accessExpiration;
    @Value("${jwt.refreshExpiration}")
    private int refreshExpiration;
    private Key key;

    private final RedisTokenService redisTokenService;

    @PostConstruct
    private void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String createAccessToken(Long id) {
        return createToken(id, accessExpiration);
    }

    public String createRefreshToken(Long id) {
        String refreshToken = createToken(id, refreshExpiration);
        redisTokenService.setRefreshToken(id, refreshToken);
        return refreshToken;
    }

    private String createToken(Long id, int expiration) {
        Claims claims = Jwts.claims()
                .id(id.toString())
                .issuedAt(issuedAt())
                .expiration(expiredAt(expiration))
                .build();
        return Jwts.builder()
                .claims(claims)
                .signWith(key)
                .compact();
    }

    private Date issuedAt() {
        LocalDateTime now = LocalDateTime.now();
        return Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Date expiredAt(int expiration) {
        LocalDateTime now = LocalDateTime.now();
        return Date.from(now.plusHours(expiration).atZone(ZoneId.systemDefault()).toInstant());
    }

    public Long extractId(String token) {
        try {
            Claims claims = createClaims(token);
            return Long.parseLong(claims.getId());
        } catch (ExpiredJwtException e) {
            throw new AuthException(EXPIRED_TOKEN);
        } catch (SecurityException e) {
            throw new AuthException(INVALID_SIGNATURE);
        } catch (MalformedJwtException e) {
            throw new AuthException(MALFORMED_TOKEN);
        } catch (UnsupportedJwtException | IllegalArgumentException e) {
            throw new AuthException(INVALID_TOKEN);
        }
    }

    public Long extractIdWithoutExpiration(String token) {
        try {
            Claims claims = createClaims(token);
            return Long.parseLong(claims.getId());
        } catch (ExpiredJwtException e) {
            Claims expiredClaims = e.getClaims();
            return Long.parseLong(expiredClaims.getId());
        } catch (SecurityException e) {
            throw new AuthException(INVALID_SIGNATURE);
        } catch (MalformedJwtException e) {
            throw new AuthException(MALFORMED_TOKEN);
        } catch (UnsupportedJwtException | IllegalArgumentException e) {
            throw new AuthException(INVALID_TOKEN);
        }
    }

    private Claims createClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
