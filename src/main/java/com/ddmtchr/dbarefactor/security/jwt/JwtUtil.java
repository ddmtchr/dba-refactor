package com.ddmtchr.dbarefactor.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.util.Optional;

import static com.ddmtchr.dbarefactor.security.jwt.JwtProvider.*;


public class JwtUtil {

    public static Optional<String> getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(HEADER_STRING);
        return StringUtils.hasText(bearer) && bearer.startsWith(TOKEN_PREFIX) ?
                Optional.of(bearer.substring(7)) : Optional.empty();
    }

    public static String getLoginFromToken(String token) {
        Claims claims = extractClaims(token);
        return claims.getSubject();
    }

    public static Claims extractClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET))).build().parseClaimsJws(token).getBody();
    }

    public static String buildJwt(Claims claims) {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)), SignatureAlgorithm.HS512)
                .compact();
    }
}
