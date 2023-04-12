package com.sgu.sliderservice.controller;

import com.sgu.sliderservice.dto.request.DeleteImageRequest;
import com.sgu.sliderservice.dto.request.HiddenImageRequest;
import com.sgu.sliderservice.dto.response.HttpResponseEntity;
import com.sgu.sliderservice.service.SliderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/sliders")
public class SliderController {
    @Autowired
    private SliderService sliderService;


    @GetMapping("")
    public ResponseEntity<HttpResponseEntity> getAllWithPagination(
            @RequestParam(name = "page", required = true) Optional<Integer> pageRequest,
            @RequestParam(name = "size", required = true) Optional<Integer> sizeRequest
    ){

        HttpResponseEntity httpResponseEntity = null;
        if(pageRequest.isEmpty()  && sizeRequest.isEmpty()){
            httpResponseEntity = sliderService.getAll();
        }
        else{
            Integer page = pageRequest.isEmpty() ? 1 : pageRequest.get();
            Integer size = sizeRequest.isEmpty() ? 20 : sizeRequest.get();

            httpResponseEntity = sliderService.getAllWithPagination(page,size);
        }

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }



}
