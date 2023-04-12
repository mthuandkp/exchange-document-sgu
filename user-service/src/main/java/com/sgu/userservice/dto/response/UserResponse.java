package com.sgu.userservice.dto.response;

import com.sgu.userservice.constant.Role;
import com.sgu.userservice.model.Account;
import com.sgu.userservice.model.Person;
import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String id;
    private String username;
    private Role role;
    private String avatar;
    private Boolean isBlock;
    private Boolean isActive;
    private String vnpayURL;
    private String name;
    private String address;
    private String phone;
    private String birthday;
    private Boolean gender;

    public String getId() {
        return id.toString();
    }

}
