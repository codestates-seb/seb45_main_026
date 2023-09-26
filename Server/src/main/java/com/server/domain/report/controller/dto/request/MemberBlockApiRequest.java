package com.server.domain.report.controller.dto.request;

import com.server.domain.report.service.dto.request.MemberBlockServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Positive;
import java.util.Optional;

@AllArgsConstructor
@Getter
@Builder
public class MemberBlockApiRequest {

    @Positive(message = "{validation.positive}")
    private Integer days;

    private String blockReason;

    public MemberBlockServiceRequest toServiceRequest() {
        return MemberBlockServiceRequest.builder()
                .days(Optional.ofNullable(this.days).orElse(7))
                .blockReason(Optional.ofNullable(this.blockReason).orElse("부적절한 이용으로 차단되었습니다."))
                .build();
    }
}
