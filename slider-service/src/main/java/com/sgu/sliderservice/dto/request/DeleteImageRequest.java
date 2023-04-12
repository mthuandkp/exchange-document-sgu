package com.sgu.sliderservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DeleteImageRequest {
    @NotNull(message = "Id slider không tồn tại")
    private String id;
}
