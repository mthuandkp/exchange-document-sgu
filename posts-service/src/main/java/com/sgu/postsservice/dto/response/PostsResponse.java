package com.sgu.postsservice.dto.response;

import com.sgu.postsservice.constant.PostStatus;
import com.sgu.postsservice.model.Category;
import com.sgu.postsservice.utils.DateUtils;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class PostsResponse {
    private String id;
    private String accountId;
    private UserResponse userProfile;
    private String title;
    private String description;
    private Long price;
    private String postSlug;
    private PostStatus postStatus;
    private String reasonBlock;
    private String thumbnail;
    private String createdAt;
    private String updatedAt;
    private CategoryResponse category;
    private List<String> postsImageList;
}
