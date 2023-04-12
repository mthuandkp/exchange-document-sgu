package com.sgu.orderservice.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sgu.orderservice.constant.OrderStatus;
import com.sgu.orderservice.dto.request.OrderCancel;
import com.sgu.orderservice.dto.request.OrderRequest;
import com.sgu.orderservice.dto.response.HttpResponseEntity;
import com.sgu.orderservice.dto.response.OrderResponse;
import com.sgu.orderservice.dto.response.Pagination;
import com.sgu.orderservice.exception.BadRequestException;
import com.sgu.orderservice.exception.ConflictException;
import com.sgu.orderservice.exception.NotFoundException;
import com.sgu.orderservice.model.Order;
import com.sgu.orderservice.model.OrderDetail;
import com.sgu.orderservice.repository.OrderDetailRepository;
import com.sgu.orderservice.repository.OrderRepository;
import com.sgu.orderservice.service.OrderService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImp implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private WebClient webClient;
    @Override
    public HttpResponseEntity create(String token, OrderRequest orderRequest) {
        HttpResponseEntity httpResponsePerson = null;
        HttpResponseEntity httpResponsePosts = null;
        String username = null;
        OrderDetail orderDetail = null;


        if(!token.startsWith("Bearer ")){
            throw new BadRequestException("Token không hợp lệ");
        }

        DecodedJWT jwt = JWT.decode(token.substring("Bearer ".length()));

        if(jwt.getClaims().get("role") == null){
            throw new BadRequestException("Access token không chính xác");
        }

        username = jwt.getSubject();


        try{
            httpResponsePerson = webClient.get()
                    .uri("http://localhost:8082/api/v1/profile/username/"+username)
                    .retrieve()
                    .bodyToMono(HttpResponseEntity.class)
                    .block();


            JsonObject jsonObject = new Gson().fromJson(new Gson().toJson(httpResponsePerson.getData().get(0)),JsonObject.class);
            if(jsonObject.get("isBlock").getAsBoolean()){
                throw new ConflictException("Tài khoản đã bị khoá");
            }
            else if(!jsonObject.get("isActive").getAsBoolean()){
                throw new ConflictException("Tài khoản chưa được kích hoạt");
            }

        }catch (ConflictException ex){
            throw new ConflictException(ex.getMessage());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            throw new NotFoundException("Không tìm thấy user");
        }


        try{
            httpResponsePosts = webClient.get()
                    .uri("http://localhost:8084/api/v1/posts/id/" + orderRequest.getPostsId())
                    .retrieve()
                    .bodyToMono(HttpResponseEntity.class)
                    .block();

            JsonObject jsonObject = new Gson().fromJson(new Gson().toJson(httpResponsePosts.getData().get(0)),JsonObject.class);
            if(!jsonObject.get("postStatus").getAsString().equals("DISPLAY")){
                throw new ConflictException("Bài viết đã bị ẩn hoặc xoá");
            }

            JsonObject categoryJsonObject = jsonObject.get("category").getAsJsonObject();
            orderDetail = OrderDetail.builder()
                    .postsId(new ObjectId(jsonObject.get("id").getAsString()))
                    .title(jsonObject.get("title").getAsString())
                    .description(jsonObject.get("description").getAsString())
                    .price(jsonObject.get("price").getAsLong())
                    .thumbnail(jsonObject.get("thumbnail").getAsString())
                    .categorySlug(categoryJsonObject.get("categorySlug").getAsString())
                    .build();
        }catch (ConflictException ex){
            throw new ConflictException(ex.getMessage());
        }
        catch (Exception e){
            throw new NotFoundException("Không tìm thấy bài viết");
        }


        Order order = Order.builder()
                .username(username)
                .address(orderRequest.getAddress())
                .phone(orderRequest.getPhone())
                .build();

        System.out.println(order);
        order = orderRepository.save(order);
        orderDetail.setOrderId(order.getId());
        orderDetailRepository.save(orderDetail);

        HttpResponseEntity httpResponseEntity  = HttpResponseEntity.builder()
                .code(HttpStatus.CREATED.value())
                .message("Tạo thành công")
                .build();
        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity getAll(Integer page, Integer size, OrderStatus status, String startDay, String endDay, String userId) {
        List<Order> orderList = orderRepository.findAll();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy:HH-mm-ss");
        Pagination pagination = null;

        if(status != null){
            orderList = orderList.stream()
                    .filter(order -> order.getOrderStatus().equals(status))
                    .collect(Collectors.toList());
        }



        if(startDay != null){

            try {
                long epoch = df.parse(startDay+":00-00-00").getTime();
                orderList = orderList.stream()
                        .filter(order -> {
                            long createAt = Long.valueOf(order.getUpdatedAt());
                            return createAt >= epoch;
                        })
                        .collect(Collectors.toList());
            } catch (ParseException e) {
                throw new BadRequestException(e.getMessage());
            }
        }

        if(endDay != null){

            try {
                long epoch = df.parse(endDay+":23-59-59").getTime();
                orderList = orderList.stream()
                        .filter(order -> {
                            long createAt = Long.valueOf(order.getUpdatedAt());
                            return createAt <= epoch;
                        })
                        .collect(Collectors.toList());
            } catch (ParseException e) {
                throw new BadRequestException(e.getMessage());
            }
        }

        if(userId != null){
            orderList = orderList.stream()
                    .filter(order -> order.getUsername().equals(userId))
                    .collect(Collectors.toList());
        }

        //Pagination
        List<Order> paginationOrderList = new ArrayList<>();
        if(page != null || size != null){
            page = page==null ? 1 : page;
            size = size==null ? 20 : size;

            if(page < 1 || size <= 0){
                throw new BadRequestException("Trang phải >=1 và size > 0");
            }

            int to = page * size;
            int from = to - size;
            int totalPage = orderList.size() % size == 0 ?
                    orderList.size() / size : orderList.size() / size + 1;
            int totalSize = orderList.size();

            for (int i = from; i < to; i++) {
                if (i < orderList.size()) {
                    paginationOrderList.add(orderList.get(i));
                }
            }
            orderList = paginationOrderList;
            pagination = Pagination.builder()
                    .page(page)
                    .size(size)
                    .total_page(totalPage)
                    .total_size(totalSize)
                    .build();

        }

        List<OrderResponse> orderResponseList = orderList.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.OK.value())
                .message("Thành công")
                .data(orderResponseList)
                .pagination(pagination)
                .build();

        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity confim(String token, String orderId) {
        if(!token.startsWith("Bearer ")){
            throw new BadRequestException("Token không hợp lệ");
        }

        DecodedJWT jwt = JWT.decode(token.substring("Bearer ".length()));

        if(jwt.getClaims().get("role") == null){
            throw new BadRequestException("Access token không chính xác");
        }

        String username = jwt.getSubject();

        Boolean isSellerOfOrder = this.IsSellerOfOrder(orderId,username);
        if(!isSellerOfOrder){
            throw new ConflictException("Bạn không phải là người đăng bài viết");
        }

        Order orderdb = orderRepository.findById(new ObjectId(orderId)).orElseThrow(
                ()-> new NotFoundException("Đơn hàng không tồn tại")
        );

        if(!orderdb.getOrderStatus().equals(OrderStatus.WAIT_CONFIRM)){
            throw new ConflictException("Đơn hàng này đã được xác nhận hoặc đã huỷ hoặc đã hoàn tất");
        }

        orderdb.setOrderStatus(OrderStatus.CONFIRMED);

        orderRepository.save(orderdb);

        HttpResponseEntity httpResponseEntity  = HttpResponseEntity.builder()
                .code(HttpStatus.OK.value())
                .message("Huỷ đơn thành công")
                .build();
        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity sellerCancel(String token, OrderCancel orderCancel, String orderId) {
        if(!token.startsWith("Bearer ")){
            throw new BadRequestException("Token không hợp lệ");
        }

        DecodedJWT jwt = JWT.decode(token.substring("Bearer ".length()));

        if(jwt.getClaims().get("role") == null){
            throw new BadRequestException("Access token không chính xác");
        }

        String username = jwt.getSubject();

        Boolean isSellerOfOrder = this.IsSellerOfOrder(orderId,username);
        if(!isSellerOfOrder){
            throw new ConflictException("Bạn không phải là người đăng bài viết");
        }

        Order orderdb = orderRepository.findById(new ObjectId(orderId)).orElseThrow(
                ()-> new NotFoundException("Đơn hàng không tồn tại")
        );

        if(!orderdb.getOrderStatus().equals(OrderStatus.WAIT_CONFIRM) &&
                !orderdb.getOrderStatus().equals(OrderStatus.CONFIRMED)){
            throw new ConflictException("Đơn hàng này đã huỷ hoặc đã hoàn tất");
        }

        orderdb = orderdb.toBuilder()
                .orderStatus(OrderStatus.SELLER_CANCEL)
                .reasonCancel(orderCancel.getReasonCancel())
                .build();


        orderRepository.save(orderdb);

        HttpResponseEntity httpResponseEntity  = HttpResponseEntity.builder()
                .code(HttpStatus.OK.value())
                .message("Huỷ đơn thành công")
                .build();
        return httpResponseEntity;

    }

    @Override
    public HttpResponseEntity buyerCancel(String token, OrderCancel orderCancel, String orderId) {
        if(!token.startsWith("Bearer ")){
            throw new BadRequestException("Token không hợp lệ");
        }

        DecodedJWT jwt = JWT.decode(token.substring("Bearer ".length()));

        if(jwt.getClaims().get("role") == null){
            throw new BadRequestException("Access token không chính xác");
        }

        String username = jwt.getSubject();

        Boolean isSellerOfOrder = this.IsBuyerOfOrder(orderId,username);
        if(!isSellerOfOrder){
            throw new ConflictException("Bạn không phải là người đặt mua đơn hàng");
        }

        Order orderdb = orderRepository.findById(new ObjectId(orderId)).orElseThrow(
                ()-> new NotFoundException("Đơn hàng không tồn tại")
        );

        if(!orderdb.getOrderStatus().equals(OrderStatus.WAIT_CONFIRM) && !orderdb.getOrderStatus().equals(OrderStatus.CONFIRMED)){
            throw new ConflictException("Đơn hàng này đã huỷ hoặc đã hoàn tất");
        }

        orderdb = orderdb.toBuilder()
                .orderStatus(OrderStatus.SELLER_CANCEL)
                .reasonCancel(orderCancel.getReasonCancel())
                .build();


        orderRepository.save(orderdb);

        HttpResponseEntity httpResponseEntity  = HttpResponseEntity.builder()
                .code(HttpStatus.OK.value())
                .message("Cập nhật thành công")
                .build();
        return httpResponseEntity;
    }

    private Boolean IsBuyerOfOrder(String orderId, String username) {
        Order orderDB = orderRepository.findById(new ObjectId(orderId))
                .orElseThrow(()->new NotFoundException("Không tìm thấy đơn hàng"));
        return orderDB.getUsername().equals(username);
    }

    private Boolean IsSellerOfOrder(String id, String username) {
        //Get posts id
        OrderDetail orderDetailDb = orderDetailRepository.findByOrderId(new ObjectId(id))
                .orElseThrow(()->new NotFoundException("Đơn hàng không tồn tại"));
        String postsId = orderDetailDb.getPostsId().toString();
        //Call to posts-service to get infor person of posts
        HttpResponseEntity httpResponsePosts = webClient.get()
                .uri("http://localhost:8084/api/v1/posts/id/" + postsId)
                .retrieve()
                .bodyToMono(HttpResponseEntity.class)
                .block();

        JsonObject jsonObject = new Gson()
                .fromJson(new Gson().toJson(httpResponsePosts.getData().get(0)),JsonObject.class);
        jsonObject = jsonObject.getAsJsonObject("userProfile");

        String ownerOfPost = jsonObject.get("username").getAsString();


        return ownerOfPost.equals(username);
    }

    private OrderResponse convertToResponse(Order order) {
        Optional<OrderDetail> orderDetailOptional = orderDetailRepository.findByOrderId(order.getId());
        if(orderDetailOptional.isEmpty()){
            return null;
        }
        OrderDetail orderDetail = orderDetailOptional.get();

        return OrderResponse.builder()
                .id(order.getId().toString())
                .username(order.getUsername())
                .address(order.getAddress())
                .phone(order.getPhone())
                .reasonCancel(order.getReasonCancel())
                .orderStatus(order.getOrderStatus())
                .postsId(orderDetail.getPostsId().toString())
                .title(orderDetail.getTitle())
                .description(orderDetail.getDescription())
                .price(orderDetail.getPrice())
                .thumbnail(orderDetail.getThumbnail())
                .categorySlug(orderDetail.getCategorySlug())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
