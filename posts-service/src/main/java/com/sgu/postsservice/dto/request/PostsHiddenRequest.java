package com.sgu.postsservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostsHiddenRequest {
    @NotNull(message = "Lý do ẩn tin không tồn tại")
    private String reasonBlock;
}
