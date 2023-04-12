package com.sgu.categoryservice.service.impl;


import com.sgu.categoryservice.constant.Constant;
import com.sgu.categoryservice.dto.request.CategoryRequest;
import com.sgu.categoryservice.dto.response.CategoryResponse;
import com.sgu.categoryservice.dto.response.HttpResponseEntity;
import com.sgu.categoryservice.dto.response.Pagination;
import com.sgu.categoryservice.exception.ConflictException;
import com.sgu.categoryservice.exception.*;
import com.sgu.categoryservice.exception.ServerInternalException;
import com.sgu.categoryservice.model.Category;
import com.sgu.categoryservice.repository.CategoryRepository;
import com.sgu.categoryservice.service.CategoryService;
import com.sgu.categoryservice.service.CloudinaryService;
import com.sgu.categoryservice.utils.DateUtils;
import com.sgu.categoryservice.utils.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImp implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private WebClient.Builder webBuilder;

    @Override
    public HttpResponseEntity getAll() {
        try{
            List<Category> categoryList = categoryRepository.findAll();
            List<CategoryResponse> categoryResponseList = categoryList.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            HttpResponseEntity responseEntity = convertToResponeEntity(
                    HttpStatus.OK.value(),
                    Constant.SUCCESS,
                    categoryResponseList,
                    null
            );

            return responseEntity;
        }catch (Exception ex){
            throw new ServerInternalException(ex.getMessage());
        }
    }




    @Override
    public HttpResponseEntity getAllWithPagiantion(Integer page, Integer size) {
        try{
            Pageable pageable = PageRequest.of(page-1,size);
            Page<Category> categoryPage = categoryRepository.findAll(pageable);
            List<Category> categoryList = categoryPage.getContent();
            List<CategoryResponse> categoryResponseList = categoryList.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            Pagination pagination = Pagination.builder()
                    .page(page)
                    .size(size)
                    .total_page(categoryPage.getTotalPages())
                    .total_size(categoryPage.getTotalElements())
                    .build();

            HttpResponseEntity httpResponseEntity = convertToResponeEntity(
                    HttpStatus.OK.value(),
                    Constant.SUCCESS,
                    categoryResponseList,
                    pagination
            );

            return httpResponseEntity;
        }catch (Exception ex){
            throw new ServerInternalException(ex.getMessage());
        }
    }

    @Override
    public HttpResponseEntity getById(String id) {
        try{
            Category categoryDb = categoryRepository.findById(new ObjectId(id)).orElseThrow(
                    ()-> new NotFoundException(String.format("Can't find category with id = %s",id))
            );

            List<CategoryResponse> categoryList = Arrays.asList(this.convertToResponse(categoryDb));
            HttpResponseEntity responseEntity = convertToResponeEntity(
                    HttpStatus.OK.value(),
                    Constant.SUCCESS,
                    categoryList,
                    null
            );
            return responseEntity;
        }catch (Exception ex){
            throw new ServerInternalException(ex.getMessage());
        }

    }

    @Override
    public HttpResponseEntity create(CategoryRequest categoryRequest) {
            //check category name Exists
            if(categoryRepository.findByName(categoryRequest.getName()).isPresent()){
                throw new ConflictException(String
                        .format("Danh mục có tên '%s' đã tồn tại",categoryRequest.getName()));
            }


            Category saveCategory = this.convertToEntity(categoryRequest);
            CategoryResponse categoryResponse = this.convertToResponse(categoryRepository.save(saveCategory));
            List<CategoryResponse> categoryResponseList = Arrays.asList(categoryResponse);
            HttpResponseEntity responseEntity = convertToResponeEntity(
                    HttpStatus.OK.value(),
                    Constant.SUCCESS,
                    categoryResponseList,
                    null
            );

            return responseEntity;
    }

    private CategoryResponse convertToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId().toString())
                .name(category.getName())
                .url(category.getUrl())
                .categorySlug(category.getCategorySlug())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }


    @Override
    public HttpResponseEntity update(String slug, CategoryRequest categoryRequest) {
        try{
            Category categoryDb = categoryRepository.findByCategorySlug(slug).orElseThrow(
                    ()->new NotFoundException(String.format("Không thể tìm danh mục có slug = '%s'",slug))
            );

            if(categoryRepository.findByName(categoryRequest.getName()).isPresent()){
                throw new ConflictException(String.format("Danh mục có tên='%s' đã được sử dụng",categoryRequest.getName()));
            }

            categoryDb.setName(categoryRequest.getName());
            categoryDb.setUrl(categoryRequest.getUrl());
            categoryDb.setUpdatedAt(DateUtils.getNow());
            categoryDb.setCategorySlug(StringUtils.convertTextToEnglish(categoryRequest.getName()));

            System.out.println(categoryDb);

            CategoryResponse categoryResponse = this.convertToResponse(categoryRepository.save(categoryDb));
            List<CategoryResponse> categoryResponseList = Arrays.asList(categoryResponse);


            HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                    .code(HttpStatus.OK.value())
                    .message(Constant.SUCCESS)
                    .data(categoryResponseList)
                    .build();

            return httpResponseEntity;
        }catch (Exception ex){
            throw new ServerInternalException(ex.getMessage());
        }
    }

    @Override
    public HttpResponseEntity delete(String slug) {
        try{
            Category categoryDb = categoryRepository.findByCategorySlug(slug).orElseThrow(
                    ()->new NotFoundException(String
                            .format("Danh mục có slug=%s không tồn tại",slug))
            );

            HttpResponseEntity httpResponseEntityPosts = webBuilder.build().get()
                    .uri("http://localhost:8084/api/v1/posts?hidden_posts=true&category_slug=" + slug)
                    .retrieve()
                    .bodyToMono(HttpResponseEntity.class)
                    .block();




            if(httpResponseEntityPosts.getData().size() > 0){
                throw new ConflictException(String.format("Danh mục '%s' đang có bài viết đang sử dụng",slug));
            }

           categoryRepository.delete(categoryDb);
            HttpResponseEntity httpResponseEntity = convertToResponeEntity(
                    HttpStatus.OK.value(),
                    Constant.SUCCESS,
                    null,
                    null
            );

            return httpResponseEntity;
        }catch (Exception ex){
            throw new ServerInternalException(ex.getMessage());
        }
    }

    @Override
    public HttpResponseEntity uploadImage(MultipartFile file) {
        try{
            System.out.println(file);
            String contentType = file.getContentType();
            Predicate<String> checkFileExtension = s -> {
                return !contentType.equals("image/jpeg") && !contentType.equals("image/png");
            };

            if(checkFileExtension.test(contentType)){
                throw new BadRequestException(String.format("Chỉ chấp nhận file png,jpg,jpeg"));
            }

            Map<?,?> map = cloudinaryService.upload(file,"category/");

            List<?> urlList = Arrays.asList(map.get("url"));

            HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                    .code(HttpStatus.OK.value())
                    .message(Constant.SUCCESS)
                    .data(urlList)
                    .build();

            return httpResponseEntity;
        }
        catch (InternalServerException e) {
            throw new ServerInternalException(e.getMessage());
        }
    }

    @Override
    public HttpResponseEntity getBySlug(String slug) {
        try{
            Category categoryDb = categoryRepository.findByCategorySlug(slug).orElseThrow(
                    ()-> new NotFoundException(String.format("Không thể tìm danh mục với slug='%s'",slug))
            );
            List<CategoryResponse> categoryList = Arrays.asList(this.convertToResponse(categoryDb));
            HttpResponseEntity responseEntity = convertToResponeEntity(
                    HttpStatus.OK.value(),
                    Constant.SUCCESS,
                    categoryList,
                    null
            );
            return responseEntity;
        }catch (Exception ex){
            throw new ServerInternalException(ex.getMessage());
        }
    }


    private Category convertToEntity(CategoryRequest categoryRequest) {
        return Category.builder()
                .name(categoryRequest.getName())
                .url(categoryRequest.getUrl())
                .categorySlug(StringUtils.createSlug(categoryRequest.getName()))
                .build();
    }

    private HttpResponseEntity convertToResponeEntity(int code, String mesage, List<?> data,Pagination pagination) {
        return HttpResponseEntity.builder()
                .code(code)
                .message(mesage)
                .data(data)
                .pagination(pagination)
                .build();
    }

}
