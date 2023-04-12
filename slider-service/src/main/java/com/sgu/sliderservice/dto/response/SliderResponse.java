package com.sgu.sliderservice.dto.response;

import com.sgu.sliderservice.utils.DateUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class SliderResponse {
    private String id;
    private String public_id;
    private String url;
    private boolean isShow = true;
    private String createdAt = DateUtils.getNow();
    private String updatedAt = DateUtils.getNow();
}
