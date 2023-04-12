package com.sgu.categoryservice.dto.response;

import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
public class CategoryResponse {
    private String id;
    private String name;
    private String url;
    private String categorySlug;
    private String createdAt;
    private String updatedAt;
}
