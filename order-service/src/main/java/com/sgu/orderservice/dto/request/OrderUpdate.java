package com.sgu.orderservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OrderUpdate {
    @NotNull(message = "Mã đơn hàng không tồn tại")
    private String id;
}
