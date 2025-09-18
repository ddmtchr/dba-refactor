package com.ddmtchr.dbarefactor.config;

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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private static final String REALM_NAME = "dbarefactor";

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint basicAuthenticationEntryPoint() {
        BasicAuthenticationEntryPoint authenticationEntryPoint = new BasicAuthenticationEntryPoint();
        authenticationEntryPoint.setRealmName(REALM_NAME);
        return authenticationEntryPoint;
    }

    @Bean
    public AccessDeniedHandler basicAccessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
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
                                           AuthenticationEntryPoint basicAuthenticationEntryPoint,
                                           AccessDeniedHandler basicAccessDeniedHandler) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(httpBasic ->
                        httpBasic
                                .authenticationEntryPoint(basicAuthenticationEntryPoint)
                                .realmName(REALM_NAME)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(basicAuthenticationEntryPoint)
                        .accessDeniedHandler(basicAccessDeniedHandler))
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
                );

        return http.build();
    }

}
