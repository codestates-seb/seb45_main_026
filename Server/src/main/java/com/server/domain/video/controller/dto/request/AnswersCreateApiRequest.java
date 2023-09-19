package com.server.domain.video.controller.dto.request;

import com.server.global.validation.EachNotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class AnswersCreateApiRequest {
    @NotNull(message = "{validation.question.myAnswer}")
    @Size(min = 1, message = "{validation.question.myAnswer.size}")
    @EachNotBlank(message = "{validation.question.myAnswer.eachNotBlank}")
    private List<String> myAnswers;
}
