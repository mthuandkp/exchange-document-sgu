package com.sgu.postsservice.dto.response;

import com.sgu.postsservice.constant.Role;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String id;
    private String username;
    private String avatar;
    private String name;
    private String address;
    private String phone;
    private String birthday;
    private Boolean gender;


}
