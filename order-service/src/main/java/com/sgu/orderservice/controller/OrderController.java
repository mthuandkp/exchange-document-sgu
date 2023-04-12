package com.sgu.orderservice.controller;

import com.sgu.orderservice.constant.OrderStatus;
import com.sgu.orderservice.dto.request.OrderCancel;
import com.sgu.orderservice.dto.request.OrderRequest;
import com.sgu.orderservice.dto.request.OrderUpdate;
import com.sgu.orderservice.dto.response.HttpResponseEntity;
import com.sgu.orderservice.exception.BadRequestException;
import com.sgu.orderservice.service.OrderService;
import com.sgu.orderservice.utils.DateUtils;
import jakarta.validation.Valid;
import jakarta.ws.rs.HeaderParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @PostMapping("/create")
    public ResponseEntity<HttpResponseEntity> create(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid OrderRequest orderRequest
    ){
        HttpResponseEntity httpResponseEntity = orderService.create(token,orderRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(httpResponseEntity);
    }

    @GetMapping("")
    public ResponseEntity<HttpResponseEntity> getAll(
            @RequestParam(name = "page") Optional<Integer> pageRequest,
            @RequestParam(name = "size") Optional<Integer> sizeRequest,
            @RequestParam(name = "status") Optional<Integer> statusRequest,
            @RequestParam(name = "start_day") Optional<String> startDayRequest,
            @RequestParam(name = "end_day") Optional<String> endDayRequest,
            @RequestParam(name = "username") Optional<String> userIdRequest

    ){
        Integer page = pageRequest.isEmpty() ? null : pageRequest.get();
        Integer size = sizeRequest.isEmpty() ? null : sizeRequest.get();
        OrderStatus status = null;
        String startDay = startDayRequest.isEmpty() ? null : startDayRequest.get();
        String endDay = endDayRequest.isEmpty() ? null : endDayRequest.get();
        String userId = userIdRequest.isEmpty() ? null : userIdRequest.get();
        HttpResponseEntity httpResponseEntity = null;


        //Check valid date
        if(startDay != null) {
            if (!DateUtils.isValidDate(startDay)) {
                throw new BadRequestException("Ngày bắt đầu phải hợp lệ và có dạng dd-mm-yyyy");
            }
        }

        if(endDay != null) {
            if (!DateUtils.isValidDate(endDay)) {
                throw new BadRequestException("Ngày kết thúc phải hợp lệ và có dạng dd-mm-yyyy");
            }
        }

        if(startDay != null && endDay != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            try {
                if (!sdf.parse(startDay).before(sdf.parse(endDay))) {
                    throw new BadRequestException("Ngày bắt đầu phải nhỏ hơn ngày kết thúc");
                }
            } catch (BadRequestException ex) {
                throw new BadRequestException(ex.getMessage());
            } catch (ParseException ex) {
                throw new BadRequestException(ex.getMessage());
            }
        }

        if(statusRequest.isPresent()) {
            switch (statusRequest.get()) {
                case 0:{status = OrderStatus.WAIT_CONFIRM;break;}
                case 1:{status = OrderStatus.CONFIRMED;break;}
                case 2:{status = OrderStatus.BUYER_CANCEL;break;}
                case 3:{status = OrderStatus.SELLER_CANCEL;break;}
                case 4:{status = OrderStatus.FINISH;break;}
            }
        }



            httpResponseEntity = orderService.getAll(page, size, status, startDay, endDay, userId);



        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }


    @PostMapping("/confirm/{order_id}")
    public ResponseEntity<HttpResponseEntity> confirm(
            @RequestHeader("Authorization") String token,
            @PathVariable(name = "order_id") String orderId
    ){
        HttpResponseEntity httpResponseEntity = orderService.confim(token,orderId);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

    @PostMapping("/seller-cancel/{order_id}")
    public ResponseEntity<HttpResponseEntity> sellerCancel(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid OrderCancel orderCancel,
            @PathVariable(name = "order_id") String orderId
    ){
        HttpResponseEntity httpResponseEntity = orderService.sellerCancel(token,orderCancel,orderId);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

    @PostMapping("/buyer-cancel/{order_id}")
    public ResponseEntity<HttpResponseEntity> buyerCancel(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid OrderCancel orderCancel,
            @PathVariable(name = "order_id") String orderId
    ){
        HttpResponseEntity httpResponseEntity = orderService.buyerCancel(token,orderCancel,orderId);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

}
