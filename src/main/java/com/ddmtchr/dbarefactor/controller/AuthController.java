package com.ddmtchr.dbarefactor.controller;

import com.ddmtchr.dbarefactor.dto.request.RegisterRequest;
import com.ddmtchr.dbarefactor.exception.UsernameAlreadyExistsException;
import com.ddmtchr.dbarefactor.security.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Void> login() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid RegisterRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        userService.addUser(request);
        return new ResponseEntity<>("Registered successfully", HttpStatus.CREATED);
    }
}
