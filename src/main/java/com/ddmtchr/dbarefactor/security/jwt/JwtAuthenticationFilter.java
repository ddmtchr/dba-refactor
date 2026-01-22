package com.ddmtchr.dbarefactor.security.jwt;

import com.ddmtchr.dbarefactor.dto.auth.JwtResponseDto;
import com.ddmtchr.dbarefactor.dto.auth.LoginDto;
import com.ddmtchr.dbarefactor.security.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public static final String AUTH_URL = "/auth/login";

    private final JwtProvider jwtProvider;

    private final ObjectMapper objectMapper;


    public JwtAuthenticationFilter(JwtProvider jwtProvider, ObjectMapper objectMapper) {
        this.jwtProvider = jwtProvider;
        this.objectMapper = objectMapper;
        setFilterProcessesUrl(AUTH_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            LoginDto credentials = objectMapper.readValue(request.getInputStream(), LoginDto.class);
            String username = credentials.getUsername();
            username = (username != null) ? username.trim() : "";
            String password = credentials.getPassword();
            password = (password != null) ? password : "";
            UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username,
                    password);
            setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (IOException e) {
            throw new BadCredentialsException("Failed to obtain credentials from request", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws ServletException, IOException {
        JwtResponseDto jwtResponse = jwtProvider.generateTokenAndGetInfo(((User) authResult.getPrincipal()).getUsername());
        String jsonBody = new ObjectMapper().writeValueAsString(jwtResponse);
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonBody);
        response.flushBuffer();
        chain.doFilter(request, response);
    }

}
