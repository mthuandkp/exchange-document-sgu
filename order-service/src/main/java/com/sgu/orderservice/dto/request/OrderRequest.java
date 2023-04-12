package com.sgu.orderservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;


@Getter
public class OrderRequest {
    @NotNull(message = "postsId không tồn tại")
    private String postsId;
    @NotNull(message = "Địa chỉ dùng không tồn tại")
    @Length(min = 20,message = "Địa chỉ tối thiểu phải có 20 ký tự")
    private String address;
    @NotNull(message = "Số điện thoại không tồn tại")
    @Pattern(regexp="(0[3|5|7|8|9])+([0-9]{8})")
    private String phone;
}
