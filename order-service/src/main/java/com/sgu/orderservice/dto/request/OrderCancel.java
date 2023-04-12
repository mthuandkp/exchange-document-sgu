package com.sgu.orderservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class OrderCancel {
    @NotNull(message = "Lý do huỷ là bắt buộc")
    @Length(min = 5,message = "Lý do huỷ tối thiểu 5 ký tự")
    private String reasonCancel;
}
