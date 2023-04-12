package com.sgu.categoryservice.controller;

import com.sgu.categoryservice.dto.request.CategoryRequest;
import com.sgu.categoryservice.dto.request.DeleteCategory;
import com.sgu.categoryservice.dto.response.HttpResponseEntity;
import com.sgu.categoryservice.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/category")
public class AdminCategoryController {
    @Autowired
    private CategoryService categoryService;
    @PostMapping("/create")
    public ResponseEntity<HttpResponseEntity> create(
            @RequestBody @Valid CategoryRequest categoryRequest
    ) {
        HttpResponseEntity httpResponseEntity = categoryService.create(categoryRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(httpResponseEntity);
    }

    @PutMapping("/update/{slug}")
    public ResponseEntity<HttpResponseEntity> updateById(
            @PathVariable(name = "slug") String slug,
            @RequestBody @Valid CategoryRequest categoryRequest
    ) {
        HttpResponseEntity httpResponseEntity = categoryService.update(slug, categoryRequest);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

    @DeleteMapping("/delete/{slug}")
    public ResponseEntity<HttpResponseEntity> delete(
            @PathVariable(name = "slug") String slug) {
        HttpResponseEntity httpResponseEntity = categoryService.delete(slug);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

    @PostMapping("/upload")
    public ResponseEntity<HttpResponseEntity> delete(
            @RequestParam(name = "file") MultipartFile multipartFile
    ) {
        HttpResponseEntity httpResponseEntity = categoryService.uploadImage(multipartFile);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }
}
