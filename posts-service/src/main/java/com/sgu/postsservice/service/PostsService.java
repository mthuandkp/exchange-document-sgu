package com.sgu.postsservice.service;

import com.sgu.postsservice.dto.request.*;
import com.sgu.postsservice.dto.response.HttpResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface PostsService {
    public HttpResponseEntity createPosts(String token, PostRequest postRequest);

    public HttpResponseEntity getAll(Boolean getHidden, String slug, String keyword, String date,Integer page, Integer size,Long startPrice, Long endPrice);


    public HttpResponseEntity updatePosts(String id, PostUpdateRequest postUpdateRequest);

    public HttpResponseEntity uploadMultiFiles(MultipartFile[] files);
//
//    public HttpResponseEntity getById(Long id);
//
//    public HttpResponseEntity getByAccountId(Long id);
//
//    public HttpResponseEntity getByCategoryId(Long id);
//
    public HttpResponseEntity userHidden(String id, PostsHiddenRequest postsHiddenRequest, String token);
//
    public HttpResponseEntity userUnhidden(String id,String token);
//
    public HttpResponseEntity userDelete(String slug, PostsDeleteRequest postsDeleteRequest, String token);
//
    public HttpResponseEntity adminDelete(String id, PostsDeleteRequest postsDeleteRequest);
//
//    public HttpResponseEntity getByCategorySlug(String slug, int page, int size);
    public HttpResponseEntity getBySlug(String slug);

    public HttpResponseEntity getByUserId(String id);

    public HttpResponseEntity getById(String id);
}
