package com.sgu.categoryservice.service;

import com.sgu.categoryservice.exception.InternalServerException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudinaryService {
    public Map<?, ?> upload(MultipartFile multipartFile,String folder) throws InternalServerException;
}
