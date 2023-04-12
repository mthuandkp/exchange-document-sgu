package com.sgu.categoryservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class DeleteCategory {
    @NotNull(message = "Slug là bắt buộc")
    private String slug;
}
