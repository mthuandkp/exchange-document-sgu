package com.sgu.userservice.model;

import com.sgu.userservice.constant.Role;
import com.sgu.userservice.utils.DateUtils;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("account")
@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
public class Account{
    @Id
    private ObjectId id;
    @Field("username")
    private String username;
    @Field("password")
    private String password;
    @Field("role")
    @Builder.Default
    private Role role=Role.USER;
    @Field("avatar")
    @Builder.Default
    private String avatar="https://res.cloudinary.com/dslctuib7/image/upload/v1679203868/default/user_icon_default_rljhvt.png";
    @Field("is_block")
    @Builder.Default
    private Boolean isBlock=false;
    @Field("is_active")
    @Builder.Default
    private Boolean isActive=false;
    @Field("otp_code")
    @Builder.Default
    private String otpCode="";
    @Field("refresh_token")
    @Builder.Default
    private String refreshToken="";
    @Field("vnpay_url")
    @Builder.Default
    private String vnpayURL="";
    @Field("created_at")
    @Builder.Default
    private String createdAt= DateUtils.getNow();
    @Field("updated_at")
    @Builder.Default
    private String updatedAt=DateUtils.getNow();
    @Field("reason_for_blocking")
    @Builder.Default
    private String reasonForBlock="";
    @Field("otp_created_at")
    @Builder.Default
    private String otpCreatedAt="";
}