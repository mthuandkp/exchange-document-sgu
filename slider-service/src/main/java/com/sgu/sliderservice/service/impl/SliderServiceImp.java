package com.sgu.sliderservice.service.impl;

import com.sgu.sliderservice.dto.request.SliderRequest;
import com.sgu.sliderservice.dto.response.HttpResponseEntity;
import com.sgu.sliderservice.dto.response.Pagination;
import com.sgu.sliderservice.dto.response.SliderResponse;
import com.sgu.sliderservice.exception.*;
import com.sgu.sliderservice.model.Slider;
import com.sgu.sliderservice.repository.SliderRepository;
import com.sgu.sliderservice.service.CloudinaryService;
import com.sgu.sliderservice.service.SliderService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class SliderServiceImp implements SliderService {
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private SliderRepository sliderRepository;

    @Override
    public HttpResponseEntity singleUpload(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            Predicate<String> checkFileExtension = s -> {
                return !contentType.equals("image/jpeg") && !contentType.equals("image/png");
            };

            if (checkFileExtension.test(contentType)) {
                throw new BadRequestException(String.format("Chỉ chấp nhận file png,jpg,jpeg"));
            }

            Map<?, ?> map = cloudinaryService.upload(file, "slider/");

            String publicId = String.valueOf(map.get("public_id"));
            String url = String.valueOf(map.get("url"));

            SliderRequest sliderRequest = SliderRequest.builder()
                    .public_id(publicId)
                    .url(url)
                    .build();

            Slider sliderEntity = this.convertToEntity(sliderRequest);
            sliderEntity  = sliderRepository.save(sliderEntity);
            SliderResponse sliderResponse = this.convertToRespone(sliderEntity);
            List<SliderResponse> responseList = Arrays.asList(sliderResponse);

            HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                    .code(HttpStatus.OK.value())
                    .message("Thêm thành công")
                    .data(responseList)
                    .build();

            return httpResponseEntity;
        } catch (ServerInternalException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public HttpResponseEntity getAll() {
        List<Slider> sliderList = sliderRepository.findAll();
        List<SliderResponse> sliderResponseList = sliderList.stream()
                .map(this::convertToRespone)
                .collect(Collectors.toList());

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(sliderResponseList)
                .build();

        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity getAllWithPagination(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Slider> categoryPage = sliderRepository.findAll(pageable);
        List<SliderResponse> categoryList = categoryPage.getContent().stream()
                .map(this::convertToRespone)
                .collect(Collectors.toList());

        Pagination pagination = Pagination.builder()
                .page(page)
                .size(size)
                .total_page(categoryPage.getTotalPages())
                .total_size(categoryPage.getTotalElements())
                .build();

//        HttpResponseEntity httpResponseEntity = convertToResponeEntity(
//                HttpStatus.OK.value(),
//                "SUCCESS",
//                categoryList,
//                pagination
//        );
//
//        return httpResponseEntity;

        return null;
    }

    @Override
    public HttpResponseEntity hiddenImage(String id) {
        Slider sliderEntity = sliderRepository.findById(new ObjectId(id)).orElseThrow(
                () -> new NotFoundException(String.format("Không thể tìm slider"))
        );

        if (sliderEntity.isHidden()) {
            throw new ForbiddenException("Slider đã ẩn trước đó. Không thể ẩn");
        }

        sliderEntity.setHidden(true);

        sliderRepository.save(sliderEntity);
        List<Slider> responseList = Arrays.asList(sliderEntity);
        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.OK.value())
                .message("Ẩn thành công")
                .build();

        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity unhiddenImage(String id) {
        Slider sliderEntity = sliderRepository.findById(new ObjectId(id)).orElseThrow(
                () -> new NotFoundException(String.format("Không thể tìm slider"))
        );

        if (!sliderEntity.isHidden()) {
            throw new ForbiddenException("Slider đang hiển thị. Không thể bỏ ẩn");
        }

        sliderEntity.setHidden(false);

        sliderRepository.save(sliderEntity);
        List<Slider> responseList = Arrays.asList(sliderEntity);
        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.OK.value())
                .message("Hiển thị slider thành công")
                .build();

        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity delete(String id) {
        Slider sliderEntity = sliderRepository.findById(new ObjectId(id)).orElseThrow(
                () -> new NotFoundException(String.format("Không thể tìm slider"))
        );
        Map<?, ?> map = cloudinaryService.destroy(sliderEntity.getPublic_id());
        String result = String.valueOf(map.get("result"));
        if (!result.equals("ok")) {
            throw new ServerInternalException(
                    String.format("Không thể xoá. Không tìm thấy id=%s trên cloudinary", id));
        }

        sliderRepository.delete(sliderEntity);

        List<Slider> responseList = Arrays.asList(sliderEntity);
        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.OK.value())
                .message("Xoá thành công")
                .build();

        return httpResponseEntity;
    }

    private SliderResponse convertToRespone(Slider slider) {
        return SliderResponse.builder()
                .id(slider.getId().toString())
                .public_id(slider.getPublic_id())
                .url(slider.getUrl())
                .createdAt(slider.getCreatedAt())
                .updatedAt(slider.getUpdatedAt())
                .isShow(slider.isHidden())
                .build();
    }

    private Slider convertToEntity(SliderRequest sliderRequest) {
        return Slider.builder()
                .public_id(sliderRequest.getPublic_id())
                .url(sliderRequest.getUrl())
                .build();
    }

}
