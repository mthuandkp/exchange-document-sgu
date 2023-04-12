package com.sgu.sliderservice.controller;

import com.sgu.sliderservice.dto.request.DeleteImageRequest;
import com.sgu.sliderservice.dto.request.HiddenImageRequest;
import com.sgu.sliderservice.dto.response.HttpResponseEntity;
import com.sgu.sliderservice.service.SliderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/sliders")
public class AdminSliderController {
    @Autowired
    private SliderService sliderService;
    @PostMapping("/upload")
    public ResponseEntity<HttpResponseEntity> singleUpload(
            @RequestParam("file") MultipartFile file
    ){
        HttpResponseEntity httpResponseEntity = sliderService.singleUpload(file);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

    @PutMapping("/hidden/{id}")
    public ResponseEntity<HttpResponseEntity> hiddenImage(
        @PathVariable(name = "id") String id
    ) {
        HttpResponseEntity httpResponseEntity = sliderService.hiddenImage(id);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }

    @PutMapping("/unhidden/{id}")
    public ResponseEntity<HttpResponseEntity> unhiddenImage(
            @PathVariable(name = "id") String id
    ){
        HttpResponseEntity httpResponseEntity = sliderService.unhiddenImage(id);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpResponseEntity> delete(
            @PathVariable(name = "id") String id
    ){
        HttpResponseEntity httpResponseEntity = sliderService.delete(id);

        return ResponseEntity.status(HttpStatus.OK).body(httpResponseEntity);
    }
}
