package com.sgu.sliderservice.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sgu.sliderservice.exception.BadRequestException;
import com.sgu.sliderservice.exception.InternalServerException;
import com.sgu.sliderservice.exception.ServerInternalException;
import com.sgu.sliderservice.service.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;


    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }


    public Map<?, ?> upload(MultipartFile multipartFile,String folder) {
        if (multipartFile.isEmpty()) throw new BadRequestException("File is not exist!!");
        try {
            File file = convert(multipartFile);
            Map<?, ?> resultMap = cloudinary.uploader()
                    .upload(file, ObjectUtils.asMap("folder", folder, "",""));
            file.delete();
            return resultMap;
        } catch (IOException ioException) {
            throw new ServerInternalException(ioException.getMessage());
        }

    }

    @Override
    public Map<?,?> destroy(String id) {
        try {
            Map<?,?> map = cloudinary.uploader().destroy(id,ObjectUtils.emptyMap());
            return map;
        } catch (IOException e) {
            throw new ServerInternalException(e.getMessage());
        }
    }

    private File convert(MultipartFile multipartFile) {
        try {
            File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            FileOutputStream fOutputStream = new FileOutputStream(file);
            fOutputStream.write(multipartFile.getBytes());
            fOutputStream.close();
            return file;
        } catch (IOException ioException) {
            throw new ServerInternalException(ioException.getMessage());
        }
    }
}
