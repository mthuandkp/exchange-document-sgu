package com.sgu.postsservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sgu.postsservice.constant.PostStatus;
import com.sgu.postsservice.utils.DateUtils;
import com.sgu.postsservice.utils.StringUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document("posts")
@Getter
@Setter
@Builder(toBuilder = true)
@ToString
@AllArgsConstructor

public class Posts {
    @Id
    @Field("id")
    private ObjectId id;
    @Field("account_id")
    private ObjectId accountId;
    @Field("title")
    private String title;
    @Field("description")
    private String description;
    @Field("price")
    private Long price;
    @Field("posts_slug")
    private String postsSlug;
    @Builder.Default
    @Field("posts_status")
    private PostStatus postStatus = PostStatus.DISPLAY;
    @Builder.Default
    @Field("reason_block")
    private String reasonBlock="";
    @Field("thumbnail")
    private String thumbnail;
    @Builder.Default
    @Field("created_at")
    private String createdAt = DateUtils.getNow();
    @Builder.Default
    @Field("updated_at")
    private String updatedAt = DateUtils.getNow();
    @Field("category")
    private Category category;
    @Field("posts_image")
    private List<String> postsImageList;

    public int compareTitle(Posts post, List<String> keywordList) {
        int thisPoint = this.calcPriority(this.getTitle(),keywordList);
        int otherPoint = this.calcPriority(post.getTitle(),keywordList);

        if(thisPoint == otherPoint){
            return 0;
        }
        return thisPoint > otherPoint ? -1:1;
    }

    public static int calcPriority(String title, List<String> keywordList) {
        int priority = 0;
        String encodeTitle = StringUtils.convertTextToEnglish(title);
        for(String key: keywordList){
            if(encodeTitle.contains(key)){
                priority++;
            }
        }

        return priority;
    }

    public int compareTime(Posts p, String date) {
        Long thisTime = Long.valueOf(this.getUpdatedAt());
        Long otherTime = Long.valueOf(p.getUpdatedAt());
        if(thisTime == otherTime) return 0;

        if(date.toLowerCase().equals("asc")){
            return thisTime > otherTime ? 1 : -1;
        }
        return thisTime < otherTime ? 1 : -1;

    }
}
