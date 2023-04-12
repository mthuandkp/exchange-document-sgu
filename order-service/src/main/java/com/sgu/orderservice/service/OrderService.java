package com.sgu.orderservice.service;

import com.sgu.orderservice.constant.OrderStatus;
import com.sgu.orderservice.dto.request.OrderCancel;
import com.sgu.orderservice.dto.request.OrderRequest;
import com.sgu.orderservice.dto.response.HttpResponseEntity;
import jakarta.validation.Valid;

public interface OrderService {
    public HttpResponseEntity create(String token, OrderRequest orderRequest);

    public HttpResponseEntity getAll(Integer page, Integer size,OrderStatus status, String startDay, String endDay, String userId);

    public HttpResponseEntity confim(String token, String orderUpdate);

    public HttpResponseEntity sellerCancel(String token, @Valid OrderCancel orderUpdate, String orderId);

    public HttpResponseEntity buyerCancel(String token, OrderCancel orderCancel, String orderId);
}
