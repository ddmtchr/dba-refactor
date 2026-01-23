package com.ddmtchr.dbarefactor.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshTokenDto {
    private String refreshToken;
}
