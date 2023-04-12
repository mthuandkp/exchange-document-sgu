package com.sgu.sliderservice.service;

import com.sgu.sliderservice.exception.InternalServerException;
import com.sgu.sliderservice.exception.ServerInternalException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudinaryService {
    public Map<?, ?> upload(MultipartFile multipartFile,String folder);

    public Map<?,?> destroy(String id);
}
