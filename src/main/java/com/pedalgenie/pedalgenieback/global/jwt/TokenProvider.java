package com.pedalgenie.pedalgenieback.global.jwt;

import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import com.pedalgenie.pedalgenieback.global.jwt.refresh.RefreshToken;
import com.pedalgenie.pedalgenieback.global.jwt.refresh.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class TokenProvider {
    private final Key key;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public TokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


    // 엑세스 토큰 생성 메서드
    public String createAccessToken(Long memberId, String role) {
        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + 30000); // 테스트용 30초
        Date expiryDate = new Date(now.getTime() + 3600000); // 1시간

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    // 리프레시 토큰 생성 메서드
    public String createRefreshToken(Long memberId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 30L * 24 * 60 * 60 * 1000); // 30일

        String refreshToken = Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // redis에 저장
        String key = "refreshToken:" + memberId;
        redisTemplate.opsForValue().set(key, refreshToken);
        redisTemplate.expire(key, 30L * 24 * 60 * 60, TimeUnit.SECONDS); // 30일

        return refreshToken;
    }


    // 토큰 검증 메서드
    public void validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED); // 토큰 만료
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e)  { // 타입, 형식, 서명 오류
            throw new CustomException(ErrorCode.NOT_VALID_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.NOT_EXISTS_AUTHORIZATION); // 빈 값
        }
    }

    // 토큰에서 역할 추출 메서드
    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("role", String.class);
    }

    // 토큰에서 memberId 추출 메서드
    public Long getMemberIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.valueOf(claims.getSubject());
    }

}
