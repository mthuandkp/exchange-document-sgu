package com.sgu.orderservice.dto.response;

import com.sgu.orderservice.constant.OrderStatus;
import com.sgu.orderservice.utils.DateUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Builder
public class OrderResponse {
    private String id;
    private String username;
    private String address;
    private String phone;
    private String reasonCancel;
    private OrderStatus orderStatus;
    private String postsId;
    private String title;
    private String description;
    private Long price;
    private String thumbnail;
    private String categorySlug;
    private String createdAt;
    private String updatedAt;
}
