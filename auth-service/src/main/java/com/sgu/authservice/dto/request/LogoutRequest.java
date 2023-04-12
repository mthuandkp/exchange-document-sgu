package com.sgu.authservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LogoutRequest {
    @NotNull(message = "refresh token không tồn tại")
    private String refreshToken;
}
