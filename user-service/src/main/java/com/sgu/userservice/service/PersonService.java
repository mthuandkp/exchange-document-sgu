package com.sgu.userservice.service;

import com.sgu.userservice.dto.request.PersonRequest;
import com.sgu.userservice.dto.response.HttpResponseEntity;

public interface PersonService {
    public HttpResponseEntity getAllPerson();

    public HttpResponseEntity getAllPersonWithPagination(int page, int size);

    public HttpResponseEntity getById(String id);

}
