package com.sgu.userservice.service;

import com.sgu.userservice.dto.request.*;
import com.sgu.userservice.dto.response.HttpResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    public HttpResponseEntity register(UserRequest userRequest);

    public HttpResponseEntity delete(DeleteRequest deleteRequest);

    public HttpResponseEntity profile(String token);

    public HttpResponseEntity updatePerson(String token, PersonRequest personRequest);

    public HttpResponseEntity personProfile(String id);

    public HttpResponseEntity uploadImage(MultipartFile file);

    public HttpResponseEntity getByUsername(String username);
}
