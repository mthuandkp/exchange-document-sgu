package com.sgu.postsservice.controller;

import com.sgu.postsservice.dto.request.PostRequest;
import com.sgu.postsservice.dto.request.PostUpdateRequest;
import com.sgu.postsservice.dto.request.PostsDeleteRequest;
import com.sgu.postsservice.dto.request.PostsHiddenRequest;
import com.sgu.postsservice.dto.response.HttpResponseEntity;
import com.sgu.postsservice.exception.BadRequestException;
import com.sgu.postsservice.service.PostsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/posts")
public class PostsController {
    @Autowired
    private PostsService postsService;

    @GetMapping("")
    public ResponseEntity<HttpResponseEntity> getAll(
            @RequestParam(name = "page") Optional<Integer> pageRequest,
            @RequestParam(name = "size") Optional<Integer> sizeRequest,
            @RequestParam(name = "hidden_posts",defaultValue = "false") Boolean getHiddenRequest,
            @RequestParam(name = "category_slug", defaultValue = "") Optional<String> slug,
            @RequestParam(name = "title_keyword", defaultValue = "") Optional<String> titleKeyword,
            @RequestParam(name = "date",defaultValue = "ASC") String date,
            @RequestParam(name = "start_price") Optional<Long> startPriceReq,
            @RequestParam(name = "end_price") Optional<Long> endPriceReq
    ) {
        HttpResponseEntity httpResponseEntity = null;
        Integer page = null;
        Integer size = null;
        Long startPrice = startPriceReq.isPresent() ? startPriceReq.get() : null;
        Long endPrice = endPriceReq.isPresent() ? endPriceReq.get() : null;

        if (pageRequest.isPresent() || sizeRequest.isPresent()) {
            page = pageRequest.isPresent() ? pageRequest.get() : 1;
            size = sizeRequest.isPresent() ? sizeRequest.get() : 20;

            if (page < 1 || size <= 0) {
                throw new BadRequestException("Số trang phải >= 1 và kích thước phải > 0");
            }
        }

        if(!date.toLowerCase().equals("asc") && !date.toLowerCase().equals("desc")){
            throw new BadRequestException("date phải là asc hoặc desc");
        }

        httpResponseEntity = postsService.getAll(getHiddenRequest, slug.get(), titleKeyword.get(), date, page, size, startPrice,endPrice);


        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }


    @GetMapping("/slug/{slug}")
    public ResponseEntity<HttpResponseEntity> getBySlug(
            @PathVariable("slug") String slug
    ){
        HttpResponseEntity httpResponseObject = postsService.getBySlug(slug);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseObject);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<HttpResponseEntity> getById(
            @PathVariable("id") String id
    ){
        HttpResponseEntity httpResponseObject = postsService.getById(id);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseObject);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<HttpResponseEntity> getByUser(
            @PathVariable("id") String id
    ){
        HttpResponseEntity httpResponseObject = postsService.getByUserId(id);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseObject);
    }






//    @PostMapping("/user-unhidden-posts")
//    public ResponseEntity<HttpResponseEntity> userUnHiddenPosts(
//            @RequestBody @Valid PostsHiddenRequest postsHiddenRequest
//    ){
//        HttpResponseEntity httpResponseEntity = postsService.userUnhidden(postsHiddenRequest);
//
//        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
//    }
//

//
//    @DeleteMapping("/admin-delete")
//    public ResponseEntity<HttpResponseEntity> adminDelete(
//            @RequestBody @Valid PostsDeleteRequest postsDeleteRequest
//    ){
//        HttpResponseEntity httpResponseEntity = postsService.adminDelete(postsDeleteRequest);
//
//        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
//    }

}
