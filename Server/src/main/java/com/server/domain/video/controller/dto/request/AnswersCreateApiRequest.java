package com.server.domain.video.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class AnswersCreateApiRequest {
    private List<String> myAnswers;
}
