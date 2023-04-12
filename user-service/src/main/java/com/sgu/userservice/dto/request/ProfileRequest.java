package com.sgu.userservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ProfileRequest {
    @NotNull(message = "access token không tồn tại")
    private String accessToken;
}
