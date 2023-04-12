package com.sgu.sliderservice.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class SliderRequest {
    private String public_id;
    private String extension;
    private String url;

}
