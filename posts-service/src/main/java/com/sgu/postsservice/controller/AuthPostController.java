package com.sgu.postsservice.controller;

import com.sgu.postsservice.dto.request.*;
import com.sgu.postsservice.dto.response.HttpResponseEntity;
import com.sgu.postsservice.service.PostsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/auth/posts")
public class AuthPostController {
    @Autowired
    private PostsService postsService;

    @PostMapping("/create")
    public ResponseEntity<HttpResponseEntity> createPost(
            @RequestBody @Valid PostRequest postRequest,
            @RequestHeader("Authorization") String token
    ){
        HttpResponseEntity httpResponseEntity = postsService.createPosts(token, postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(httpResponseEntity);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<HttpResponseEntity> updatePost(
            @PathVariable("id") String id,
            @RequestBody @Valid PostUpdateRequest postUpdateRequest
    ) {
        HttpResponseEntity httpResponseEntity = postsService.updatePosts(id, postUpdateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

    @PostMapping("/uploads")
    public ResponseEntity<HttpResponseEntity> updatePost(
            @RequestParam("files") MultipartFile[] files
    ) {
        HttpResponseEntity httpResponseEntity = postsService.uploadMultiFiles(files);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

    @PutMapping("/user-hidden/{id}")
    public ResponseEntity<HttpResponseEntity> userHiddenPosts(
            @PathVariable(name = "id") String id,
            @RequestBody @Valid PostsHiddenRequest postsHiddenRequest,
            @RequestHeader("Authorization") String token
    ) {
        HttpResponseEntity httpResponseEntity = postsService.userHidden(id,postsHiddenRequest,token);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

    @PutMapping("/user-unhidden/{id}")
    public ResponseEntity<HttpResponseEntity> userUnhiddenPosts(
            @PathVariable(name = "id") String id,
            @RequestHeader("Authorization") String token
    ) {
        HttpResponseEntity httpResponseEntity = postsService.userUnhidden(id,token);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

    @DeleteMapping("/user-delete/{slug}")
    public ResponseEntity<HttpResponseEntity> userDelete(
            @PathVariable(name = "slug") String slug,
            @RequestBody @Valid PostsDeleteRequest postsDeleteRequest,
            @RequestHeader("Authorization") String token
    ) {
        HttpResponseEntity httpResponseEntity = postsService.userDelete(slug,postsDeleteRequest,token);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

}
