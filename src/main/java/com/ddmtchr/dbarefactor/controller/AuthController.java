package com.ddmtchr.dbarefactor.controller;

import com.ddmtchr.dbarefactor.dto.auth.JwtResponseDto;
import com.ddmtchr.dbarefactor.dto.auth.LoginDto;
import com.ddmtchr.dbarefactor.dto.request.RegisterRequest;
import com.ddmtchr.dbarefactor.exception.UsernameAlreadyExistsException;
import com.ddmtchr.dbarefactor.security.entity.User;
import com.ddmtchr.dbarefactor.security.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Аутентификация и авторизация")
public class AuthController {

    private final UserService userService;

    @Operation(
            summary = "Войти",
            description = "Получает JWT-токен по имени пользователя и паролю",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Вход успешный",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = JwtResponseDto.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверный формат запроса",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Неверный логин или пароль",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PostMapping("/login")
    public ResponseEntity<Void> login() {
        throw new IllegalStateException("This endpoint is handled by Spring Security");
    }

    @Operation(
            summary = "Зарегистрироваться",
            description = "Создает нового пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Регистрация успешна",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = User.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверный формат запроса или пользователь уже существует",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody @Valid RegisterRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        User user = userService.addUser(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}
