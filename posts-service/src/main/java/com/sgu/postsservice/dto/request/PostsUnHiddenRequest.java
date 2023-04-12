package com.sgu.postsservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PostsUnHiddenRequest {
    @NotNull(message = "id bài viết không tồn tại")
    private String postsId;
}
