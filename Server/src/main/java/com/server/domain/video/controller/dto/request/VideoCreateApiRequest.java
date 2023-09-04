package com.server.domain.video.controller.dto.request;

import com.server.domain.video.service.dto.request.VideoCreateServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class VideoCreateApiRequest {

    @NotBlank(message = "{validation.video.name}")
    private String videoName;
    @NotNull(message = "{validation.video.price}")
    @Min(value = 0, message = "{validation.video.price.min}")
    private Integer price;
    private String description;
    @NotNull(message = "{validation.video.categories}")
    @Size(min = 1, message = "{validation.video.categories.size}")
    private List<String> categories;

    public VideoCreateServiceRequest toServiceRequest() {
        return VideoCreateServiceRequest.builder()
                .videoName(videoName)
                .price(price)
                .description(description)
                .categories(categories)
                .build();
    }

}
