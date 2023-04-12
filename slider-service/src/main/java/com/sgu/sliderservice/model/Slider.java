package com.sgu.sliderservice.model;

import com.sgu.sliderservice.utils.DateUtils;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("slider")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
public class Slider {
    @Id
    private ObjectId id;
    private String public_id;
    private String url;
    @Builder.Default
    private boolean hidden = false;
    @Builder.Default
    private String createdAt = DateUtils.getNow();
    @Builder.Default
    private String updatedAt = DateUtils.getNow();

}
