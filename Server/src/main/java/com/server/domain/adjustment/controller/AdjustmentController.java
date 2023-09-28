package com.server.domain.adjustment.controller;

import com.server.domain.adjustment.controller.dto.request.AccountUpdateApiRequest;
import com.server.domain.adjustment.service.AdjustmentService;
import com.server.domain.adjustment.service.dto.response.AccountResponse;
import com.server.domain.adjustment.service.dto.response.ToTalAdjustmentResponse;
import com.server.domain.adjustment.service.dto.response.VideoAdjustmentResponse;
import com.server.domain.order.controller.dto.request.AdjustmentSort;
import com.server.domain.adjustment.service.dto.response.AdjustmentResponse;
import com.server.global.annotation.LoginId;
import com.server.global.exception.businessexception.orderexception.AdjustmentDateException;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/adjustments")
@Validated
public class AdjustmentController {

    private final AdjustmentService adjustmentService;

    public AdjustmentController(AdjustmentService adjustmentService) {
        this.adjustmentService = adjustmentService;
    }

    @GetMapping
    public ResponseEntity<ApiPageResponse<AdjustmentResponse>> adjustment(
            @RequestParam(defaultValue = "1") @Positive(message = "{validation.positive}") int page,
            @RequestParam(defaultValue = "10") @Positive(message = "{validation.positive}") int size,
            @RequestParam(required = false) @Min(value = 1) @Max(value = 12) Integer month,
            @RequestParam(required = false) @Min(value = 2020) Integer year,
            @RequestParam(defaultValue = "video-created-date") AdjustmentSort sort,
            @LoginId Long loginMemberId) {

        checkValidDate(month, year);

        Page<AdjustmentResponse> response = adjustmentService.adjustment(loginMemberId, page - 1, size, month, year, sort.getSort());

        return ResponseEntity.ok(ApiPageResponse.ok(response, getAdjustmentMessage(month, year) + " 정산 내역"));
    }

    @GetMapping("/videos")
    public ResponseEntity<ApiSingleResponse<List<VideoAdjustmentResponse>>> calculateVideoRate(
            @RequestParam(required = false) @Min(value = 1) @Max(value = 12) Integer month,
            @RequestParam(required = false) @Min(value = 2020) Integer year,
            @LoginId Long loginMemberId) {

        checkValidDate(month, year);

        List<VideoAdjustmentResponse> total = adjustmentService.calculateVideoRate(loginMemberId, month, year);

        return ResponseEntity.ok(ApiSingleResponse.ok(total, getAdjustmentMessage(month, year) + " 비디오 정산 내역"));
    }

    @GetMapping("/total-adjustment")
    public ResponseEntity<ApiSingleResponse<ToTalAdjustmentResponse>> calculateAmount(
            @RequestParam(required = false) @Min(value = 1) @Max(value = 12) Integer month,
            @RequestParam(required = false) @Min(value = 2020) Integer year,
            @LoginId Long loginMemberId) {

        checkValidDate(month, year);

        ToTalAdjustmentResponse total = adjustmentService.totalAdjustment(loginMemberId, month, year);

        return ResponseEntity.ok(ApiSingleResponse.ok(total, getAdjustmentMessage(month, year) + " 정산 내역"));
    }

    @GetMapping("/account")
    public ResponseEntity<ApiSingleResponse<AccountResponse>> getAccount(
            @LoginId Long loginMemberId) {

        AccountResponse account = adjustmentService.getAccount(loginMemberId);

        String message = account.getName().equals("계좌 정보가 없습니다.") ? "계좌 정보가 없습니다." : "계좌 정보 조회 성공";

        return ResponseEntity.ok(ApiSingleResponse.ok(account, message));
    }

    @PutMapping("/account")
    public ResponseEntity<ApiSingleResponse<Void>> updateAccount(
            @RequestBody @Valid AccountUpdateApiRequest request,
            @LoginId Long loginMemberId) {

        adjustmentService.updateAccount(loginMemberId, request.toServiceRequest());

        return ResponseEntity.noContent().build();
    }

    private void checkValidDate(Integer month, Integer year) {
        if(month != null && year == null) {
            throw new AdjustmentDateException();
        }
    }

    private String getAdjustmentMessage(Integer month, Integer year) {
        if(month == null && year == null) {
            return "전체";
        }

        if(month != null && year != null) {
            return year + "년 " + month + "월";
        }

        return year + "년";
    }
}
