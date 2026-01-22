package com.ddmtchr.dbarefactor.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginDto {
    private String username;
    private String password;
}
