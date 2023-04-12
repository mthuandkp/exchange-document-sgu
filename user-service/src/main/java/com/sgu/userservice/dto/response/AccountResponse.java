package com.sgu.userservice.dto.response;

import com.sgu.userservice.constant.Role;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class AccountResponse {
    private ObjectId id;
    private String username;
    private Role role;
    private String avatar;
    private Boolean isBlock;
    private Boolean isActive;
    private String otpCode;
    private String refreshToken;
    private String vnpayURL;
    private String createdAt;
    private String updatedAt;

    public String getId() {
        return id.toString();
    }
}
