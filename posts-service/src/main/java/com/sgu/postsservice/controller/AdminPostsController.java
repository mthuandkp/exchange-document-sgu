package com.sgu.postsservice.controller;

import com.sgu.postsservice.dto.request.PostsDeleteRequest;
import com.sgu.postsservice.dto.response.HttpResponseEntity;
import com.sgu.postsservice.service.PostsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/posts")
public class AdminPostsController {
    @Autowired
    private PostsService postsService;

    @DeleteMapping("/admin-delete/{slug}")
    public ResponseEntity<HttpResponseEntity> userDelete(
            @PathVariable(name = "slug") String slug,
            @RequestBody @Valid PostsDeleteRequest postsDeleteRequest
    ){
        HttpResponseEntity httpResponseEntity = postsService.adminDelete(slug,postsDeleteRequest);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }


}
