package com.sgu.userservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class PersonRequest {
    @NotBlank(message = "Tên người dùng không thể rỗng")
    @NotNull(message = "Tên người dùng không tồn tại")
    @Length(min = 4,max = 50,message = "Tên người dùng phải có ít nhất 4 ký tự")
    private String name;
    @NotBlank(message = "Địa chỉ không thể rỗng")
    @NotNull(message = "Địa chỉ không tồn tại")
    @Length(min = 20,message = "Địa chỉ phải có tối thiếu 20 ký tự")
    private String address;
    @NotNull(message = "Số điện thoại không được rỗng")
    @Pattern(regexp="(0[3|5|7|8|9])+([0-9]{8})",message = "Số điện thoại không hợp lệ")
    private String phone;
    @NotBlank(message = "birthday can't be blank")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private String birthday;
    @NotNull(message = "Giới tính phải là nam(true) hoặc nữ(false)")
    private Boolean gender;
    @NotNull(message = "Ảnh đại diện không tồn tại")
    private String avatar;
}