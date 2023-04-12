package com.sgu.orderservice.model;

import com.sgu.orderservice.constant.OrderStatus;
import com.sgu.orderservice.utils.DateUtils;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("order")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {
    @Id
    @Field("id")
    private ObjectId id;
    @Field("username")
    private String username;
    @Field("address")
    private String address;
    @Field("phone")
    private String phone;
    @Field("reason_cancel")
    @Builder.Default
    private String reasonCancel = "";
    @Field("oder_status")
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.WAIT_CONFIRM;
    @Field("created_at")
    @Builder.Default
    private String createdAt = DateUtils.getNow();
    @Field("updated_at")
    @Builder.Default
    private String updatedAt = DateUtils.getNow();
}
