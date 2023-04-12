package com.sgu.categoryservice.controller;

import com.cloudinary.utils.StringUtils;
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

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("")
    public ResponseEntity<HttpResponseEntity> getAll(
            @RequestParam(name = "page", required = true) Optional<Integer> pageRequest,
            @RequestParam(name = "size", required = true) Optional<Integer> sizeRequest
    ){

        HttpResponseEntity httpResponseEntity = null;
        if(pageRequest.isEmpty()  && sizeRequest.isEmpty()){
            httpResponseEntity = categoryService.getAll();
        }
        else{
            Integer page = pageRequest.isEmpty() ? 1 : pageRequest.get();
            Integer size = sizeRequest.isEmpty() ? 20 : sizeRequest.get();

            httpResponseEntity = categoryService.getAllWithPagiantion(page,size);
        }

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponseEntity> getAllAccountWithPagination(
            @PathVariable(name = "id") String id
    ){
        HttpResponseEntity httpResponseEntity = categoryService.getById(id);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<HttpResponseEntity> getBySlug(
            @PathVariable(name = "slug",required = false) Optional<String> slugRequest
    ){
        String slug = slugRequest.isEmpty() || slugRequest.get().equals(":slug") ? "" : slugRequest.get().replaceAll(" ","");
        HttpResponseEntity httpResponseEntity = categoryService.getBySlug(slug);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

//    @PostMapping("/update/{id}")
//    public ResponseEntity<HttpResponseEntity> updateById(
//            @PathVariable(name = "id") String id,
//            @RequestBody @Valid CategoryRequest categoryRequest
//    ){
//        HttpResponseEntity httpResponseEntity = categoryService.update(id,categoryRequest);
//
//        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
//    }
//
//    @PostMapping("/upload-image")
//    public ResponseEntity<HttpResponseEntity> updateById(
//            @RequestParam(name = "file") MultipartFile file
//            ){
//        HttpResponseEntity httpResponseEntity = categoryService.uploadImage(file);
//
//        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
//    }
//
//    @DeleteMapping("/delete")
//    public ResponseEntity<HttpResponseEntity> create(
//            @RequestBody @Valid DeleteCategory deleteCategory
//    ){
//        HttpResponseEntity httpResponseObject = categoryService.delete(deleteCategory);
//
//        return ResponseEntity.status(HttpStatus.OK).body(httpResponseObject);
//    }
}
