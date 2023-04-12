package com.sgu.sliderservice.service;

import com.sgu.sliderservice.dto.response.HttpResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface SliderService {
    public HttpResponseEntity singleUpload(MultipartFile files);

    public HttpResponseEntity getAll();

    public HttpResponseEntity getAllWithPagination(Integer page, Integer size);

    public HttpResponseEntity hiddenImage(String hiddenImageRequest);

    public HttpResponseEntity unhiddenImage(String hiddenImageRequest);

    public HttpResponseEntity delete(String deleteImageRequest);

}
