package com.server.domain.video.controller.dto.request;

import com.server.global.validation.EachNotBlank;
import com.server.global.validation.EachPositive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class VideoCartDeleteApiRequest {

    @NotNull(message = "{validation.video.cart.videoIds}")
    @EachPositive(message = "{validation.positive}")
    @Size(min = 1, message = "{validation.video.cart.videoIds}")
    public List<Long> videoIds;
}
