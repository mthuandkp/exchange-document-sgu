package com.sgu.userservice.service.impl;

import com.sgu.userservice.constant.Constant;
import com.sgu.userservice.dto.request.PersonRequest;
import com.sgu.userservice.dto.response.HttpResponseEntity;
import com.sgu.userservice.exception.BadRequestException;
import com.sgu.userservice.exception.NotFoundException;
import com.sgu.userservice.dto.response.Pagination;
import com.sgu.userservice.model.Person;
import com.sgu.userservice.repository.PersonRepository;
import com.sgu.userservice.service.PersonService;
import com.sgu.userservice.utils.DateUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class PersonServiceImp implements PersonService {
    @Autowired
    PersonRepository personRepository;
    @Override
    public HttpResponseEntity getAllPerson() {
        List<Person> accountList = personRepository.findAll();
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity().convertToResponeEntity(
                HttpStatus.OK.value(),
                Constant.SUCCESS,
                accountList,
                null
        );

        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity getAllPersonWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page-1,size);
        Page<Person> accountPage = personRepository.findAll(pageable);
        List<Person> accountList = accountPage.getContent();
        Pagination pagination = Pagination.builder()
                .page(page)
                .size(size)
                .total_page(accountPage.getTotalPages())
                .total_size(accountPage.getTotalElements())
                .build();

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.convertToResponeEntity(
                HttpStatus.OK.value(),
                Constant.SUCCESS,
                accountList,
                pagination
        );


        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity getById(String id) {
        Person person = personRepository.findById(new ObjectId(id)).orElseThrow(
                ()-> new NotFoundException(String.format("Không thể tìm người duùng có id=%s",id))
        );

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.convertToResponeEntity(
                HttpStatus.OK.value(),
                Constant.SUCCESS,
                Arrays.asList(person),
        null
        );

        return httpResponseEntity;
    }
}
