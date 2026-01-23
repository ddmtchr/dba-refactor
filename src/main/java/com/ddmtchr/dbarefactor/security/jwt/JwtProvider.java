package com.ddmtchr.dbarefactor.security.jwt;

import com.ddmtchr.dbarefactor.dto.auth.JwtResponseDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtProvider {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SECRET = "d45a16bcf405da59fccd8b7e263ea218880013b1741a03a131fae33eff917f2f88595507d22534e1de2fb4418d8a805f5720151ebbc661459b04669d00713da0";
    private static final Map<String, String> TOKEN_CACHE = new HashMap<>();

    @Value("${jwt.expirationTimeInMills:3600000}")
    private Long expirationTimeInMills;

    public JwtResponseDto generateTokenAndGetInfo(String login) {
        Date expiredDate = new Date(System.currentTimeMillis() + expirationTimeInMills);
        Date refreshTokenExpiredDate = Date.from(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant());

        String refreshToken = generateToken(login, refreshTokenExpiredDate);
        String accessToken = TOKEN_CACHE.computeIfAbsent(refreshToken, it -> generateToken(login, expiredDate));

        return JwtResponseDto.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build();
    }

    public boolean validateToken(String token) {
        try {
            JwtUtil.extractClaims(token);
            return true;
        } catch (Exception e) {
            if (e instanceof ExpiredJwtException) {
                try {
                    String login = ((ExpiredJwtException) e).getClaims().getSubject();
                    log.warn("JWT token expired for user '{}: {}", login, e.getMessage());
                } catch (Exception ex) {
                    log.warn(ex.getMessage());
                }
            } else {
                log.warn(e.getMessage());
            }
        }
        return false;
    }

    private String generateToken(String login, Date expiredDate) {
        Claims claims = Jwts.claims()
                .setSubject(login)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiredDate);

        return JwtUtil.buildJwt(claims);
    }
}
