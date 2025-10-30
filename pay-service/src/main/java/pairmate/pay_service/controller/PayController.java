package pairmate.pay_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pairmate.common_libs.response.ApiResponse;
import pairmate.common_libs.response.SuccessCode;
import pairmate.pay_service.dto.PayDTO;
import pairmate.pay_service.service.PayService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pay")
public class PayController {

    private final PayService payService;

    /**
     * 결제 요청
     */
    @PostMapping("/register")
    public ApiResponse<Void> registerPayment(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody PayDTO.PayRequestDTO request
    ) {
        payService.registerPayment(userId, request);
        return ApiResponse.onSuccess(null, SuccessCode.OK);
    }

    /**
     * 오늘 결제 금액 조회
     */
    @GetMapping("/today")
    public ApiResponse<Integer> getTodayPayment(
            @RequestHeader("X-User-Id") Long userId
    ) {
        int todayTotal = payService.getTodayPayment(userId);
        return ApiResponse.onSuccess(todayTotal, SuccessCode.OK);
    }

    /**
     * 이번 달 결제 금액 조회
     */
    @GetMapping("/month")
    public ApiResponse<Integer> getMonthlyPayment(
            @RequestHeader("X-User-Id") Long userId
    ) {
        int monthlyTotal = payService.getMonthlyPayment(userId);
        return ApiResponse.onSuccess(monthlyTotal, SuccessCode.OK);
    }

}
