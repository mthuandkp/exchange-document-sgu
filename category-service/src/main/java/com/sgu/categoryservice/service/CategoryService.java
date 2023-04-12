package com.sgu.categoryservice.service;


import com.sgu.categoryservice.dto.request.CategoryRequest;
import com.sgu.categoryservice.dto.response.HttpResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface CategoryService {
    public HttpResponseEntity getAll();

    public HttpResponseEntity getAllWithPagiantion(Integer page, Integer size);

    public HttpResponseEntity getById(String id);

    public HttpResponseEntity create(CategoryRequest categoryRequest);

    public HttpResponseEntity update(String slug, CategoryRequest categoryRequest);

    public HttpResponseEntity delete(String deleteCategory);

    public HttpResponseEntity uploadImage(MultipartFile file);

    public HttpResponseEntity getBySlug(String slug);

}
