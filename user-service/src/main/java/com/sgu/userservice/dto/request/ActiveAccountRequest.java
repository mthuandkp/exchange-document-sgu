package com.sgu.userservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActiveAccountRequest {
    @NotNull(message = "OTP là bắt buộc")
    @Min(value = 100000,message = "OTP code must have 6 digits")
    @Max(value = 999999,message = "OTP code must have 6 digits")
    private int code;
}
