package com.ddmtchr.dbarefactor.config;

import com.ddmtchr.dbarefactor.security.jwt.JwtAuthenticationFilter;
import com.ddmtchr.dbarefactor.security.jwt.JwtAuthorizationFilter;
import com.ddmtchr.dbarefactor.security.jwt.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:5173/");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           CorsConfigurationSource corsConfigurationSource,
                                           JwtAuthorizationFilter jwtAuthorizationFilter,
                                           JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                                auth
                                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/auth/**").permitAll()

                                        .requestMatchers("/pay/**", "/bookings/byGuest").hasAuthority("GUEST")
                                        .requestMatchers(HttpMethod.POST, "/bookings/**").hasAuthority("GUEST")
                                        .requestMatchers(HttpMethod.PUT,
                                                "/bookings/{id}/approveChanges",
                                                "/bookings/{id}/rejectChanges",
                                                "/bookings/{id}/checkIn").hasAuthority("GUEST")

                                        .requestMatchers("/bookings/byHost").hasAuthority("HOST")
                                        .requestMatchers(HttpMethod.POST, "/estate/**").hasAuthority("HOST")
                                        .requestMatchers(HttpMethod.PUT,
                                                "/bookings/{id}/approve",
                                                "/bookings/{id}/reject",
                                                "/bookings/{id}/suggestChanges").hasAuthority("HOST")

                                        .requestMatchers("/bookings/{id}").authenticated()

                                        .requestMatchers(HttpMethod.GET, "/estate/**").permitAll()

                                        .anyRequest().authenticated()
//                                .anyRequest().permitAll()
                )
                .addFilterAt(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAuthorizationFilter, JwtAuthenticationFilter.class)
        ;

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                                           JwtProvider jwtProvider,
                                                           ObjectMapper objectMapper) {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtProvider, objectMapper);
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> registrationJWTAuthentication(JwtAuthenticationFilter jwtAuthenticationFilter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(jwtAuthenticationFilter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<JwtAuthorizationFilter> registrationJWTAuthorization(JwtAuthorizationFilter jwtAuthorizationFilter) {
        FilterRegistrationBean<JwtAuthorizationFilter> registration = new FilterRegistrationBean<>(jwtAuthorizationFilter);
        registration.setEnabled(false);
        return registration;
    }

}
