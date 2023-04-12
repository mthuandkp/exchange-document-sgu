package com.sgu.postsservice.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sgu.postsservice.exception.BadRequestException;
import com.sgu.postsservice.exception.InternalServerException;
import com.sgu.postsservice.service.CloudinaryService;
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


    public Map<?, ?> upload(MultipartFile multipartFile,String folder) throws InternalServerException {
        if (multipartFile.isEmpty()) throw new BadRequestException("File is not exist!!");
        try {
            File file = convert(multipartFile);
            Map<?, ?> resultMap = cloudinary.uploader()
                    .upload(file, ObjectUtils.asMap("folder", folder, "",""));
            file.delete();
            return resultMap;
        } catch (IOException ioException) {
            throw new InternalServerException(ioException.getMessage());
        }

    }

    private File convert(MultipartFile multipartFile) throws InternalServerException {
        try {
            File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            FileOutputStream fOutputStream = new FileOutputStream(file);
            fOutputStream.write(multipartFile.getBytes());
            fOutputStream.close();
            return file;
        } catch (IOException ioException) {
            throw new InternalServerException(ioException.getMessage());
        }
    }
}
