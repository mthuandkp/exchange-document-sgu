package com.sgu.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendOTPRequest {
//    @NonNull
    @NotNull(message = "Username không tồn tại")
    private String username;
}
